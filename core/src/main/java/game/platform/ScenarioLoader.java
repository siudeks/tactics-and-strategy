package game.platform;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderType;
import game.domain.ScenarioDefinition;
import game.domain.Side;
import game.domain.TerrainType;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
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
        InputStream is = ScenarioLoader.class.getClassLoader().getResourceAsStream(INDEX_RESOURCE);
        if (is == null) {
            throw new IllegalStateException("Scenario index not found: " + INDEX_RESOURCE);
        }
        try {
            String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JsonValue root = new JsonReader().parse(json);
            List<ScenarioEntry> entries = new ArrayList<>();
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
        InputStream is = ScenarioLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalArgumentException("Scenario resource not found: " + resourcePath);
        }
        return load(is);
    }

    public static LoadedScenario load(InputStream inputStream) {
        Objects.requireNonNull(inputStream, "inputStream must not be null");
        try {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return parseJson(json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read scenario input stream", e);
        }
    }

    private static LoadedScenario parseJson(String json) {
        JsonValue root = new JsonReader().parse(json);

        // Parse scenario section
        JsonValue scenarioJson = root.get("scenario");
        String scenarioId = scenarioJson.getString("id");
        String scenarioName = scenarioJson.getString("name");

        JsonValue mapJson = scenarioJson.get("map");
        int mapWidth = mapJson.getInt("width");
        int mapHeight = mapJson.getInt("height");
        TerrainType defaultTerrain = TerrainType.valueOf(mapJson.getString("defaultTerrain").toUpperCase(Locale.ROOT));

        // Parse units in scenario
        List<Unit> scenarioUnits = new ArrayList<>();
        JsonValue unitsJson = scenarioJson.get("units");
        if (unitsJson != null) {
            for (JsonValue unitJson = unitsJson.child; unitJson != null; unitJson = unitJson.next) {
                scenarioUnits.add(parseUnit(unitJson));
            }
        }
        validateUnitCoordinates(scenarioId, mapWidth, mapHeight, scenarioUnits);

        ScenarioDefinition scenarioDefinition = new ScenarioDefinition(
            scenarioId, scenarioName, mapWidth, mapHeight, defaultTerrain, scenarioUnits
        );

        // Parse campaignState section
        JsonValue campaignJson = root.get("campaignState");
        String campaignId = campaignJson.getString("campaignId");
        String campaignScenarioId = campaignJson.getString("scenarioId");
        int turnNumber = campaignJson.getInt("turnNumber");
        Side activeSide = Side.valueOf(campaignJson.getString("activeSide").toUpperCase(Locale.ROOT));

        List<Order> pendingOrders = new ArrayList<>();
        JsonValue ordersJson = campaignJson.get("pendingOrders");
        if (ordersJson != null) {
            for (JsonValue orderJson = ordersJson.child; orderJson != null; orderJson = orderJson.next) {
                pendingOrders.add(parseOrder(orderJson));
            }
        }

        CampaignState campaignState = new CampaignState(
            campaignId, campaignScenarioId, turnNumber, activeSide,
            scenarioUnits, pendingOrders
        );

        return new LoadedScenario(scenarioDefinition, campaignState);
    }

    private static Unit parseUnit(JsonValue unitJson) {
        String id = unitJson.getString("id");
        Side side = Side.valueOf(unitJson.getString("side").toUpperCase(Locale.ROOT));
        UnitType type = UnitType.fromScenarioValue(unitJson.getString("type"));
        UnitSize size = UnitSize.valueOf(unitJson.getString("size").toUpperCase(Locale.ROOT));
        int tileX = unitJson.getInt("tileX");
        int tileY = unitJson.getInt("tileY");
        return new Unit(id, side, type, size, tileX, tileY);
    }

    private static Order parseOrder(JsonValue orderJson) {
        String id = orderJson.getString("id");
        String unitId = orderJson.getString("unitId");
        Side side = Side.valueOf(orderJson.getString("side").toUpperCase(Locale.ROOT));
        OrderType type = OrderType.valueOf(orderJson.getString("type").toUpperCase(Locale.ROOT));
        int targetX = orderJson.getInt("targetX");
        int targetY = orderJson.getInt("targetY");
        return new Order(id, unitId, side, type, targetX, targetY);
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
