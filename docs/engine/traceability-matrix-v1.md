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
| REQ-SUP-001 | Supply continuity and penalties | Not in v1 scope | None | Not implemented in engine runtime. |
| REQ-STACK-001 | Stacking constraints | Not in v1 scope | None | Not implemented in engine runtime. |
| REQ-HUD-001 | HUD operational readability | Not in v1 scope | None in this package | UI-specific behavior is outside this engine mechanics package. |

## 3. Code Anchor Map
- Turn semantics: `core/src/main/java/pl/tactics/engine/TurnEngine.java`
- Turn phases: `core/src/main/java/pl/tactics/engine/TurnPhase.java`
- Turn result contract: `core/src/main/java/pl/tactics/engine/TurnResult.java`
- Deterministic seed contract: `core/src/main/java/pl/tactics/engine/DeterministicContext.java`
- Scenario loading: `core/src/main/java/pl/tactics/scenario/ScenarioLoader.java`

## 4. Matrix Governance
1. Every new normative rule MUST add at least one executable evidence reference.
2. Rules without evidence MUST NOT be marked as `Implemented`.
3. Out-of-scope items MAY be tracked here but MUST stay non-normative for v1.
