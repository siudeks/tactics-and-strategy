package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.domain.CampaignState;
import game.engine.GameRuntime;
import game.platform.ScenarioLoader;

import static org.junit.jupiter.api.Assertions.*;

class GameRuntimeTurnIntegrationTest {

    private GameRuntime runtime;

    @BeforeEach
    void setUp() {
        runtime = new GameRuntime(ScenarioLoader.loadBootstrapScenario());
    }

    @Test
    void gameRuntime_initialTurnNumber_isOne() {
        assertEquals(1, runtime.getTurnNumber());
    }

    @Test
    void gameRuntime_initialActiveSide_isAllies() {
        assertEquals("ALLIES", runtime.getActiveSideCode());
    }

    @Test
    void gameRuntime_simulateOneTurn_incrementsTurnNumber() {
        runtime.simulateOneTurn();
        assertEquals(2, runtime.getTurnNumber());
    }

    @Test
    void gameRuntime_simulateOneTurn_flipsActiveSide() {
        runtime.simulateOneTurn();
        assertEquals("AXIS", runtime.getActiveSideCode());
    }

    @Test
    void gameRuntime_currentCampaignState_reflectsPostSimulationState() {
        CampaignState initialState = runtime.getCurrentCampaignState();

        runtime.simulateOneTurn();

        CampaignState updatedState = runtime.getCurrentCampaignState();
        assertEquals(1, initialState.turnNumber());
        assertEquals(2, updatedState.turnNumber());
        assertEquals("AXIS", updatedState.activeSide().name());
        assertIterableEquals(initialState.units(), updatedState.units());
    }

    @Test
    void gameRuntime_assignMoveTarget_isAcceptedWhileClockIsAdvancing() {
        // REQ-RTS-003: movement commands must be accepted at any point during real-time play,
        // with no phase gate blocking the assignment.
        String unitId = runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.side().name().equals("ALLIES"))
            .findFirst()
            .map(game.domain.Unit::id)
            .orElseThrow(() -> new AssertionError("Expected at least one ALLIES unit"));

        runtime.advanceClock(0.5f);
        runtime.assignMoveTarget(unitId, 2, 3);
        runtime.advanceClock(0.5f);

        boolean orderPresent = runtime.getCurrentCampaignState().pendingOrders().stream()
            .anyMatch(o -> o.unitId().equals(unitId) && o.type() == game.domain.OrderType.MOVE);
        assertTrue(orderPresent, "MOVE order should persist after clock advances");
    }

}
