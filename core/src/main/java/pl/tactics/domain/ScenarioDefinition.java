package pl.tactics.domain;

import java.util.List;
import java.util.Objects;

public record ScenarioDefinition(
    String id,
    String name,
    int mapWidth,
    int mapHeight,
    TerrainType defaultTerrain,
    List<Unit> units
) {
    public ScenarioDefinition {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(defaultTerrain, "defaultTerrain must not be null");
        units = List.copyOf(Objects.requireNonNull(units, "units must not be null"));
    }
}
