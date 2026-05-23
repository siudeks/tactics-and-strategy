# Scenario Runtime Contract v1 (Normative)

## 1. Purpose
This document defines the runtime contract for loading scenario resources into executable game state.

## 2. Entry Points
The loader contract applies to:
- `ScenarioLoader.loadBootstrapScenario()`
- `ScenarioLoader.loadFromResource(String resourcePath)`
- `ScenarioLoader.load(InputStream inputStream)`
- `ScenarioLoader.listAvailableScenarios()`

## 3. Input Data Shape
A scenario payload MUST contain:
- `scenario.id`
- `scenario.name`
- `scenario.map.width`
- `scenario.map.height`
- `scenario.map.defaultTerrain`
- `scenario.units[]` (optional but supported)
- `campaignState.campaignId`
- `campaignState.scenarioId`
- `campaignState.turnNumber`
- `campaignState.activeSide`
- `campaignState.pendingOrders[]` (optional but supported)

## 4. Parsing Rules
1. Enum values for `Side`, `UnitType`, `UnitSize`, `OrderType`, and `TerrainType` MUST parse case-insensitively via uppercase conversion.
2. Scenario units parsed from `scenario.units` MUST be copied into:
   - `ScenarioDefinition.units`
   - `CampaignState.units`
3. `campaignState.pendingOrders` MUST default to empty list when omitted.

## 5. Unit Coordinate Validation
Before returning `LoadedScenario`, loader MUST validate every unit coordinate:
- `0 <= tileX < mapWidth`
- `0 <= tileY < mapHeight`

If any unit is out of bounds, loader MUST throw `IllegalArgumentException` using this message shape:

`Scenario <scenarioId> has unit <unitId> at tile (<x>,<y>) outside bounds [0..<maxX>] x [0..<maxY>]`

Where:
- `maxX = mapWidth - 1`
- `maxY = mapHeight - 1`

## 6. Resource Availability Semantics
1. If a requested scenario resource cannot be found, loader MUST throw `IllegalArgumentException`.
2. If scenario index cannot be found, loader MUST throw `IllegalStateException`.
3. If stream read fails, loader MUST throw `RuntimeException` with cause.

## 7. Output Contract
Successful load MUST return `LoadedScenario` where:
1. `scenarioDefinition` is non-null.
2. `campaignState` is non-null.
3. `campaignState.activeSide` equals parsed value.
4. `campaignState.turnNumber` equals parsed value.

## 8. Compatibility Constraint
This contract is normative only for fields currently consumed by runtime engine behavior.
Fields for objectives, control points, supply, or victory are out of scope in v1.
