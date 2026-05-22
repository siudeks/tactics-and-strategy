package pl.tactics;

import org.junit.jupiter.api.Test;
import pl.tactics.domain.Side;
import pl.tactics.domain.TerrainType;
import pl.tactics.scenario.LoadedScenario;
import pl.tactics.scenario.ScenarioLoader;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioLoaderTest {

    @Test
    void loadBootstrapScenario_returnsNonNull() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertNotNull(loaded);
    }

    @Test
    void loadBootstrapScenario_parsesScenarioId() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals("desert-rats-bootstrap", loaded.scenarioDefinition().id());
    }

    @Test
    void loadBootstrapScenario_parsesScenarioName() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals("Desert Rats Bootstrap", loaded.scenarioDefinition().name());
    }

    @Test
    void loadBootstrapScenario_parsesMapDimensions() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(10, loaded.scenarioDefinition().mapWidth());
        assertEquals(10, loaded.scenarioDefinition().mapHeight());
    }

    @Test
    void loadBootstrapScenario_parsesDefaultTerrain() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(TerrainType.SAND, loaded.scenarioDefinition().defaultTerrain());
    }

    @Test
    void loadBootstrapScenario_parsesUnitCount() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(4, loaded.scenarioDefinition().units().size());
    }

    @Test
    void loadBootstrapScenario_parsesActiveSide() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(Side.ALLIES, loaded.campaignState().activeSide());
    }

    @Test
    void loadBootstrapScenario_parsesTurnNumber() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        assertEquals(1, loaded.campaignState().turnNumber());
    }
}
