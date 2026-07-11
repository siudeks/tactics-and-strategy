package game;

import game.engine.GameRuntime;
import game.engine.MovementPlaybackOutcome;
import game.engine.RuntimePhase;
import game.engine.PhaseStepResult;
import game.engine.TurnExecutionSession;
import game.platform.ScenarioLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRuntimeTurnSessionTest {

    @Test
    void beginTurnExecution_advancesStepwise_andCommitsOnlyAfterEndTurn() {
        GameRuntime runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
        var initialState = runtime.getCurrentCampaignState();

        TurnExecutionSession session = runtime.beginTurnExecution();
        assertEquals(RuntimePhase.ISSUE_ORDERS, session.currentPhase());
        assertEquals(1, runtime.getTurnNumber());
        assertFalse(session.isComplete());

        var issueOrdersStep = runtime.advanceTurnExecution();
        assertEquals(RuntimePhase.ISSUE_ORDERS, issueOrdersStep.phase());
        assertTrue(issueOrdersStep.movementPlayback().isEmpty());
        assertFalse(issueOrdersStep.turnCompleted());
        assertEquals(1, runtime.getTurnNumber());
        assertEquals(initialState, runtime.getCurrentCampaignState());

        var moveStep = runtime.advanceTurnExecution();
        assertEquals(RuntimePhase.SIMULTANEOUS_MOVE, moveStep.phase());
        assertFalse(moveStep.turnCompleted());
        assertEquals(initialState.units().size(), moveStep.movementPlayback().size());
        assertTrue(moveStep.movementPlayback().stream().allMatch(playback -> playback.outcome() == MovementPlaybackOutcome.SKIPPED));

        PhaseStepResult finalStep = null;
        while (runtime.hasActiveTurnExecution()) {
            finalStep = runtime.advanceTurnExecution();
        }

        assertNotNull(finalStep);
        assertTrue(finalStep.turnCompleted());
        assertEquals(2, runtime.getTurnNumber());
        assertEquals("AXIS", runtime.getActiveSideCode());
        assertNotNull(finalStep.completedTurnResult().orElseThrow());
    }
}