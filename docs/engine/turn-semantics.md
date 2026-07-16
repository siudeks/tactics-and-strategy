# Turn Semantics v1 (Normative)

## 1. Purpose
This document specifies the implemented turn-execution model over `CampaignState`, including the pure engine phase transitions, the live runtime session API, and the UI playback choreography that consumes phase-step results.

## 2. Execution Model
### 2.1 Engine Turn Function
Given:
- `S_in: CampaignState`
- `E: TurnEngine` created with `ScenarioDefinition D` and `DeterministicContext C`

The engine exposes two equivalent execution forms:
- Monolithic execution: `R = E.runOneTurn(S_in)`
- Stepwise execution: `X = E.beginExecution(S_in)` followed by repeated `advance()` calls until `X.isComplete()`.

Both forms MUST produce the same terminal `TurnResult` semantics.

### 2.2 Runtime Session Orchestration
`GameRuntime` wraps the engine with a live turn session:
- `beginTurnExecution()` opens a `TurnExecutionSession` against the current runtime `CampaignState`.
- `advanceTurnExecution()` advances exactly one phase and returns `PhaseStepResult`.
- Before the final `END_TURN` step completes, `GameRuntime` MUST continue exposing the in-session transient state through `getCurrentCampaignState()`.
- The durable runtime campaign state MUST be committed only when the returned `PhaseStepResult.completedTurnResult()` is present.

### 2.3 UI Playback Contract
The battlefield UI consumes `PhaseStepResult` without adding engine-side delays:
- phase notifications are rendered from the current runtime phase trace,
- `SIMULTANEOUS_MOVE` playback is rendered from `PhaseStepResult.movementPlayback()`,
- HUD/map lock policy is controlled by `InteractionLockState`,
- camera drag/zoom remain available only during `MOVEMENT_PLAYBACK`.

### 2.4 Runtime MOVE Command Contract
`GameRuntime.assignMoveTarget(unitId, x, y)` MUST return a `MoveCommandResult` with one of the following outcomes:
- `ACCEPTED` when a new MOVE target is persisted for a known unit.
- `REPLACED_EXISTING` when a known unit already had a MOVE order and the target is replaced.
- `UNKNOWN_UNIT` when the unit id does not exist in the current runtime state.

MOVE persistence MUST be materialized through `OrderBook`, which enforces:
- at most one active `MOVE` per `unitId`,
- deterministic overwrite policy `last-write-wins` for repeated assignments of the same unit.

Validation of target coordinates remains in `SIMULTANEOUS_MOVE` (`isValidMove`); invalid targets are persisted as order intent and ignored during movement resolution.

## 3. Preconditions
1. `S_in` MUST be non-null.
2. `D` MUST be non-null.
3. `C` MUST be non-null.
4. `S_in.activeSide` SHOULD be either `ALLIES` or `AXIS`.

## 4. Ordered Phase Semantics
Every phase step returns a `PhaseExecution` whose state becomes the next in-session `CampaignState`. The phase marker MUST be appended to the eventual phase trace in execution order.

### 4.1 ISSUE_ORDERS
- Current behavior: no-op.
- Session effect: the in-session state remains unchanged.

### 4.2 SIMULTANEOUS_MOVE
- Input orders are read from the current in-session `pendingOrders` through `OrderBook.activeMoveOrdersByUnit()`.
- `OrderBook` guarantees a deterministic single active `MOVE` per unit (`last-write-wins` if duplicates exist).
- The engine resolves valid MOVE candidates first, then applies deterministic destination arbitration and occupancy checks.
- Stacking policy (`REQ-STACK-001`) in movement phase:
  - maximum `1` unit per tile after movement resolution,
  - each destination tile accepts at most one MOVE winner,
  - contenders for the same destination are ordered by collision tie-break and only the highest-priority candidate is considered.
- A chosen MOVE candidate succeeds only if every incumbent currently on its destination tile also vacates successfully in the same phase; otherwise it is skipped.
- Units without a valid route, units that lose destination arbitration, and units blocked by incumbents remain on their current tile.
- `PhaseStepResult.movementPlayback()` MUST describe the per-unit move/skip outcome for this phase.

#### 4.2.1 Deterministic Tie-Break
When multiple routes have identical total terrain cost, the resolver MUST pick a deterministic winner by frontier ordering:
- lower total route cost first,
- then lower `y`,
- then lower `x`.

This keeps route choice stable across repeated executions with identical inputs.

#### 4.2.2 Terrain-Cost Model
Movement cost per orthogonal tile step is derived from scenario default terrain:
- `SAND` => cost `1`
- `MOUNTAIN` => cost `3`
- `VOID`, `WATER` => impassable (no valid route)

#### 4.2.3 Valid MOVE Target Predicate
`isValidMove(x, y)` is true iff:
- `x >= 0`
- `y >= 0`
- `x < D.mapWidth`
- `y < D.mapHeight`
- `D.defaultTerrain != VOID`
- `D.defaultTerrain != WATER`

If false, move MUST be ignored without exception.

#### 4.2.4 Destination Collision Tie-Break (REQ-STACK-001)
When two or more units contend for the same destination tile, winner selection MUST be deterministic and independent of collection iteration order.

For a single destination tile, contenders are ordered by:
- lower resolved route total cost,
- then lower source tile `y`,
- then lower source tile `x`,
- then lexicographically lower `unit.id`.

With v1 stack limit `1`, only the first contender in this order can win the tile.

### 4.3 COMBAT
- Current behavior: no-op.
- Session effect: the in-session state remains unchanged.

### 4.4 RETREAT
- Current behavior: no-op.
- Session effect: the in-session state remains unchanged.

### 4.5 END_TURN
Let `flip(side)` be defined as:
- `flip(ALLIES) = AXIS`
- `flip(AXIS) = ALLIES`
- `flip(NEUTRAL)` raises `IllegalStateException`

The completed turn result state MUST be:
- `S_out.campaignId = S_in.campaignId`
- `S_out.scenarioId = S_in.scenarioId`
- `S_out.turnNumber = S_in.turnNumber + 1`
- `S_out.activeSide = flip(S_in.activeSide)`
- `S_out.units = units_after_movement`
- `S_out.pendingOrders = []`

Only after this step completes may `GameRuntime` replace its durable stored campaign state with `S_out`.

## 5. Postconditions
1. The completed phase trace `P` MUST equal `[ISSUE_ORDERS, SIMULTANEOUS_MOVE, COMBAT, RETREAT, END_TURN]`.
2. `S_out.turnNumber = S_in.turnNumber + 1`.
3. `S_out.pendingOrders` MUST be empty.
4. `|S_out.units| = |S_in.units|`.
5. Stepwise execution and monolithic execution MUST be semantically equivalent.

## 6. Failure Semantics
1. Invalid MOVE coordinates MUST NOT raise exception and MUST result in unchanged unit coordinates.
2. Side flip on `NEUTRAL` MUST raise `IllegalStateException`.
3. Starting a new runtime turn session while another incomplete session is active MUST raise `IllegalStateException`.

## 7. Evidence
- `OneTurnSimulationTest.oneTurn_phaseTraceContainsAllFivePhases`
- `OneTurnSimulationTest.oneTurn_stepwiseSessionMatchesMonolithicRun`
- `OneTurnSimulationTest.oneTurn_contestedDestination_isDeterministicAcrossRepeatedRuns`
- `TurnEngineOrderTest.moveOrders_twoUnitsContendForSameDestination_onlyDeterministicWinnerMoves`
- `TurnEngineOrderTest.moveOrders_threeUnitsContendForSameDestination_lowestCostWinnerMoves`
- `GameRuntimeTurnSessionTest.beginTurnExecution_advancesStepwise_andCommitsOnlyAfterEndTurn`
- `BattlefieldPhasePlaybackControllerTest.advance_sequencesNotificationsMovementPlaybackAndTurnCompletion`

## 8. Non-Normative Notes
- Combat, retreat, and supply logic remain placeholders, but their phases are already part of the stable execution trace and playback sequence.
- The UI does not animate from a post-simulation overlay snapshot anymore; it renders a live runtime session and choreographs overlays/movement playback around each engine phase step.
