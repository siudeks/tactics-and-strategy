# Turn Semantics v1 (Normative)

## 1. Purpose
This document specifies a formal state transition for one engine turn over `CampaignState`.

## 2. Transition Signature
Given:
- `S_in: CampaignState`
- `E: TurnEngine` created with `ScenarioDefinition D` and `DeterministicContext C`

Execution:
- `R = E.runOneTurn(S_in)`

Returns:
- `R.state = S_out`
- `R.phaseTrace = P`

## 3. Preconditions
1. `S_in` MUST be non-null.
2. `D` MUST be non-null.
3. `C` MUST be non-null.
4. `S_in.activeSide` SHOULD be either `ALLIES` or `AXIS`.

## 4. Ordered Phase Semantics
## 4.1 ISSUE_ORDERS
- Current behavior: no-op.
- Contract: phase marker MUST be appended to `P`.

## 4.2 SIMULTANEOUS_MOVE
- Input orders are read from `S_in.pendingOrders`.
- For each order where `type == MOVE`, engine computes unit target by `unitId`.
- For each unit in `S_in.units`:
  - If a valid MOVE target exists for that `unit.id`, output unit coordinates become target `(x, y)`.
  - Otherwise, output unit coordinates remain unchanged.
- Contract: phase marker MUST be appended to `P`.

### 4.2.1 Valid MOVE Target Predicate
`isValidMove(x, y)` is true iff:
- `x >= 0`
- `y >= 0`
- `x < D.mapWidth`
- `y < D.mapHeight`
- `D.defaultTerrain != VOID`
- `D.defaultTerrain != WATER`

If false, move MUST be ignored without exception.

## 4.3 COMBAT
- Current behavior: no-op.
- Contract: phase marker MUST be appended to `P`.

## 4.4 RETREAT
- Current behavior: no-op.
- Contract: phase marker MUST be appended to `P`.

## 4.5 END_TURN
Let `flip(side)` be defined as:
- `flip(ALLIES) = AXIS`
- `flip(AXIS) = ALLIES`
- `flip(NEUTRAL)` raises `IllegalStateException`

Then output state MUST be:
- `S_out.campaignId = S_in.campaignId`
- `S_out.scenarioId = S_in.scenarioId`
- `S_out.turnNumber = S_in.turnNumber + 1`
- `S_out.activeSide = flip(S_in.activeSide)`
- `S_out.units = units_after_movement`
- `S_out.pendingOrders = []`

Contract: phase marker MUST be appended to `P`.

## 5. Postconditions
1. `P` MUST equal `[ISSUE_ORDERS, SIMULTANEOUS_MOVE, COMBAT, RETREAT, END_TURN]`.
2. `S_out.turnNumber = S_in.turnNumber + 1`.
3. `S_out.pendingOrders` MUST be empty.
4. `|S_out.units| = |S_in.units|`.

## 6. Failure Semantics
1. Invalid MOVE coordinates MUST NOT raise exception and MUST result in unchanged unit coordinates.
2. Side flip on `NEUTRAL` MUST raise `IllegalStateException`.

## 7. Non-Normative Notes
- This specification does not define future combat, retreat, or supply logic.
- These phases are represented for forward-compatible turn trace shape.
