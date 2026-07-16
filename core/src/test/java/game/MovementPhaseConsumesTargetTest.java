package game;

import org.junit.jupiter.api.Test;
import game.domain.CampaignState;
import game.domain.ScenarioDefinition;
import game.domain.Side;
import game.domain.TerrainType;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import game.engine.GameRuntime;
import game.engine.MoveCommandOutcome;
import game.scenario.LoadedScenario;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

// REQ-ORD-MOVE-002: Movement phase consumes assigned MOVE targets and applies validation
// (bounds + impassable terrain) when the user ends the turn via GameRuntime.simulateOneTurn().
class MovementPhaseConsumesTargetTest {

    private static final String UNIT_ID = "u1";
    private static final int START_X = 1;
    private static final int START_Y = 1;

    private static GameRuntime runtimeFor(TerrainType defaultTerrain) {
        var unit = new Unit(UNIT_ID, Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, START_X, START_Y);
        return runtimeForUnits(defaultTerrain, List.of(unit));
    }

    private static GameRuntime runtimeForUnits(TerrainType defaultTerrain, List<Unit> units) {
        var scenarioDefinition = new ScenarioDefinition(
            "test-move-002", "Test Move 002", 10, 10, defaultTerrain, List.of()
        );
        var state = new CampaignState(
            "c1", "test-move-002", 1, Side.ALLIES, units, List.of()
        );
        return new GameRuntime(new LoadedScenario(scenarioDefinition, state));
    }

    private static Unit unitFrom(GameRuntime runtime) {
        return unitFrom(runtime, UNIT_ID);
    }

    private static Unit unitFrom(GameRuntime runtime, String unitId) {
        return runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.id().equals(unitId))
            .findFirst()
            .orElseThrow();
    }

    @Test
    void simulateOneTurn_withAssignedMoveTarget_movesUnitToTarget() {
        var runtime = runtimeFor(TerrainType.SAND);
        var targetX = 2;
        var targetY = 1;

        var assignment = runtime.assignMoveTarget(UNIT_ID, targetX, targetY);
        assertEquals(MoveCommandOutcome.ACCEPTED, assignment.outcome());
        runtime.simulateOneTurn();

        var moved = unitFrom(runtime);
        assertEquals(targetX, moved.tileX());
        assertEquals(targetY, moved.tileY());
    }

    @Test
    void simulateOneTurn_withOutOfBoundsTarget_leavesUnitInPlaceAndDoesNotThrow() {
        var runtime = runtimeFor(TerrainType.SAND);

        assertDoesNotThrow(() -> {
            var assignment = runtime.assignMoveTarget(UNIT_ID, -1, 5);
            assertEquals(MoveCommandOutcome.ACCEPTED, assignment.outcome());
            runtime.simulateOneTurn();
        });

        var unit = unitFrom(runtime);
        assertEquals(START_X, unit.tileX());
        assertEquals(START_Y, unit.tileY());
    }

    @Test
    void simulateOneTurn_withImpassableTerrainTarget_leavesUnitInPlaceAndDoesNotThrow() {
        var runtime = runtimeFor(TerrainType.VOID);

        assertDoesNotThrow(() -> {
            var assignment = runtime.assignMoveTarget(UNIT_ID, 3, 3);
            assertEquals(MoveCommandOutcome.ACCEPTED, assignment.outcome());
            runtime.simulateOneTurn();
        });

        var unit = unitFrom(runtime);
        assertEquals(START_X, unit.tileX());
        assertEquals(START_Y, unit.tileY());
    }

    @Test
    void simulateOneTurn_withOccupiedDestinationAndStationaryIncumbent_blocksIncomingMove() {
        var mover = new Unit(UNIT_ID, Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, START_X, START_Y);
        var incumbent = new Unit("u2", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 2, 1);
        var runtime = runtimeForUnits(TerrainType.SAND, List.of(mover, incumbent));

        var assignment = runtime.assignMoveTarget(UNIT_ID, 2, 1);
        assertEquals(MoveCommandOutcome.ACCEPTED, assignment.outcome());
        runtime.simulateOneTurn();

        var blockedMover = unitFrom(runtime, UNIT_ID);
        var stationaryIncumbent = unitFrom(runtime, "u2");
        assertEquals(START_X, blockedMover.tileX());
        assertEquals(START_Y, blockedMover.tileY());
        assertEquals(2, stationaryIncumbent.tileX());
        assertEquals(1, stationaryIncumbent.tileY());
    }
}
