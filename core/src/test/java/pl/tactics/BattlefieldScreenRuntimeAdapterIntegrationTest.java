package pl.tactics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pl.tactics.engine.GameRuntime;
import pl.tactics.scenario.ScenarioLoader;
import pl.tactics.terrain.TerrainMapDefinition;

import static org.junit.jupiter.api.Assertions.*;

class BattlefieldScreenRuntimeAdapterIntegrationTest {

    @BeforeAll
    static void initGdx() {
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.updatesPerSecond = -1;
        new HeadlessApplication(new ApplicationAdapter() {}, config);
    }

    @Test
    void runtimeAdapter_paletteMode_defaultIsImproved() {
        GameRuntime runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
        assertEquals(TerrainMapDefinition.PaletteMode.IMPROVED, runtime.getPaletteMode());
    }

    @Test
    void runtimeAdapter_togglePaletteMode_changesMode() {
        GameRuntime runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
        runtime.togglePaletteMode();
        assertEquals(TerrainMapDefinition.PaletteMode.ORIGINAL, runtime.getPaletteMode());
    }

    @Test
    void runtimeAdapter_statusBarFormat_matchesSpec() {
        GameRuntime runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
        String scenarioId = "desert-rats-bootstrap";
        String statusText = String.format("Scenariusz: %s | Tura: %d | Strona aktywna: %s",
            scenarioId,
            runtime.getTurnNumber(),
            runtime.getActiveSideCode());
        assertEquals("Scenariusz: desert-rats-bootstrap | Tura: 1 | Strona aktywna: ALLIES", statusText);
    }
}
