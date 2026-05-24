# Game Requirements v1 (Implemented Baseline)

## Scope
This document defines functional requirements that reflect the currently implemented game behavior in this repository.

## Product Goals (v1)
- Provide a deterministic, turn-based tactical loop over scenario data.
- Ensure scenario loading and turn simulation are stable and testable.
- Provide a playable baseline UI with map rendering, side/turn status, and unit selection.

## Core Gameplay Loop
1. Load scenario and initial campaign state from JSON resources.
2. Run one full turn through engine phases.
3. Apply movement orders and validate movement bounds/terrain passability.
4. End turn by flipping active side, incrementing turn number, and clearing pending orders.
5. Expose updated state through runtime for subsequent turns.

## Turn Structure
The engine executes a fixed five-phase sequence:
1. ISSUE_ORDERS
2. SIMULTANEOUS_MOVE
3. COMBAT
4. RETREAT
5. END_TURN

v1 behavior notes:
- ISSUE_ORDERS is currently a structural phase (orders are read from campaign state).
- COMBAT and RETREAT are currently structural placeholders (no domain resolution yet).
- END_TURN always increments turn number by 1, flips side ALLIES <-> AXIS, and clears pending orders.
- NEUTRAL is not a valid active turn side for side flipping.

## Map and Terrain
- Battlefield model uses hex-tile coordinates from scenario definitions.
- Scenario map defines width, height, and default terrain type.
- Movement validation enforces map bounds.
- Movement into impassable terrain is blocked for default terrain VOID and WATER.
- Invalid movement orders are ignored silently (unit remains in place).

## Units and Orders
- Units are modeled as military formations.
- Unit identity and placement are represented by immutable domain records.
- Order model currently supports two order types only: MOVE and HOLD.
- Unit and order data are loaded from scenario/campaign JSON.
- v1 does not implement operational unit states (active/disrupted/eliminated) in runtime logic.

## Scenario Loading and Initialization
- Scenario resources are loaded from bundled JSON files.
- Scenario index listing is supported through scenarios-index.json.
- Loader parses:
  - Scenario metadata (id, name)
  - Map definition (width, height, defaultTerrain)
  - Unit list
  - Campaign state (campaignId, scenarioId, turnNumber, activeSide, pendingOrders)
- Loader validates unit coordinates against scenario bounds and rejects out-of-range units.

## Determinism and Turn Result Contract
- Runtime uses a deterministic engine context with fixed seed support.
- Turn simulation produces a canonical snapshot string for semantic comparison.
- Semantic equivalence is defined by state equality, phase trace equality, and canonical snapshot equality.

## Runtime and UI Baseline
- Runtime supports one-turn simulation and persistent state update after each turn.
- UI baseline includes:
  - Battlefield map panel rendering
  - Unit icon rendering on map
  - Unit selection behavior
  - Status bar with scenario name, turn number, and active side
  - End turn action wired to runtime turn simulation

## Non-Goals / Not In v1 Scope
The following areas are intentionally out of scope for this implemented baseline:
- Combat resolution mechanics beyond structural phase placeholder.
- Retreat/regroup mechanics beyond structural phase placeholder.
- Supply continuity model and penalties.
- Stacking limit enforcement per hex.
- Objective runtime model (primary/secondary objective evaluation).
- Failure condition runtime model.
- Control point ownership system.
- Campaign chaining with cumulative scenario effects.

## Traceability Note
For executable evidence mapping (tests and code anchors), see docs/engine/traceability-matrix-v1.md.
