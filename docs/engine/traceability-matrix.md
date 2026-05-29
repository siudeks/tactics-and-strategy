# Traceability Matrix v1

## 1. Purpose
This matrix links currently implemented behavior to requirements and executable evidence.

Status legend:
- `Implemented` - behavior exists in code and has evidence.
- `Partial` - behavior is represented structurally but has no full domain logic.
- `Not in v1 scope` - requirement area exists in product docs but is not implemented.

## 2. Requirement Coverage
| Requirement ID | Requirement Topic | v1 Status | Evidence Tests | Notes |
|---|---|---|---|---|
| REQ-LOOP-001 | Core gameplay loop and turn progression | Implemented | `OneTurnSimulationTest.oneTurn_phaseTraceContainsAllFivePhases`, `TurnEngineOrderTest.turnNumber_incrementsAfterRunOneTurn`, `TurnEngineOrderTest.activeSide_flipsAfterRunOneTurn` | Five-phase order and turn transition are enforced. |
| REQ-TURN-001 | Turn sequence and validation | Partial | `TurnEngineOrderTest.moveOrder_movesUnitToTarget`, `TurnEngineOrderTest.moveOrder_outOfBounds_unitStaysInPlace` | Sequence and basic movement validation exist; advanced validation does not. |
| REQ-UNIT-001 | Formation-based units and states | Partial | `CampaignInitializationTest.initialCampaignState_alliesUnitsPresent`, `CampaignInitializationTest.initialCampaignState_axisUnitsPresent` | Formation units are implemented; operational unit states are not. |
| REQ-MAP-001 | Hex map and terrain control model | Partial | `ScenarioLoaderTest.loadBootstrapScenario_parsesMapDimensions`, `TurnEngineOrderTest.moveOrder_toVoidTile_unitStaysInPlace` | Map dimensions and impassable terrain checks are present; control points are not. |
| REQ-SCEN-001 | Scenario objective/failure model | Partial | `ScenarioLoaderTest.loadBootstrapScenario_parsesScenarioId`, `ScenarioLoaderTest.listAvailableScenarios_allBundledScenarioResourcesLoad` | Scenario loading works; objective/failure runtime evaluation is missing. |
| REQ-CBT-001 | Contextual combat resolution | Partial | `OneTurnSimulationTest.oneTurn_phaseTraceContainsAllFivePhases` | Combat phase exists as turn placeholder only. |
| NFR-MNT-003 | Compilation-time nullability checks via package `@NullMarked` defaults | Implemented | `ArchitecturePackageInfoTest.allApplicationPackagesMustDefinePackageInfo`, `ArchitecturePackageInfoTest.allApplicationPackagesMustBeNullMarked`, Gradle task `nullabilityCheck` | Build enables Error Prone + NullAway for `compileJava` and `compileTestJava` with `NullAway:AnnotatedPackages=game`. |
| REQ-SUP-001 | Supply continuity and penalties | Not in v1 scope | None | Not implemented in engine runtime. |
| REQ-STACK-001 | Stacking constraints | Not in v1 scope | None | Not implemented in engine runtime. |
| REQ-HUD-001 | HUD operational readability | Not in v1 scope | None in this package | UI-specific behavior is outside this engine mechanics package. |
| REQ-UI-CAM-001 | Drag camera panning with map-bound clamping | Partial | None (direct input-flow tests missing) | Implemented in battlefield input handling and camera clamp path. |
| REQ-UI-CAM-002 | Zoom clamping and cursor-stable zoom transform | Implemented | `BattlefieldScreenRenderingTest.clampZoomLevel_clampsToBounds`, `BattlefieldScreenRenderingTest.cameraAfterZoom_keepsWorldPointUnderCursorStable` | UI utility behavior in battlefield screen module. |
| REQ-UI-CAM-003 | Zoom transform keeps world point under cursor stable | Implemented | `BattlefieldScreenRenderingTest.cameraAfterZoom_keepsWorldPointUnderCursorStable` | Implemented as camera transform helper. |
| REQ-UI-CAM-004 | Auto-center selected unit when not fully visible | Implemented | `BattlefieldScreenRenderingTest.isViewportReadyForCameraCentering_requiresPositivePanelSize`, `BattlefieldScreenRenderingTest.isUnitFullyVisibleInViewport_returnsTrue_whenUnitFitsInWorldViewport`, `BattlefieldScreenRenderingTest.isUnitFullyVisibleInViewport_returnsFalse_whenUnitExceedsViewportEdge`, `BattlefieldScreenRenderingTest.centeredCameraPosition_centersUnitAndClampsToMapBounds` | Added in BattlefieldScreen selection flow, including startup viewport readiness guard. |
| REQ-UI-SEL-001 | Click selection resolves unit id at screen point for active side only | Implemented | `BattlefieldScreenUnitSelectionTest.unitIdAtScreenPoint_returnsId_whenPointInsideBoundsAndSideMatches`, `BattlefieldScreenUnitSelectionTest.unitIdAtScreenPoint_returnsNull_whenPointInsideBoundsButWrongSide` | Input plumbing is UI-layer behavior. |
| REQ-UI-SEL-002 | TAB-like selection cycle order with wrap-around | Implemented | `BattlefieldScreenUnitSelectionTest.nextSelectedUnitId_advancesToNextUnit`, `BattlefieldScreenUnitSelectionTest.nextSelectedUnitId_wrapsToFirstAfterLast` | Selection list order follows active units list. |
| REQ-UI-SEL-003 | ESC clears current unit selection | Partial | None (direct keybinding tests missing) | Implemented in key handling path. |
| REQ-UI-SEL-004 | Drag-threshold gate separates click selection from drag pan | Partial | None (direct input-flow tests missing) | Threshold check exists in pointer up handler (`dx*dx + dy*dy < 100`). |
| REQ-UI-SEL-005 | Selection change resets selector blink state | Partial | None (direct visual-state tests missing) | Implemented in selection setter. |
| REQ-UI-SEL-006 | Selected unit renders on top with blinking highlight | Partial | None (direct render-order tests missing) | Implemented in draw pass by deferring selected placement. |
| REQ-UI-FOG-001 | Enemy unit type concealment in icon layer | Implemented | `BattlefieldScreenUnitSelectionTest.visibleUnitType_returnsActualType_forOwnUnit`, `BattlefieldScreenUnitSelectionTest.visibleUnitType_returnsNull_forEnemyUnit` | Implemented as icon-type visibility rule; refreshed tank icon assets at `core/src/main/resources/ui/icon_medium_tank_64x64.png`, `core/src/main/resources/ui/icon_light_tank_64x64.png`, `core/src/main/resources/ui/icon_infantry_tank_64x64.png`, and `core/src/main/resources/ui/icon_anti_tank_64x64.png`. |
| REQ-UI-DBG-001 | Debug grid overlay toggle with G key | Partial | None (direct keybinding/render toggle tests missing) | Implemented in key handling and conditional draw path. |
| REQ-UI-PANEL-001 | Unit info panel sync with selection state | Implemented | `BattlefieldScreenSyncTest.syncUnitInfoPanel_showsUnitAndSetsId_whenUnitSelected`, `BattlefieldScreenSyncTest.syncUnitInfoPanel_hidesSection_whenNoUnitSelected` | Implemented in shared sync helper. |
| REQ-UI-MOVE-001 | Immediate MOVE destination-selection mode with blinking preview on valid destination hex only | Partial | `BattlefieldScreenUnitSelectionTest.mapTileAtPanelPoint_returnsTileCoordinates_forClickInsideMap`, `BattlefieldScreenUnitSelectionTest.moveTargetAssignmentForClick_returnsAssignment_whenMoveModeAndSelectionArePresent`, `BattlefieldScreenUnitSelectionTest.movePreviewTile_returnsHoveredTile_whenMoveModeSelectionAndTerrainAreValid`, `BattlefieldScreenUnitSelectionTest.movePreviewTile_returnsNull_whenHoveredTileIsImpassable`, `BattlefieldScreenUnitSelectionTest.isPassableTerrainCode_returnsFalse_forVoidAndWater`, `BattlefieldScreenUnitSelectionTest.shouldConsumeClickInMoveMode_returnsTrue_whenMoveModeIsActive` | MOVE mode activation, valid-terrain filtering, and preview-target derivation exist in the battlefield screen; end-to-end render/input-flow evidence is still limited to helper-level tests. |
| REQ-UI-MOVE-002 | Clicking valid destination replaces preview with persistent small flag marker and exits selection state | Partial | `BattlefieldScreenUnitSelectionTest.moveTargetAssignmentForClick_returnsAssignment_whenMoveModeAndSelectionArePresent`, `BattlefieldScreenUnitSelectionTest.moveTargetAssignmentForClick_returnsNull_whenPreviewIsMissing`, `BattlefieldScreenUnitSelectionTest.moveTargetAssignmentForClick_returnsNull_whenClickedTileDoesNotMatchPreview` | Confirmation now requires an active matching preview tile, exits MOVE mode for the unit, clears preview, and stores assigned target used by map flag-marker rendering; render flow still lacks dedicated end-to-end UI test coverage. |
| REQ-UI-MOVE-003 | Auto-focus next unit without target after confirmation | Planned | None yet (to add selection-flow tests) | Requires deterministic ordering policy in selection list. |
| REQ-UI-AUDIO-001 | Short confirmation sound on target assignment | Planned | None yet (to add UI audio trigger tests) | Trigger points and debouncing to be validated in tests. |
| REQ-ORD-MOVE-001 | Persist unit-scoped MOVE target context for current turn | Planned | None yet (to add order-context persistence tests) | Persists per-unit target intent between command and movement phases. |
| REQ-ORD-MOVE-002 | Movement phase consumes target context to attempt movement | Planned | None yet (to add turn-engine movement-intent tests) | Validation timing and invalid-target fallback still open. |
| REQ-ORD-MOVE-003 | Multi-unit target planning in single command phase | Planned | None yet (to add multi-unit integration tests) | Independent target context per unit in same turn. |

## 3. Code Anchor Map
- Turn semantics: `core/src/main/java/pl/tactics/engine/TurnEngine.java`
- Turn phases: `core/src/main/java/pl/tactics/engine/TurnPhase.java`
- Turn result contract: `core/src/main/java/pl/tactics/engine/TurnResult.java`
- Deterministic seed contract: `core/src/main/java/pl/tactics/engine/DeterministicContext.java`
- Scenario loading: `core/src/main/java/pl/tactics/scenario/ScenarioLoader.java`
- Battlefield UI behaviors: `core/src/main/java/game/screens/BattlefieldScreen.java`

## 4. Matrix Governance
1. Every new normative rule MUST add at least one executable evidence reference.
2. Rules without evidence MUST NOT be marked as `Implemented`.
3. Out-of-scope items MAY be tracked here but MUST stay non-normative for v1.
4. Planned requirements MAY be listed before implementation, but MUST remain in `Planned` status until executable evidence is added.
