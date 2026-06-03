# Game Requirements Plan

## Scope
This document defines functional requirements for a turn-based tactical strategy game inspired by classic North Africa campaign design patterns.

## Product Goals
- Deliver a readable and tactical turn-based battlefield experience.
- Keep scenario objectives explicit and measurable.
- Preserve historical-campaign flavor while using original production assets.

## Core Gameplay Loop
1. Load scenario and briefing.
2. Execute player turn actions (movement, positioning, engagement decisions).
3. Execute opposing side turn.
4. Resolve combat outcomes and logistics state.
5. Check mission objectives and failure conditions.
6. Continue to next turn or finish scenario.

## Turn Structure
- Turn order is side-based (Player Side then Opponent Side).
- Each turn includes at least:
  - Command phase
  - Movement phase
  - Engagement/combat phase
  - End-of-turn validation phase (objectives, supply continuity, control points)
- End condition checks run after each full turn cycle.

## Map and Terrain
- Battlefield representation uses a square-tile map.
- Terrain categories must influence movement and engagement context.
- Strategic control points must be represented as objective-capable map nodes.
- Scenario files must define objective-relevant map sectors.

## Units and Roles
- Units are military formations, not single-character entities.
- Each unit has role attributes (combat effectiveness, mobility, command relation).
- Unit state must support at least: active, disrupted, eliminated.
- Unit data must be scenario-configurable.

## Movement Phase Details (Planned)
### Scope
- Define how movement orders are validated and resolved during the Movement phase.
- Keep compatibility with existing turn structure and scenario-driven data model.

### Planned Requirement Candidates
- REQ-MOVE-001: The engine shall enforce per-unit movement allowance during the Movement phase.
- REQ-MOVE-002: The engine shall use terrain-cost-aware route resolution with deterministic tie-breaking.
- REQ-MOVE-003: The engine shall enforce occupancy and collision rules for simultaneous movement.

### Draft Acceptance Criteria
- AC-MOVE-001 (Range Within Allowance):
  Given a unit with movement allowance N and a valid target reachable with total movement cost <= N,
  when the turn is resolved,
  then the unit shall end the turn on the target tile.
- AC-MOVE-002 (Range Exceeded):
  Given a unit with movement allowance N and an assigned target requiring total movement cost > N,
  when the turn is resolved,
  then the order shall be rejected and the unit shall remain on its original tile.
- AC-MOVE-003 (Impassable Terrain):
  Given a planned route that crosses an impassable tile type,
  when the turn is resolved,
  then the route shall be treated as invalid and the unit shall remain in place.
- AC-MOVE-004 (Deterministic Path Tie-Break):
  Given at least two equal-cost valid paths to the same target,
  when the same initial state is resolved multiple times,
  then the engine shall pick the same path and produce identical final unit positions.
- AC-MOVE-005 (Conflicting Destination):
  Given two units with valid orders that would end on the same destination tile,
  when the turn is resolved,
  then the conflict shall be resolved by the defined collision policy and the resulting board state shall satisfy occupancy constraints.
- AC-MOVE-006 (Simultaneous Multi-Unit Resolution):
  Given multiple units with independent valid movement orders,
  when the turn is resolved,
  then each unit outcome shall be computed without side effects from processing order beyond the declared tie-break and collision rules.
- AC-MOVE-007 (Order Consumption):
  Given movement orders present in pending orders at turn start,
  when End Turn is executed and the Movement phase completes,
  then processed movement orders shall be consumed and not re-applied in the next turn unless reassigned.

### Key Decisions Required
- Mobility model per unit role (tile allowance, points-based, or hybrid).
- Terrain movement cost table and impassable rules.
- Collision and tie-break policy when two or more units target conflicting destinations.

### Out of Scope for This Iteration
- Visual movement animation polish.
- Full AI movement optimization.
- Combat-retreat interaction redesign.

## Supply and Stacking
- Supply status is tracked per unit or formation group.
- Supply disruption impacts operational effectiveness.
- Stacking constraints limit concentration of units on a single square tile.
- Loss of supply continuity can trigger scenario penalties.

## Combat System
### Confirmed requirements
- Combat resolution depends on tactical context (position, terrain, and unit state).
- Outcomes update unit operational state.
- Combat outcome contributes to objective progress.

### Open design points
- Exact resolution model (table-driven, probability-based, or hybrid).
- Full modifier set and weighting rules.
- Edge cases for retreat, regroup, and forced displacement.

## Scenarios and Victory Conditions
- Every scenario must include:
  - Primary objectives
  - Optional secondary objectives
  - Explicit failure conditions
  - Turn/time constraints where applicable
- Campaign progression may chain scenarios with cumulative impact.

## UI/HUD and Input
- HUD must display:
  - Current turn and active side
  - Objective state
  - Unit operational status
  - Supply/logistics status
- Input must support fast tactical decisions with clear state feedback.
- Objective and unit-state readability has higher priority than decorative UI.

## Phase Visualization Decisions (Plan 20260531-phase-visualization)
- Canonical implemented requirement IDs for this decision set are `REQ-UI-PHASE-001` and `REQ-UI-LOCK-001` (normative definitions are maintained in `game-requirements-functional.md`).
- UI-only phase timing is used; no engine-level delays are introduced.
- Overlay order is sourced from the live runtime phase sequence as the active turn-execution session advances.
- Overlay display duration is uniform at 3 seconds for every phase, including not-yet-implemented logic phases.
- Movement playback keeps map drag/zoom enabled while click, selection, shortcuts, HUD commands, and End Turn remain locked.
- Duration and phase labels are centralized as constants.

### Consistency Notes (Implemented)
- `REQ-UI-PHASE-001`: End Turn begins a live runtime turn-execution session; the UI advances that session one phase at a time and renders a dimmed phase overlay sequence for each phase notification, 3.0 seconds per phase including `RETREAT`.
- `REQ-UI-LOCK-001`: During phase playback, notifications/commit steps lock map input paths and key shortcuts, while movement playback keeps drag/zoom available but still blocks click, selection, shortcuts, HUD commands, and repeated End Turn requests until playback completion.

## Confidence Levels by Section
- High confidence: gameplay loop, scenario objective model, map/control-point concept.
- Medium confidence: supply behavior details, stacking granularity, full HUD composition.
- Low confidence: exact combat math and modifier hierarchy (pending deeper source validation).

## Open Questions Backlog
### High priority
- Exact combat resolution model and balancing levers.
- Movement allowance model by unit role/class.
- Terrain movement cost table and deterministic path/tie-break rules.
- Collision/stacking behavior for conflicting movement orders.
- Final supply disruption penalty model.

### Medium priority
- AI behavior profiles by scenario stage.
- Campaign difficulty ramp and scenario sequencing rules.

### Low priority
- Extended quality-of-life overlays and optional analytics views.

## Example Invocations (REQ Workflow)
- `/Implement Requirement From Plan REQ-MOVE-001`
- `/Implement Requirement From Plan REQ-MOVE-001, only core + unit tests`
- `/Implement Requirement From Plan REQ-MOVE-002, include terrain cost model + tests`
- `/Implement Requirement From Plan REQ-MOVE-003, include documentation and traceability update`

## Non-Goals (Current Stage)
- No gameplay code implementation.
- No direct reuse of legacy copyrighted graphics or audio.
- No final balance lock.
