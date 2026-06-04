package game;

import org.junit.jupiter.api.Test;
import game.domain.CampaignState;
import game.engine.DeterministicContext;
import game.engine.TurnEngine;
import game.engine.TurnResult;
import game.scenario.LoadedScenario;
import game.platform.ScenarioLoader;

import static org.junit.jupiter.api.Assertions.*;

class EngineDeterminismSmokeTest {

    @Test
    void determinism_sameInputProducesSameCanonicalSnapshot() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        CampaignState state = loaded.campaignState();
        DeterministicContext ctx = DeterministicContext.withSeed(0L);

        TurnEngine engine1 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());
        TurnEngine engine2 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());

        TurnResult result1 = engine1.runOneTurn(state);
        TurnResult result2 = engine2.runOneTurn(state);

        assertEquals(result1.canonicalSnapshot(), result2.canonicalSnapshot());
    }

    @Test
    void determinism_areSemanticallyEquivalent_returnsTrueForIdenticalResults() {
        LoadedScenario loaded = ScenarioLoader.loadBootstrapScenario();
        CampaignState state = loaded.campaignState();
        DeterministicContext ctx = DeterministicContext.withSeed(0L);

        TurnEngine engine1 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());
        TurnEngine engine2 = TurnEngine.fixedContext(ctx, loaded.scenarioDefinition());

        TurnResult result1 = engine1.runOneTurn(state);
        TurnResult result2 = engine2.runOneTurn(state);

        assertTrue(TurnEngine.areSemanticallyEquivalent(result1, result2));
    }
}
