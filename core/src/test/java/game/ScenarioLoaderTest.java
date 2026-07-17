package game;

import org.junit.jupiter.api.Test;
import game.domain.Side;
import game.domain.TerrainType;
import game.domain.UnitType;
import game.platform.ScenarioLoader;
import game.scenario.ScenarioEntry;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioLoaderTest {

    @Test
    void loadBootstrapScenario_returnsNonNull() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertNotNull(loaded);
    }

    @Test
    void loadBootstrapScenario_parsesScenarioId() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals("desert-rats-bootstrap", loaded.scenarioDefinition().id());
    }

    @Test
    void loadBootstrapScenario_parsesScenarioName() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals("Desert Rats Bootstrap", loaded.scenarioDefinition().name());
    }

    @Test
    void loadBootstrapScenario_parsesMapDimensions() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(10, loaded.scenarioDefinition().mapWidth());
        assertEquals(10, loaded.scenarioDefinition().mapHeight());
    }

    @Test
    void loadBootstrapScenario_parsesDefaultTerrain() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(TerrainType.SAND, loaded.scenarioDefinition().defaultTerrain());
    }

    @Test
    void loadBootstrapScenario_parsesUnitCount() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(4, loaded.scenarioDefinition().units().size());
    }

    @Test
    void loadBootstrapScenario_mapsBattalionArmorTypeToLightTank() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(UnitType.LIGHT_TANK, loaded.scenarioDefinition().units().getFirst().type());
    }

    @Test
    void loadBootstrapScenario_mapsInfantryTypeToFootInfantry() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(UnitType.FOOT_INFANTRY, loaded.scenarioDefinition().units().get(1).type());
    }

    @Test
    void loadOperationCompass_mapsBattalionInfantryTypeToMotorisedInfantry() {
        var loaded = ScenarioLoader.loadFromResource("scenarios/desert-rats-op-compass.json");
        assertEquals(UnitType.MOTORISED_INFANTRY, loaded.scenarioDefinition().units().getLast().type());
    }

    @Test
    void loadBootstrapScenario_parsesActiveSide() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(Side.ALLIES, loaded.campaignState().activeSide());
    }

    @Test
    void loadBootstrapScenario_parsesTurnNumber() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(1, loaded.campaignState().turnNumber());
    }

        @Test
        void load_rejectsUnitCoordinatesOutsideScenarioBounds() {
                var json = scenarioJson("invalid-coordinates", 5, 4, 5, -1);

                var exception = assertThrows(
                        IllegalArgumentException.class,
                        () -> ScenarioLoader.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                );

                assertEquals(
                        "Scenario invalid-coordinates has unit bad-unit at tile (5,-1) outside bounds [0..4] x [0..3]",
                        exception.getMessage()
                );
        }

        @Test
        void load_acceptsUnitCoordinatesWithinScenarioBounds() {
                var json = scenarioJson("valid-coordinates", 5, 4, 4, 3);

                var loaded = ScenarioLoader.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

                assertEquals("valid-coordinates", loaded.scenarioDefinition().id());
                assertEquals(1, loaded.scenarioDefinition().units().size());
                assertEquals(4, loaded.scenarioDefinition().units().getFirst().tileX());
                assertEquals(3, loaded.scenarioDefinition().units().getFirst().tileY());
        }

            @Test
            void load_rejectsUnitCoordinatesAtUpperYBoundOverflow() {
                var json = scenarioJson("invalid-upper-y", 5, 4, 0, 4);

                var exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ScenarioLoader.load(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)))
                );

                assertEquals(
                    "Scenario invalid-upper-y has unit bad-unit at tile (0,4) outside bounds [0..4] x [0..3]",
                    exception.getMessage()
                );
            }

        @Test
        void listAvailableScenarios_allBundledScenarioResourcesLoad() {
                var entries = ScenarioLoader.listAvailableScenarios();

                assertFalse(entries.isEmpty());
                for (ScenarioEntry entry : entries) {
                        assertDoesNotThrow(() -> ScenarioLoader.loadFromResource(entry.resourcePath()), entry.resourcePath());
                }
        }

        private static String scenarioJson(String scenarioId, int mapWidth, int mapHeight, int tileX, int tileY) {
                return """
                        {
                            "scenario": {
                                "id": "%s",
                                "name": "Test Scenario",
                                "map": {
                                    "width": %d,
                                    "height": %d,
                                    "defaultTerrain": "SAND"
                                },
                                "units": [
                                    {"id": "bad-unit", "side": "ALLIES", "type": "FOOT_INFANTRY", "size": "BRIGADE", "tileX": %d, "tileY": %d}
                                ]
                            },
                            "campaignState": {
                                "campaignId": "test-campaign",
                                "scenarioId": "%s",
                                "turnNumber": 1,
                                "activeSide": "ALLIES",
                                "pendingOrders": []
                            }
                        }
                        """.formatted(scenarioId, mapWidth, mapHeight, tileX, tileY, scenarioId);
        }
}
