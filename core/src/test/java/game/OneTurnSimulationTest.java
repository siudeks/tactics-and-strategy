package game;

import org.junit.jupiter.api.Test;
import game.engine.DeterministicContext;
import game.engine.TurnEngine;
import game.engine.TurnPhase;
import game.engine.TurnResult;
import game.scenario.ScenarioLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OneTurnSimulationTest {

    private TurnEngine engine() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        return TurnEngine.fixedContext(
            DeterministicContext.withSeed(42L),
            loaded.scenarioDefinition()
        );
    }

    @Test
    void oneTurn_phaseTraceContainsAllFivePhases() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        TurnResult result = engine().runOneTurn(loaded.campaignState());

        List<TurnPhase> expected = List.of(
            TurnPhase.ISSUE_ORDERS,
            TurnPhase.SIMULTANEOUS_MOVE,
            TurnPhase.COMBAT,
            TurnPhase.RETREAT,
            TurnPhase.END_TURN
        );
        assertEquals(expected, result.phaseTrace());
    }

    @Test
    void oneTurn_canonicalSnapshotIsNotEmpty() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        TurnResult result = engine().runOneTurn(loaded.campaignState());

        assertNotNull(result.canonicalSnapshot());
        assertFalse(result.canonicalSnapshot().isEmpty());
    }

    @Test
    void oneTurn_resultStateHasSameUnitCount() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        int initialUnitCount = loaded.campaignState().units().size();
        TurnResult result = engine().runOneTurn(loaded.campaignState());

        assertEquals(initialUnitCount, result.state().units().size());
    }
}
