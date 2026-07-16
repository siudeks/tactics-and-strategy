package game.platform;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import game.domain.*;
import game.scenario.LoadedScenario;
import game.scenario.ScenarioEntry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class ScenarioLoader {

    private static final String BOOTSTRAP_RESOURCE = "scenarios/desert-rats-bootstrap.json";
    private static final String INDEX_RESOURCE = "scenarios/scenarios-index.json";

    public static List<ScenarioEntry> listAvailableScenarios() {
        var is = ScenarioLoader.class.getClassLoader().getResourceAsStream(INDEX_RESOURCE);
        if (is == null) {
            throw new IllegalStateException("Scenario index not found: " + INDEX_RESOURCE);
        }
        try {
            var json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            var root = new JsonReader().parse(json);
            var entries = new ArrayList<ScenarioEntry>();
            for (JsonValue entry = root.get("scenarios").child; entry != null; entry = entry.next) {
                entries.add(new ScenarioEntry(entry.getString("name"), entry.getString("resource")));
            }
            return entries;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read scenario index", e);
        }
    }

    public static LoadedScenario loadBootstrapScenario() {
        return loadFromResource(BOOTSTRAP_RESOURCE);
    }

    public static LoadedScenario loadFromResource(String resourcePath) {
        var is = ScenarioLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("Scenario resource not found: " + resourcePath);
        }
        return load(is);
    }

    public static LoadedScenario load(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "inputStream must not be null");
        try {
            var json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return parseJson(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read scenario input stream", e);
        }
    }

    private static LoadedScenario parseJson(String json) {
        var root = new JsonReader().parse(json);

        // Parse scenario section
        var scenarioJson = root.get("scenario");
        var scenarioId = scenarioJson.getString("id");
        var scenarioName = scenarioJson.getString("name");

        var mapJson = scenarioJson.get("map");
        var mapWidth = mapJson.getInt("width");
        var mapHeight = mapJson.getInt("height");
        var defaultTerrain = TerrainType.valueOf(mapJson.getString("defaultTerrain").toUpperCase(Locale.ROOT));

        // Parse units in scenario
        var scenarioUnits = new ArrayList<Unit>();
        var unitsJson = scenarioJson.get("units");
        if (unitsJson != null) {
            for (JsonValue unitJson = unitsJson.child; unitJson != null; unitJson = unitJson.next) {
                scenarioUnits.add(parseUnit(unitJson));
            }
        }
        validateUnitCoordinates(scenarioId, mapWidth, mapHeight, scenarioUnits);

        var scenarioDefinition = new ScenarioDefinition(
            scenarioId, scenarioName, mapWidth, mapHeight, defaultTerrain, scenarioUnits
        );

        // Parse campaignState section
        var campaignJson = root.get("campaignState");
        var campaignId = campaignJson.getString("campaignId");
        var campaignScenarioId = campaignJson.getString("scenarioId");
        var turnNumber = campaignJson.getInt("turnNumber");
        var activeSide = Side.valueOf(campaignJson.getString("activeSide").toUpperCase(Locale.ROOT));

        var pendingOrders = new ArrayList<Order>();
        var ordersJson = campaignJson.get("pendingOrders");
        if (ordersJson != null) {
            for (JsonValue orderJson = ordersJson.child; orderJson != null; orderJson = orderJson.next) {
                pendingOrders.add(parseOrder(orderJson));
            }
        }

        var campaignState = new CampaignState(
            campaignId, campaignScenarioId, turnNumber, activeSide,
            scenarioUnits, pendingOrders
        );

        return new LoadedScenario(scenarioDefinition, campaignState);
    }

    private static Unit parseUnit(JsonValue unitJson) {
        var id = unitJson.getString("id");
        var side = Side.valueOf(unitJson.getString("side").toUpperCase(Locale.ROOT));
        var type = UnitType.fromScenarioValue(unitJson.getString("type"));
        var size = UnitSize.valueOf(unitJson.getString("size").toUpperCase(Locale.ROOT));
        var tileX = unitJson.getInt("tileX");
        var tileY = unitJson.getInt("tileY");
        return new Unit(id, side, type, size, tileX, tileY);
    }

    private static Order parseOrder(JsonValue orderJson) {
        var id = orderJson.getString("id");
        var unitId = UnitId.of(orderJson.getString("unitId"));
        var side = Side.valueOf(orderJson.getString("side").toUpperCase(Locale.ROOT));
        var type = OrderType.valueOf(orderJson.getString("type").toUpperCase(Locale.ROOT));
        var targetX = orderJson.getInt("targetX");
        var targetY = orderJson.getInt("targetY");
        return Order.of(id, unitId, side, type, TileCoordinate.of(targetX, targetY));
    }

    private static void validateUnitCoordinates(String scenarioId, int mapWidth, int mapHeight, List<Unit> units) {
        for (Unit unit : units) {
            if (unit.tileX() < 0 || unit.tileX() >= mapWidth || unit.tileY() < 0 || unit.tileY() >= mapHeight) {
                throw new IllegalArgumentException(
                    "Scenario %s has unit %s at tile (%d,%d) outside bounds [0..%d] x [0..%d]"
                        .formatted(scenarioId, unit.id(), unit.tileX(), unit.tileY(), mapWidth - 1, mapHeight - 1)
                );
            }
        }
    }
}
