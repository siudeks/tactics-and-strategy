package game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import game.engine.GameRuntime;
import game.platform.ScenarioLoader;

import static org.junit.jupiter.api.Assertions.*;

class BattlefieldScreenRuntimeAdapterIntegrationTest {

    @BeforeAll
    static void initGdx() {
        var config = new HeadlessApplicationConfiguration();
        config.updatesPerSecond = -1;
        new HeadlessApplication(new ApplicationAdapter() {}, config);
    }

    @Test
    void runtimeAdapter_statusBarFormat_matchesSpec() {
        var runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
        var scenarioId = "desert-rats-bootstrap";
        var statusText = String.format("Scenariusz: %s | Tura: %d | Strona aktywna: %s",
            scenarioId,
            runtime.getTurnNumber(),
            runtime.getActiveSideCode());
        assertEquals("Scenariusz: desert-rats-bootstrap | Tura: 1 | Strona aktywna: ALLIES", statusText);
    }
}
