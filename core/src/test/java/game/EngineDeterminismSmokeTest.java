package game;

import org.junit.jupiter.api.Test;
import game.engine.DeterministicContext;
import game.engine.TurnEngine;
import game.platform.ScenarioLoader;

import static org.junit.jupiter.api.Assertions.*;

class EngineDeterminismSmokeTest {

    @Test
    void determinism_sameInputProducesSameCanonicalSnapshot() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        var state = loaded.campaignState();
        var ctx = DeterministicContext.withSeed(0L);

        var engine1 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());
        var engine2 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());

        var result1 = engine1.runOneTurn(state);
        var result2 = engine2.runOneTurn(state);

        assertEquals(result1.canonicalSnapshot(), result2.canonicalSnapshot());
    }

    @Test
    void determinism_areSemanticallyEquivalent_returnsTrueForIdenticalResults() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        var state = loaded.campaignState();
        var ctx = DeterministicContext.withSeed(0L);

        var engine1 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());
        var engine2 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());

        var result1 = engine1.runOneTurn(state);
        var result2 = engine2.runOneTurn(state);

        assertTrue(TurnEngine.areSemanticallyEquivalent(result1, result2));
    }
}
