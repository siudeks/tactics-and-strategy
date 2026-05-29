# Game Functional Requirements v1 (Implemented Baseline)

## Scope
This document defines functional requirements that reflect currently implemented game behavior.

## Product Functional Goals (v1)
- Provide a turn-based tactical gameplay loop over scenario data.
- Provide a playable baseline UI with map rendering, side/turn status, and unit selection.

## Core Gameplay Loop
1. Load scenario and initial campaign state from JSON resources.
2. Run one full turn through engine phases.
3. Apply movement orders and validate movement bounds and terrain passability.
4. End turn by flipping active side, incrementing turn number, and clearing pending orders.
5. Expose updated state through runtime for subsequent turns.

## Turn Structure
The engine executes a fixed five-phase sequence:
1. ISSUE_ORDERS
2. SIMULTANEOUS_MOVE
3. COMBAT
4. RETREAT
5. END_TURN

Behavior notes:
- ISSUE_ORDERS is a structural phase (orders are read from campaign state).
- COMBAT and RETREAT phases are executed as structural phases in the turn pipeline.
- END_TURN always increments turn number by 1, flips side ALLIES <-> AXIS, and clears pending orders.
- NEUTRAL is not a valid active turn side for side flipping.

## Map and Terrain
- Unit placement uses hex-tile coordinates from scenario definitions.
- Scenario map metadata (width, height, default terrain type) is loaded and used for engine-side validation and movement bounds checks.
- Terrain rendering uses the generated terrain map baseline in the current UI implementation.
- Movement validation enforces map bounds.
- Movement into impassable terrain is blocked for default terrain VOID and WATER.
- Invalid movement orders are ignored silently (unit remains in place).

## Units and Orders
- Units are modeled as military formations.
- Unit identity and placement are represented by immutable domain records.
- The order model supports two order types: MOVE and HOLD.
- Unit and order data are loaded from scenario/campaign JSON.

## Scenario Loading and Initialization
- Scenario resources are loaded from bundled JSON files.
- Scenario index listing is supported through scenarios-index.json.
- Loader parses:
  - Scenario metadata (id, name)
  - Map definition (width, height, defaultTerrain)
  - Unit list
  - Campaign state (campaignId, scenarioId, turnNumber, activeSide, pendingOrders)
- Loader validates unit coordinates against scenario bounds and rejects out-of-range units.

## Runtime and UI Baseline
- Runtime supports one-turn simulation and persistent state update after each turn.
- UI baseline includes:
  - Battlefield map panel rendering
  - Unit icon rendering on map
  - Unit selection behavior
  - Status bar with scenario name, turn number, and active side
  - End turn action wired to runtime turn simulation

## Additional Implemented UI Behaviors
The following functional behaviors are implemented and are part of this baseline.

- REQ-UI-CAM-001: The map camera is draggable with pointer drag and is clamped to map bounds.
- REQ-UI-CAM-002: Zoom is applied only with CTRL + mouse wheel, clamped to range 0.5-3.0.
- REQ-UI-CAM-003: During zoom, camera offset is recalculated so the world point under cursor remains stable.
- REQ-UI-CAM-004: On unit selection, if the selected unit is not fully visible, camera recenters to make the unit fully visible, then clamps to map bounds.
- REQ-UI-SEL-001: Unit selection is click-based and limited to units of active side.
- REQ-UI-SEL-002: TAB cycles selection across active-side units in stable list order with wrap-around.
- REQ-UI-SEL-003: ESC clears current unit selection.
- REQ-UI-SEL-004: A pointer gesture is treated as selection click only when drag distance threshold is below 10 px (squared threshold < 100).
- REQ-UI-SEL-005: When selection changes, selector blink state is reset and selected unit border starts visible.
- REQ-UI-SEL-006: Selected unit is rendered last (on top of other units) and uses blinking highlight.
- REQ-UI-FOG-001: Enemy unit type is hidden in the map icon layer and rendered as unidentified icon.
- REQ-UI-DBG-001: Debug grid overlay can be toggled on/off with G key.
- REQ-UI-PANEL-001: Unit info panel visibility is synchronized with selection state (show when selected, hide when no selection).
- REQ-UI-MOVE-001: After selecting a unit and activating MOVE mode, the game immediately enters destination-selection state. While the cursor is over a valid destination hex, the map shows a blinking preview for that destination hex to indicate the unit's prospective arrival position. Invalid terrain does not show destination preview.
- REQ-UI-MOVE-002: Clicking a valid destination hex confirms the target only when an active valid destination preview exists on that same hex, exits destination-selection state, removes the blinking preview, and shows a persistent small flag marker on the assigned target hex. Clicking without an active valid preview does not confirm a target and keeps destination-selection state active.
- REQ-UI-MOVE-003: After target confirmation, selection focus moves to the next active-side unit that has no assigned target in current turn order. If no such unit remains, selection is cleared.

## Planned Functional Requirements (Pending Implementation)
The following functional requirements are approved for next implementation increment and are not part of implemented v1 baseline yet.

- REQ-UI-AUDIO-001: Target confirmation triggers a short feedback sound.
- REQ-ORD-MOVE-001: Confirmed target hex is persisted as unit-scoped movement target context for current turn.
- REQ-ORD-MOVE-002: Movement phase consumes persisted movement target context and attempts movement toward assigned target.
- REQ-ORD-MOVE-003: System supports assigning movement targets for multiple units in one command phase.

## Related Documents
- Deferred scope: [game-requirements-plan.md](game-requirements-plan.md)
- Traceability matrix: [../engine/traceability-matrix.md](../engine/traceability-matrix.md)
