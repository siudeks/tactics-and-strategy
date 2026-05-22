package pl.tactics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.tactics.engine.GameRuntime;
import pl.tactics.scenario.ScenarioLoader;
import pl.tactics.terrain.TerrainMapDefinition;

import static org.junit.jupiter.api.Assertions.*;

class GameRuntimeTurnIntegrationTest {

    private GameRuntime runtime;

    @BeforeEach
    void setUp() {
        runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
    }

    @Test
    void gameRuntime_initialTurnNumber_isOne() {
        assertEquals(1, runtime.getTurnNumber());
    }

    @Test
    void gameRuntime_initialActiveSide_isAllies() {
        assertEquals("ALLIES", runtime.getActiveSideCode());
    }

    @Test
    void gameRuntime_simulateOneTurn_incrementsTurnNumber() {
        runtime.simulateOneTurn();
        assertEquals(2, runtime.getTurnNumber());
    }

    @Test
    void gameRuntime_simulateOneTurn_flipsActiveSide() {
        runtime.simulateOneTurn();
        assertEquals("AXIS", runtime.getActiveSideCode());
    }

    @Test
    void gameRuntime_togglePaletteMode_changesMode() {
        TerrainMapDefinition.PaletteMode initial = runtime.getPaletteMode();
        runtime.togglePaletteMode();
        assertNotEquals(initial, runtime.getPaletteMode());
    }
}
