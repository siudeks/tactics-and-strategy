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
- Battlefield representation uses a hex-based map.
- Terrain categories must influence movement and engagement context.
- Strategic control points must be represented as objective-capable map nodes.
- Scenario files must define objective-relevant map sectors.

## Units and Roles
- Units are military formations, not single-character entities.
- Each unit has role attributes (combat effectiveness, mobility, command relation).
- Unit state must support at least: active, disrupted, eliminated.
- Unit data must be scenario-configurable.

## Supply and Stacking
- Supply status is tracked per unit or formation group.
- Supply disruption impacts operational effectiveness.
- Stacking constraints limit concentration of units on a single hex.
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

## Confidence Levels by Section
- High confidence: gameplay loop, scenario objective model, map/control-point concept.
- Medium confidence: supply behavior details, stacking granularity, full HUD composition.
- Low confidence: exact combat math and modifier hierarchy (pending deeper source validation).

## Open Questions Backlog
### High priority
- Exact combat resolution model and balancing levers.
- Full terrain-to-movement impact table.
- Final supply disruption penalty model.

### Medium priority
- AI behavior profiles by scenario stage.
- Campaign difficulty ramp and scenario sequencing rules.

### Low priority
- Extended quality-of-life overlays and optional analytics views.

## Example Invocations (REQ Workflow)
- `/Implement Requirement From Plan REQ-MOVE-001`
- `/Implement Requirement From Plan REQ-MOVE-001, only core + unit tests`
- `/Implement Requirement From Plan REQ-MOVE-001, include full documentation and traceability update`

## Non-Goals (Current Stage)
- No gameplay code implementation.
- No direct reuse of legacy copyrighted graphics or audio.
- No final balance lock.
