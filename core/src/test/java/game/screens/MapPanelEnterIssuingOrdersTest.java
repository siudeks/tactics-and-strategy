package game.screens;

import game.domain.Side;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapPanelEnterIssuingOrdersTest {

    @Test
    void enterIssuingOrdersOutcome_returnsNoProgress_whenCurrentCommandSideIsIncomplete() {
        var outcome = MapPanel.enterIssuingOrdersOutcome(
            false,
            Side.ALLIES,
            Side.ALLIES
        );

        assertEquals(MapPanel.EnterIssuingOrdersOutcome.NO_PROGRESS, outcome);
    }

    @Test
    void enterIssuingOrdersOutcome_switchesCommandSide_whenCurrentSideIsCompleteAndMatchesInitialActiveSide() {
        var outcome = MapPanel.enterIssuingOrdersOutcome(
            true,
            Side.ALLIES,
            Side.ALLIES
        );

        assertEquals(MapPanel.EnterIssuingOrdersOutcome.SWITCH_COMMAND_SIDE, outcome);
    }

    @Test
    void enterIssuingOrdersOutcome_endsTurn_whenCurrentSideIsCompleteAndIsSecondCommandSide() {
        var outcome = MapPanel.enterIssuingOrdersOutcome(
            true,
            Side.AXIS,
            Side.ALLIES
        );

        assertEquals(MapPanel.EnterIssuingOrdersOutcome.END_TURN, outcome);
    }

    @Test
    void applyEnterIssuingOrdersOutcome_doesNotInvokeCallbacks_whenOutcomeIsNoProgress() {
        var sideSwitchCalls = new AtomicInteger();
        var endTurnCalls = new AtomicInteger();

        MapPanel.applyEnterIssuingOrdersOutcome(
            MapPanel.EnterIssuingOrdersOutcome.NO_PROGRESS,
            sideSwitchCalls::incrementAndGet,
            endTurnCalls::incrementAndGet
        );

        assertEquals(0, sideSwitchCalls.get());
        assertEquals(0, endTurnCalls.get());
    }

    @Test
    void applyEnterIssuingOrdersOutcome_invokesOnlySideSwitch_whenOutcomeIsSwitchCommandSide() {
        var sideSwitchCalls = new AtomicInteger();
        var endTurnCalls = new AtomicInteger();

        MapPanel.applyEnterIssuingOrdersOutcome(
            MapPanel.EnterIssuingOrdersOutcome.SWITCH_COMMAND_SIDE,
            sideSwitchCalls::incrementAndGet,
            endTurnCalls::incrementAndGet
        );

        assertEquals(1, sideSwitchCalls.get());
        assertEquals(0, endTurnCalls.get());
    }

    @Test
    void applyEnterIssuingOrdersOutcome_invokesOnlyEndTurn_whenOutcomeIsEndTurn() {
        var sideSwitchCalls = new AtomicInteger();
        var endTurnCalls = new AtomicInteger();

        MapPanel.applyEnterIssuingOrdersOutcome(
            MapPanel.EnterIssuingOrdersOutcome.END_TURN,
            sideSwitchCalls::incrementAndGet,
            endTurnCalls::incrementAndGet
        );

        assertEquals(0, sideSwitchCalls.get());
        assertEquals(1, endTurnCalls.get());
    }
}
