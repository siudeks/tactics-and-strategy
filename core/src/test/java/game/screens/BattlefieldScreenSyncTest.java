package game.screens;

import game.engine.TurnPhase;
import game.engine.TurnResult;
import game.engine.GameRuntime;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import game.scenario.ScenarioLoader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlefieldScreenSyncTest {

    private static final class FakeView implements BattlefieldScreen.UnitInfoView {
        @Nullable String shownId = null;
        boolean visible = false;

        @Override
        public void showUnit(String unitId) {
            shownId = unitId;
            visible = true;
        }

        @Override
        public void hide() {
            shownId = null;
            visible = false;
        }
    }

    // --- syncUnitInfoPanel ---

    @Test
    void syncUnitInfoPanel_showsUnitAndSetsId_whenUnitSelected() {
        FakeView view = new FakeView();

        BattlefieldScreen.syncUnitInfoPanel("tank-1", view);

        assertEquals("tank-1", view.shownId);
        assertTrue(view.visible);
    }

    @Test
    void syncUnitInfoPanel_hidesSection_whenNoUnitSelected() {
        FakeView view = new FakeView();
        view.showUnit("tank-1");  // precondition: was visible

        BattlefieldScreen.syncUnitInfoPanel(null, view);

        assertFalse(view.visible);
        assertNull(view.shownId);
    }

    @Test
    void syncUnitInfoPanel_updatesId_whenSelectionChanges() {
        FakeView view = new FakeView();

        BattlefieldScreen.syncUnitInfoPanel("unit-A", view);
        assertEquals("unit-A", view.shownId);

        BattlefieldScreen.syncUnitInfoPanel("unit-B", view);
        assertEquals("unit-B", view.shownId);
        assertTrue(view.visible);
    }

    @Test
    void initializePhaseOverlayState_usesPhaseTraceOrderAndStartsAtFirstPhase() {
        TurnResult turnResult = new TurnResult(
            ScenarioLoader.loadBootstrapScenario().campaignState(),
            List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.SIMULTANEOUS_MOVE, TurnPhase.COMBAT, TurnPhase.RETREAT, TurnPhase.END_TURN),
            7L,
            5L,
            "snapshot"
        );

        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(turnResult.phaseTrace());

        assertNotNull(overlayState);
        assertEquals(TurnPhase.ISSUE_ORDERS, BattlefieldScreen.activeOverlayPhase(overlayState));
        assertEquals(
            List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.SIMULTANEOUS_MOVE, TurnPhase.COMBAT, TurnPhase.RETREAT, TurnPhase.END_TURN),
            overlayState.phaseTrace()
        );
    }

    @Test
    void advancePhaseOverlayState_followsPhaseTraceOrderIncludingRetreat_withThreeSecondBoundaries() {
        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(
            List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.SIMULTANEOUS_MOVE, TurnPhase.COMBAT, TurnPhase.RETREAT, TurnPhase.END_TURN)
        );
        assertNotNull(overlayState);
        assertEquals(TurnPhase.ISSUE_ORDERS, BattlefieldScreen.activeOverlayPhase(overlayState));

        BattlefieldScreen.PhaseOverlayState afterThreeSeconds = BattlefieldScreen.advancePhaseOverlayState(overlayState, 3.0f);
        assertNotNull(afterThreeSeconds);
        assertEquals(TurnPhase.SIMULTANEOUS_MOVE, BattlefieldScreen.activeOverlayPhase(afterThreeSeconds));

        BattlefieldScreen.PhaseOverlayState afterSixSeconds = BattlefieldScreen.advancePhaseOverlayState(afterThreeSeconds, 3.0f);
        assertNotNull(afterSixSeconds);
        assertEquals(TurnPhase.COMBAT, BattlefieldScreen.activeOverlayPhase(afterSixSeconds));

        BattlefieldScreen.PhaseOverlayState afterNineSeconds = BattlefieldScreen.advancePhaseOverlayState(afterSixSeconds, 3.0f);
        assertNotNull(afterNineSeconds);
        assertEquals(TurnPhase.RETREAT, BattlefieldScreen.activeOverlayPhase(afterNineSeconds));

        BattlefieldScreen.PhaseOverlayState afterTwelveSeconds = BattlefieldScreen.advancePhaseOverlayState(afterNineSeconds, 3.0f);
        assertNotNull(afterTwelveSeconds);
        assertEquals(TurnPhase.END_TURN, BattlefieldScreen.activeOverlayPhase(afterTwelveSeconds));

        BattlefieldScreen.PhaseOverlayState completed = BattlefieldScreen.advancePhaseOverlayState(afterTwelveSeconds, 3.0f);
        assertNull(completed);
    }

    @Test
    void advancePhaseOverlayState_keepsEachPhaseVisibleForThreeSeconds_andThenCompletesSequence() {
        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(
            List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.RETREAT)
        );

        BattlefieldScreen.PhaseOverlayState beforeBoundary = BattlefieldScreen.advancePhaseOverlayState(overlayState, 2.99f);
        assertNotNull(beforeBoundary);
        assertEquals(TurnPhase.ISSUE_ORDERS, BattlefieldScreen.activeOverlayPhase(beforeBoundary));

        BattlefieldScreen.PhaseOverlayState secondPhase = BattlefieldScreen.advancePhaseOverlayState(beforeBoundary, 0.02f);
        assertNotNull(secondPhase);
        assertEquals(TurnPhase.RETREAT, BattlefieldScreen.activeOverlayPhase(secondPhase));

        BattlefieldScreen.PhaseOverlayState completed = BattlefieldScreen.advancePhaseOverlayState(secondPhase, 3.0f);
        assertNull(completed);
    }

    @Test
    void shouldBlockInteractions_returnsTrue_whenOverlayStateIsActive() {
        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(
            List.of(TurnPhase.ISSUE_ORDERS)
        );

        boolean blocked = BattlefieldScreen.shouldBlockInteractions(overlayState);

        assertTrue(blocked);
    }

    @Test
    void shouldAcceptEndTurn_returnsFalse_whenOverlaySequenceIsActive() {
        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(
            List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.END_TURN)
        );

        boolean accepted = BattlefieldScreen.shouldAcceptEndTurn(overlayState);

        assertFalse(accepted);
    }

    @Test
    void shouldAcceptEndTurn_staysFalseAcrossRepeatedAttempts_whenOverlayRemainsActive() {
        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(
            List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.END_TURN)
        );

        boolean firstAttempt = BattlefieldScreen.shouldAcceptEndTurn(overlayState);
        boolean secondAttempt = BattlefieldScreen.shouldAcceptEndTurn(overlayState);

        assertFalse(firstAttempt);
        assertFalse(secondAttempt);
    }

    @Test
    void shouldAcceptEndTurn_returnsTrue_afterOverlaySequenceCompletes() {
        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(
            List.of(TurnPhase.ISSUE_ORDERS)
        );
        BattlefieldScreen.PhaseOverlayState completed = BattlefieldScreen.advancePhaseOverlayState(overlayState, 3.0f);

        boolean accepted = BattlefieldScreen.shouldAcceptEndTurn(completed);

        assertTrue(accepted);
    }

    @Test
    void shouldAcceptEndTurn_remainsFalse_forEntireActiveOverlayLifecycle_thenTurnsTrueAfterCompletion() {
        BattlefieldScreen.PhaseOverlayState overlayState = BattlefieldScreen.initializePhaseOverlayState(
            List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.RETREAT)
        );
        assertNotNull(overlayState);

        assertFalse(BattlefieldScreen.shouldAcceptEndTurn(overlayState));

        BattlefieldScreen.PhaseOverlayState midFirstPhase = BattlefieldScreen.advancePhaseOverlayState(overlayState, 1.5f);
        assertNotNull(midFirstPhase);
        assertFalse(BattlefieldScreen.shouldAcceptEndTurn(midFirstPhase));

        BattlefieldScreen.PhaseOverlayState secondPhase = BattlefieldScreen.advancePhaseOverlayState(midFirstPhase, 1.5f);
        assertNotNull(secondPhase);
        assertFalse(BattlefieldScreen.shouldAcceptEndTurn(secondPhase));

        BattlefieldScreen.PhaseOverlayState nearCompletion = BattlefieldScreen.advancePhaseOverlayState(secondPhase, 2.99f);
        assertNotNull(nearCompletion);
        assertFalse(BattlefieldScreen.shouldAcceptEndTurn(nearCompletion));

        BattlefieldScreen.PhaseOverlayState completed = BattlefieldScreen.advancePhaseOverlayState(nearCompletion, 0.01f);
        assertNull(completed);
        assertTrue(BattlefieldScreen.shouldAcceptEndTurn(completed));
    }

    @Test
    void processEndTurnRequest_invokedTwiceDuringActiveOverlay_advancesSimulationOnlyOnce() {
        AtomicInteger simulationCalls = new AtomicInteger(0);

        BattlefieldScreen.EndTurnRequestResult firstRequest = BattlefieldScreen.processEndTurnRequest(
            null,
            () -> {
                simulationCalls.incrementAndGet();
                return new GameRuntime.TurnSimulationResult(new TurnResult(
                    ScenarioLoader.loadBootstrapScenario().campaignState(),
                    List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.END_TURN),
                    11L,
                    7L,
                    "snapshot-1"
                ));
            }
        );

        BattlefieldScreen.EndTurnRequestResult secondRequest = BattlefieldScreen.processEndTurnRequest(
            firstRequest.overlayState(),
            () -> {
                simulationCalls.incrementAndGet();
                return new GameRuntime.TurnSimulationResult(new TurnResult(
                    ScenarioLoader.loadBootstrapScenario().campaignState(),
                    List.of(TurnPhase.ISSUE_ORDERS, TurnPhase.END_TURN),
                    12L,
                    8L,
                    "snapshot-2"
                ));
            }
        );

        assertTrue(firstRequest.turnAdvanced());
        assertFalse(secondRequest.turnAdvanced());
        assertEquals(1, simulationCalls.get());
    }
}
