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
- Input orders are read from the current in-session `pendingOrders`.
- For each order where `type == MOVE`, engine computes unit target by `unitId`.
- For each unit in the in-session `units` list:
  - If a valid MOVE target exists for that `unit.id`, output unit coordinates become target `(x, y)`.
  - Otherwise, output unit coordinates remain unchanged.
- `PhaseStepResult.movementPlayback()` MUST describe the per-unit move/skip outcome for this phase.

#### 4.2.1 Valid MOVE Target Predicate
`isValidMove(x, y)` is true iff:
- `x >= 0`
- `y >= 0`
- `x < D.mapWidth`
- `y < D.mapHeight`
- `D.defaultTerrain != VOID`
- `D.defaultTerrain != WATER`

If false, move MUST be ignored without exception.

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
- `GameRuntimeTurnSessionTest.beginTurnExecution_advancesStepwise_andCommitsOnlyAfterEndTurn`
- `BattlefieldPhasePlaybackControllerTest.advance_sequencesNotificationsMovementPlaybackAndTurnCompletion`

## 8. Non-Normative Notes
- Combat, retreat, and supply logic remain placeholders, but their phases are already part of the stable execution trace and playback sequence.
- The UI does not animate from a post-simulation overlay snapshot anymore; it renders a live runtime session and choreographs overlays/movement playback around each engine phase step.
