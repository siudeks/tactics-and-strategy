# User Guide (Implemented Baseline)

This guide describes only behavior implemented in the current game baseline.

## Quick Start

### Install Java 21
- Ensure Java 21 is installed.
- Verify with: `java -version`

### Build Runnable JAR
- From repository root run: `./gradlew lwjgl3:fatJar`
- Output: `lwjgl3/build/libs/tactics-and-strategy-all.jar`

### Run The Game
- Run: `java -jar lwjgl3/build/libs/tactics-and-strategy-all.jar`

## What Is Currently Playable
- Turn-based tactical baseline over loaded scenario data.
- Battlefield map rendering with unit icons.
- Selection and movement-target planning UI for active-side units.
- End turn action that executes one full engine turn and updates runtime state.

## Turn Loop (Implemented)
- Turn phase sequence is fixed:
  1. ISSUE_ORDERS
  2. SIMULTANEOUS_MOVE
  3. COMBAT
  4. RETREAT
  5. END_TURN
- End turn increments turn number, flips side ALLIES <-> AXIS, and clears pending orders.

## Camera Controls (REQ-UI-CAM-001, REQ-UI-CAM-002, REQ-UI-CAM-003, REQ-UI-CAM-004)
- Drag on map to pan camera.
- Camera panning is clamped to map bounds.
- Zoom works only with CTRL + mouse wheel.
- Zoom is clamped to `0.5` to `3.0`.
- Zoom keeps the world point under cursor stable.
- Selecting a unit can recenter the camera if the unit is not fully visible.

## Unit Selection (REQ-UI-SEL-001, REQ-UI-SEL-002, REQ-UI-SEL-003, REQ-UI-SEL-004, REQ-UI-SEL-005, REQ-UI-SEL-006)
- Click selects only active-side units.
- TAB cycles active-side units in stable order with wrap-around.
- ESC clears selection.
- Click selection is ignored if pointer movement exceeded drag threshold.
- Selection change resets blink state.
- Selected unit is rendered on top and highlighted with blink.

## Information And Fog Of War (REQ-UI-FOG-001, REQ-UI-PANEL-001, REQ-UI-DBG-001)
- Enemy unit type is hidden in icon layer.
- Unit info panel is shown only when a unit is selected.
- Debug grid overlay can be toggled with G.

## Move Target Planning (REQ-UI-MOVE-001, REQ-UI-MOVE-002, REQ-UI-MOVE-003, REQ-ORD-MOVE-001, REQ-ORD-MOVE-002, REQ-ORD-MOVE-003)
- Select unit, enter MOVE mode, then point to destination hex.
- Valid destination hex shows blinking preview marker.
- Invalid terrain does not show preview.
- Click confirms destination only if active valid preview exists on that tile.
- Valid target confirmation plays a short feedback sound.
- After confirmation, preview is cleared and persistent flag marker is shown on target hex.
- after target confirmation, selection focus moves to the next active-side unit without assigned target in current turn order.
- If no such unit remains, selection is cleared.
- Confirmed MOVE targets are persisted per unit for the current turn; re-confirming a target for the same unit replaces the previous one, and persisted targets are cleared automatically when the turn ends.
- Multiple units of the active side may each be assigned their own MOVE target in the same command phase; each unit's target is independent of the others.
- Assigned MOVE targets are resolved when the turn is ended: each unit advances toward its own target during turn simulation.
- Targets are validated at resolution time, not when assigned: if a target falls outside the map or on impassable terrain, the order is silently dropped and the unit stays in place (no error is shown).

## Terrain Rules During Movement
- Movement outside map bounds is blocked.
- Movement into `VOID` or `WATER` is blocked.
- Invalid movement orders are ignored silently (unit stays in place).

## Scenario Pack Availability
- Current scenario definitions are documented in Scenario Pack v0.
- Scenario objective and failure text exists in documentation, while objective/failure runtime evaluation is still partial in engine baseline.

## Non-Functional Behavior (NFR-DET-001, NFR-DET-002, NFR-DET-003)
- Runtime behavior is deterministic for identical inputs and seed context.
- Turn simulation provides canonical state snapshot suitable for semantic comparison.

## Reliability (NFR-REL-001, NFR-REL-002, NFR-REL-003)
- Scenario and campaign data are loaded from bundled JSON resources.
- Out-of-range unit coordinates are rejected during scenario load.
- Invalid movement orders do not crash simulation flow.

## Testability (NFR-TST-001, NFR-TST-002)
- Turn simulation behavior is structured for repeatable verification.
- Runtime exposes post-turn state for semantic comparisons.

## Maintainability (NFR-MNT-001, NFR-MNT-002, NFR-MNT-003)
- Domain identity and placement use immutable records.
- Engine execution uses explicit named phases.
- Build enforces package-level nullability checks.

## Usability Baseline (NFR-USAB-001, NFR-USAB-002, NFR-USAB-003)
- Status bar exposes scenario name, turn number, and active side.
- Selection, camera, and MOVE flow are designed for clear interaction state, including deterministic auto-focus to the next unit that still needs a target.
- MOVE planning and confirmation provide explicit visual feedback.

## Explicit Scope Boundary
- Planned requirements are intentionally excluded from this guide until implemented.
- Source of truth for implemented scope:
  - `docs/requirements/game-requirements-functional.md`
  - `docs/requirements/game-requirements-non-functional.md`
