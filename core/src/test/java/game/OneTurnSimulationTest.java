package game;

import org.junit.jupiter.api.Test;
import game.engine.DeterministicContext;
import game.engine.PhaseStepResult;
import game.engine.RuntimePhase;
import game.engine.TurnExecutionSession;
import game.engine.TurnEngine;
import game.engine.TurnPhase;
import game.engine.TurnResult;
import game.platform.ScenarioLoader;

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

    @Test
    void oneTurn_stepwiseSessionMatchesMonolithicRun() {
        var loaded = ScenarioLoader.loadBootstrapScenario();
        var startState = loaded.campaignState();
        var engine = engine();

        TurnExecutionSession session = engine.beginExecution(startState);
        TurnResult stepwiseResult = null;
        while (!session.isComplete()) {
            PhaseStepResult stepResult = session.advance();
            assertEquals(stepResult.phase(), RuntimePhase.fromTurnPhase(stepResult.phase().turnPhase()));
            if (stepResult.phase() == RuntimePhase.SIMULTANEOUS_MOVE) {
                assertEquals(startState.units().size(), stepResult.movementPlayback().size());
            }
            if (stepResult.turnCompleted()) {
                stepwiseResult = stepResult.completedTurnResult().orElseThrow();
            }
        }

        TurnResult monolithicResult = engine.runOneTurn(startState);
        assertNotNull(stepwiseResult);
        assertTrue(TurnEngine.areSemanticallyEquivalent(stepwiseResult, monolithicResult));
    }
}
