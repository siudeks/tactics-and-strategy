package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import game.domain.UnitId;
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
        var initialState = runtime.getCurrentCampaignState();

        runtime.simulateOneTurn();

        var updatedState = runtime.getCurrentCampaignState();
        assertEquals(1, initialState.turnNumber());
        assertEquals(2, updatedState.turnNumber());
        assertEquals("AXIS", updatedState.activeSide().name());
        assertIterableEquals(initialState.units(), updatedState.units());
    }

    @Test
    void gameRuntime_assignMoveTarget_isAcceptedWhileClockIsAdvancing() {
        // REQ-RTS-003: movement commands must be accepted at any point during real-time play,
        // with no phase gate blocking the assignment.
        var unitId = runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.side().name().equals("ALLIES"))
            .findFirst()
            .map(game.domain.Unit::id)
            .orElseThrow(() -> new AssertionError("Expected at least one ALLIES unit"));

        runtime.advanceClock(0.5f);
        runtime.assignMoveTarget(unitId, 2, 3);
        runtime.advanceClock(0.5f);

        var orderPresent = runtime.getCurrentCampaignState().pendingOrders().stream()
            .anyMatch(o -> o.unitId().equals(UnitId.of(unitId)) && o.type() == game.domain.OrderType.MOVE);
        assertTrue(orderPresent, "MOVE order should persist after clock advances");
    }

    @Test
    void gameRuntime_assignMoveTargetOrder_persistsOrderWithoutStartingProjection() {
        var unitId = firstAlliesUnitId();
        var unit = findUnit(unitId);

        var outcome = runtime.assignMoveTargetOrder(unitId, unit.tileX() + 2, unit.tileY());

        var orderPresent = runtime.getCurrentCampaignState().pendingOrders().stream()
            .anyMatch(o -> o.unitId().equals(UnitId.of(unitId)) && o.type() == game.domain.OrderType.MOVE);
        assertAll(
            () -> assertEquals(game.engine.MoveCommandOutcome.ACCEPTED, outcome.outcome()),
            () -> assertTrue(orderPresent, "MOVE order should be persisted in pendingOrders"),
            () -> assertFalse(runtime.rtsMovementPositions().containsKey(unitId),
                "Projection should remain inactive until explicitly started")
        );
    }

    @Test
    void gameRuntime_projectMoveTarget_startsProjectionWithoutMutatingPendingOrders() {
        var unitId = firstAlliesUnitId();
        var unit = findUnit(unitId);
        var ordersBefore = runtime.getCurrentCampaignState().pendingOrders().size();

        var projected = runtime.projectMoveTarget(unitId, unit.tileX() + 2, unit.tileY());

        assertAll(
            () -> assertTrue(projected, "Known unit should start projection"),
            () -> assertEquals(ordersBefore, runtime.getCurrentCampaignState().pendingOrders().size(),
                "Projection must not mutate pendingOrders"),
            () -> assertTrue(runtime.rtsMovementPositions().containsKey(unitId),
                "Projected unit should appear in RTS positions")
        );
    }

    @Test
    void rtsMovement_assignTarget_immediatelyAppearsInRtsPositions() {
        // REQ-RTS-MOVE-001: unit movement starts immediately on target assignment
        var unitId = firstAlliesUnitId();
        var unit = findUnit(unitId);

        runtime.assignMoveTarget(unitId, unit.tileX() + 4, unit.tileY());

        java.util.Map<String, float[]> positions = runtime.rtsMovementPositions();
        assertTrue(positions.containsKey(unitId), "Moving unit should appear in RTS positions immediately after assignment");
    }

    @Test
    void rtsMovement_afterAdvanceMovements_positionChanges() {
        // REQ-RTS-MOVE-001: unit moves toward target as time passes
        var unitId = firstAlliesUnitId();
        var unit = findUnit(unitId);
        var targetX = unit.tileX() + 4; // 4 tiles away → totalSeconds = 4.0
        runtime.assignMoveTarget(unitId, targetX, unit.tileY());

        runtime.advanceMovements(1f); // 1 second → 25% of the way

        var pos = runtime.rtsMovementPositions().get(unitId);
        assertNotNull(pos, "Unit should still be moving after 1s (out of 4s)");
        assertEquals(unit.tileX() + 1f, pos[0], 0.0001f, "Unit should be 1 tile closer to target");
    }

    @Test
    void rtsMovement_whenClockPaused_positionDoesNotChange() {
        // REQ-RTS-MOVE-002: movement pauses when game clock is paused
        var unitId = firstAlliesUnitId();
        var unit = findUnit(unitId);
        runtime.assignMoveTarget(unitId, unit.tileX() + 4, unit.tileY());
        runtime.togglePause();

        runtime.advanceMovements(2f); // 2 seconds while paused

        var pos = runtime.rtsMovementPositions().get(unitId);
        assertNotNull(pos, "Unit should still have active movement");
        assertEquals(unit.tileX(), pos[0], 0.0001f, "Position should not change while paused");
    }

    @Test
    void rtsMovement_unitArrivesAtTarget_campaignStateUpdated() {
        // REQ-RTS-MOVE-001: arrived unit's campaign-state position is updated
        var unitId = firstAlliesUnitId();
        var unit = findUnit(unitId);
        var targetX = unit.tileX() + 2;
        var targetY = unit.tileY() + 0;
        runtime.assignMoveTarget(unitId, targetX, targetY);

        runtime.advanceMovements(10f); // well beyond travel time

        var arrived = runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.id().equals(unitId))
            .findFirst()
            .orElseThrow();
        assertAll(
            () -> assertEquals(targetX, arrived.tileX(), "tileX should be updated to target"),
            () -> assertEquals(targetY, arrived.tileY(), "tileY should be updated to target"),
            () -> assertTrue(runtime.rtsMovementPositions().isEmpty(), "No more active RTS movement after arrival")
        );
    }

    private String firstAlliesUnitId() {
        return runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.side().name().equals("ALLIES"))
            .findFirst()
            .map(game.domain.Unit::id)
            .orElseThrow(() -> new AssertionError("Expected at least one ALLIES unit"));
    }

    private game.domain.Unit findUnit(String unitId) {
        return runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.id().equals(unitId))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Unit not found: " + unitId));
    }

}
