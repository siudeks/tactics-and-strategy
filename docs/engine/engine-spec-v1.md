# Engine Mechanics Specification v1 (Normative)

## 1. Purpose
This document defines the normative source of truth for currently implemented game-engine mechanics in the `core` module.

Normative keywords `MUST`, `MUST NOT`, `SHOULD`, and `MAY` are used as defined in RFC-style requirement language.

## 2. Scope
### 2.1 In Scope
The following mechanics are in scope and normative in v1:
- Turn execution entry point and result contract.
- Turn phase order and phase trace.
- MOVE and HOLD order runtime behavior.
- Movement validity checks against map bounds and terrain passability.
- End-turn state transition (turn increment, side flip, order clearing).
- Scenario loading runtime contract and unit coordinate validation.
- Deterministic context and canonical snapshot comparison.

### 2.2 Out of Scope (Non-Normative in v1)
The following are not currently implemented and are therefore not normative in this document:
- Combat resolution logic beyond phase placeholder.
- Retreat resolution logic beyond phase placeholder.
- Supply mechanics and penalties.
- Control points and objective evaluation.
- Victory/failure rule engine.
- Stacking limit enforcement.
- Additional order types beyond `MOVE` and `HOLD`.

## 3. Canonical Symbols
### 3.1 Domain State
- `CampaignState(campaignId, scenarioId, turnNumber, activeSide, units, pendingOrders)`
- `Unit(id, side, type, size, tileX, tileY)`
- `Order(id, unitId, side, type, targetX, targetY)`
- `ScenarioDefinition(id, name, mapWidth, mapHeight, defaultTerrain, units)`

### 3.2 Engine Runtime
- `TurnEngine.fixedContext(DeterministicContext, ScenarioDefinition)`
- `TurnEngine.runOneTurn(CampaignState): TurnResult`
- `TurnResult(state, phaseTrace, seed, timeMillis, canonicalSnapshot)`
- `TurnPhase = ISSUE_ORDERS, SIMULTANEOUS_MOVE, COMBAT, RETREAT, END_TURN`

### 3.3 Enumerations Actively Used by Implemented Behavior
- `OrderType = MOVE, HOLD`
- `TerrainType = VOID, SAND, MOUNTAIN, WATER`
- `Side = ALLIES, AXIS, NEUTRAL`

## 4. Normative Engine Rules
### 4.1 Turn Entry and Output
1. `runOneTurn` MUST accept a non-null `CampaignState`.
2. `runOneTurn` MUST return a non-null `TurnResult` with non-null `state`, `phaseTrace`, and `canonicalSnapshot`.
3. `TurnResult.phaseTrace` MUST be immutable from caller perspective.

### 4.2 Phase Ordering
1. A single turn MUST execute phases in this exact order:
   1. `ISSUE_ORDERS`
   2. `SIMULTANEOUS_MOVE`
   3. `COMBAT`
   4. `RETREAT`
   5. `END_TURN`
2. `phaseTrace` MUST preserve this order exactly.

### 4.3 Orders and Movement
1. Only orders with `type == MOVE` MUST change unit position.
2. `HOLD` orders MUST NOT change unit position.
3. MOVE target resolution MUST be keyed by `Order.unitId`.
4. If a MOVE target is invalid, movement MUST be ignored silently and the unit MUST remain at previous coordinates.
5. Validity of move target MUST require:
   - `0 <= x < mapWidth`
   - `0 <= y < mapHeight`
   - `defaultTerrain` is not `VOID`
   - `defaultTerrain` is not `WATER`

### 4.4 End-Turn Transition
At `END_TURN`, engine output state MUST satisfy all conditions below:
1. `turnNumber` increments by exactly `1`.
2. `activeSide` flips:
   - `ALLIES -> AXIS`
   - `AXIS -> ALLIES`
3. `pendingOrders` is cleared to an empty list.
4. `units` list cardinality remains unchanged by end-turn mechanics.

### 4.5 Side Flip Constraint
1. `NEUTRAL` MUST NOT be used as an active side for side flip.
2. If side flip receives `NEUTRAL`, engine MUST fail with `IllegalStateException`.

### 4.6 Placeholders with Explicit Current Behavior
1. `COMBAT` phase MUST be represented in `phaseTrace` and currently performs no unit-state mutation.
2. `RETREAT` phase MUST be represented in `phaseTrace` and currently performs no unit-state mutation.

## 5. State Invariants
After every successful `runOneTurn`, the following invariants MUST hold:
1. `CampaignState.units` remains non-null and immutable from caller perspective.
2. `CampaignState.pendingOrders` remains non-null and immutable from caller perspective.
3. `canonicalSnapshot` is non-empty.

## 6. Evidence Baseline
Behavior in this document is evidenced by:
- `TurnEngineOrderTest`
- `OneTurnSimulationTest`
- `EngineDeterminismSmokeTest`
- `CampaignInitializationTest`
- `ScenarioLoaderTest`

See `traceability-matrix-v1.md` for direct mapping.
