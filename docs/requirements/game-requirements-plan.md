# Game Requirements Plan (Future Backlog Only)

## Scope
This document tracks only approved requirements that are not implemented yet.
Implemented baseline behavior is maintained in `game-requirements-functional.md`,
`game-requirements-non-functional.md`, and `../engine/traceability-matrix.md`.

## Removed from Active Plan (Already Implemented)
The following requirement groups were removed from this planning backlog because
they are already implemented and covered by executable evidence in the traceability matrix.

- REQ-ARCH-001, REQ-ARCH-002, REQ-ARCH-003 (libGDX dependency boundary and platform split)
- REQ-UI-PHASE-001, REQ-UI-LOCK-001 (phase overlay playback and interaction lock policy)
- REQ-ORD-MOVE-001, REQ-ORD-MOVE-002, REQ-ORD-MOVE-003 (MOVE target persistence and consumption)
- REQ-RTS-001, REQ-RTS-002, REQ-RTS-003 (real-time clock and always-available movement commands)
- REQ-MOVE-002 (terrain-cost-aware movement route resolution with deterministic tie-break)
- REQ-STACK-001 (tile-level stacking constraints and deterministic contested-destination policy)

## Planned Requirement Candidates

### Movement Rules v2 (Partially Implemented)
- REQ-MOVE-001: The engine shall enforce per-unit movement allowance.
- REQ-MOVE-003: The engine shall enforce occupancy and collision rules for conflicting destinations.

Already implemented in current baseline:
- REQ-MOVE-002: The engine resolves terrain-cost-aware orthogonal routes with deterministic tie-breaking.

Draft acceptance criteria:
- AC-MOVE-001 (Within allowance):
  Given allowance N and route cost <= N,
  when movement resolves,
  then the unit reaches the target tile.
- AC-MOVE-002 (Exceeded allowance):
  Given allowance N and route cost > N,
  when movement resolves,
  then the order is rejected and the unit remains in place.
- AC-MOVE-003 (Impassable route segment):
  Given a route crossing impassable terrain,
  when movement resolves,
  then the route is invalid and no movement is applied.
- AC-MOVE-004 (Deterministic tie-break):
  Given equal-cost paths,
  when the same state is resolved repeatedly,
  then path choice and final positions are identical across runs.
- AC-MOVE-005 (Conflicting destination):
  Given multiple units targeting one tile,
  when movement resolves,
  then the collision policy is applied deterministically and occupancy constraints hold.
- AC-MOVE-006 (Multi-unit determinism):
  Given multiple valid movement orders,
  when movement resolves,
  then outcomes are deterministic and independent of iteration order except declared tie-break policy.

Key decisions required:
- Movement allowance model per unit role.
- Collision policy for conflicting destinations.

### Supply and Stacking (Not Implemented)
- REQ-SUP-001: Supply continuity must be tracked and penalties applied when disrupted.


### Combat Resolution (Partial/Not Implemented)
- REQ-CBT-001: Combat phase must resolve contextual outcomes using terrain, position, and unit state.

Open design points:
- Resolution model (table-driven, probabilistic, or hybrid).
- Modifier system and weighting.
- Retreat/regroup/forced-displacement handling.

### Scenario Objectives Runtime (Partial/Not Implemented)
- REQ-SCEN-001: Scenario runtime must evaluate primary objectives, optional objectives, and explicit failure conditions.

### Non-Functional Future Backlog
- NFR-USAB-004: Destination preview animation must stay visually consistent with selected-unit highlighting while remaining independently adjustable for future UI revisions.
- NFR-DET-004: Target assignment and movement-phase consumption must remain deterministic for identical inputs.

Covered by current implemented behavior (not pending):

- NFR-USAB-005: Invalid terrain must not display destination preview.

## Open Questions Backlog

High priority:
- Final combat resolution model and balancing levers.
- Movement allowance finalization.
- Supply disruption penalties and recovery rules.

Medium priority:
- AI behavior profiles by scenario stage.
- Campaign difficulty ramp and scenario sequencing rules.

Low priority:
- Optional quality-of-life overlays and analytics views.

## Example Invocations (REQ Workflow)
- `/Implement Requirement From Plan REQ-MOVE-001`
- `/Implement Requirement From Plan REQ-MOVE-003, include documentation and traceability update`
- `/Implement Requirement From Plan REQ-SUP-001, include engine tests and docs updates`

## Non-Goals (Current Planning Window)
- No direct reuse of legacy copyrighted graphics or audio.
- No final balance lock.
