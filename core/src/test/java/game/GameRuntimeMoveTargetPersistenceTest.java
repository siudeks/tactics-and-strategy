package game;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderType;
import game.domain.Side;
import game.domain.Unit;
import game.engine.GameRuntime;
import game.engine.MoveCommandOutcome;
import game.platform.ScenarioLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameRuntimeMoveTargetPersistenceTest {

    private GameRuntime runtime;

    @BeforeEach
    void setUp() {
        runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
    }

    @Test
    void assignMoveTarget_persistsMoveOrderInPendingOrders() {
        var unit = findUnit("allies-armor-1");

        var outcome = runtime.assignMoveTarget("allies-armor-1", 5, 4);

        var orders = runtime.getCurrentCampaignState().pendingOrders();
        assertEquals(MoveCommandOutcome.ACCEPTED, outcome.outcome());
        assertEquals(1, orders.size());
        var o = orders.get(0);
        assertEquals("move-allies-armor-1", o.id());
        assertEquals("allies-armor-1", o.unitId());
        assertEquals(OrderType.MOVE, o.type());
        assertEquals(5, o.target().x());
        assertEquals(4, o.target().y());
        assertEquals(unit.side(), o.side());
    }

    @Test
    void assignMoveTarget_replacesExistingMoveOrderForSameUnit() {
        var firstOutcome = runtime.assignMoveTarget("allies-armor-1", 3, 3);
        var secondOutcome = runtime.assignMoveTarget("allies-armor-1", 7, 2);

        var orders = runtime.getCurrentCampaignState().pendingOrders();
        assertEquals(MoveCommandOutcome.ACCEPTED, firstOutcome.outcome());
        assertEquals(MoveCommandOutcome.REPLACED_EXISTING, secondOutcome.outcome());
        assertEquals(1, orders.size());
        var o = orders.get(0);
        assertEquals("allies-armor-1", o.unitId());
        assertEquals(7, o.target().x());
        assertEquals(2, o.target().y());
        assertEquals("move-allies-armor-1", o.id());
    }

    @Test
    void assignMoveTarget_keepsIndependentOrdersForDifferentUnits() {
        runtime.assignMoveTarget("allies-armor-1", 3, 3);
        runtime.assignMoveTarget("allies-inf-1", 4, 1);

        var orders = runtime.getCurrentCampaignState().pendingOrders();
        assertEquals(2, orders.size());

        Order armor = orders.stream().filter(o -> o.unitId().equals("allies-armor-1")).findFirst().orElseThrow();
        Order inf = orders.stream().filter(o -> o.unitId().equals("allies-inf-1")).findFirst().orElseThrow();

        assertEquals("move-allies-armor-1", armor.id());
        assertEquals(3, armor.target().x());
        assertEquals(3, armor.target().y());

        assertEquals("move-allies-inf-1", inf.id());
        assertEquals(4, inf.target().x());
        assertEquals(1, inf.target().y());
    }

    @Test
    void assignMoveTarget_unknownUnitId_isSilentNoOp() {
        var before = runtime.getCurrentCampaignState();
        assertTrue(before.pendingOrders().isEmpty());

        var outcome = assertDoesNotThrow(() -> runtime.assignMoveTarget("does-not-exist", 1, 1));

        var after = runtime.getCurrentCampaignState();
        assertEquals(MoveCommandOutcome.UNKNOWN_UNIT, outcome.outcome());
        assertTrue(after.pendingOrders().isEmpty());
        assertIterableEquals(before.units(), after.units());
        assertEquals(before.turnNumber(), after.turnNumber());
        assertEquals(before.activeSide(), after.activeSide());
    }

    @Test
    void assignMoveTarget_outOfBoundsTarget_isPersistedForMovementPhaseValidation() {
        var outcome = runtime.assignMoveTarget("allies-armor-1", -1, 1);

        var after = runtime.getCurrentCampaignState();
        assertEquals(MoveCommandOutcome.ACCEPTED, outcome.outcome());
        assertEquals(1, after.pendingOrders().size());
        var order = after.pendingOrders().get(0);
        assertEquals(-1, order.target().x());
        assertEquals(1, order.target().y());
    }

    @Test
    void assignMoveTarget_nullUnitId_throwsNullPointerException() {
        var nullId = nullString();
        assertThrows(NullPointerException.class, () -> runtime.assignMoveTarget(nullId, 1, 1));
    }

    @SuppressWarnings("NullAway")
    private static String nullString() {
        return null;
    }

    @Test
    void assignMoveTarget_doesNotMutateUnits_orTurnNumber_orActiveSide() {
        var before = runtime.getCurrentCampaignState();

        runtime.assignMoveTarget("axis-inf-1", 0, 0);

        var after = runtime.getCurrentCampaignState();
        assertEquals(before.campaignId(), after.campaignId());
        assertEquals(before.scenarioId(), after.scenarioId());
        assertEquals(before.turnNumber(), after.turnNumber());
        assertSame(before.activeSide(), after.activeSide());
        assertIterableEquals(before.units(), after.units());
    }

    @Test
    void assignMoveTarget_usesUnitsOwnSide_notActiveSide() {
        // active side is ALLIES; assigning to an AXIS unit must record AXIS as the order's side
        runtime.assignMoveTarget("axis-armor-1", 6, 6);

        var orders = runtime.getCurrentCampaignState().pendingOrders();
        assertEquals(1, orders.size());
        assertEquals(Side.AXIS, orders.get(0).side());
    }

    private Unit findUnit(String id) {
        return runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.id().equals(id))
            .findFirst()
            .orElseThrow();
    }
}
