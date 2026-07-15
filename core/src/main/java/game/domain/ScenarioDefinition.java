package game.domain;

import java.util.List;

public record ScenarioDefinition(
    String id,
    String name,
    int mapWidth,
    int mapHeight,
    TerrainType defaultTerrain,
    List<Unit> units
) {
    public ScenarioDefinition {
        units = List.copyOf(units);
    }
}
