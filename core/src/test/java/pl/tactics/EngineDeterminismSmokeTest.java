package pl.tactics;

import org.junit.jupiter.api.Test;
import pl.tactics.domain.CampaignState;
import pl.tactics.engine.DeterministicContext;
import pl.tactics.engine.TurnEngine;
import pl.tactics.engine.TurnResult;
import pl.tactics.scenario.LoadedScenario;
import pl.tactics.scenario.ScenarioLoader;

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
