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
        var scenarioDefinition = new ScenarioDefinition(
            "test-move-002", "Test Move 002", 10, 10, defaultTerrain, List.of()
        );
        var state = new CampaignState(
            "c1", "test-move-002", 1, Side.ALLIES, List.of(unit), List.of()
        );
        return new GameRuntime(new LoadedScenario(scenarioDefinition, state));
    }

    private static Unit unitFrom(GameRuntime runtime) {
        return runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.id().equals(UNIT_ID))
            .findFirst()
            .orElseThrow();
    }

    @Test
    void simulateOneTurn_withAssignedMoveTarget_movesUnitToTarget() {
        var runtime = runtimeFor(TerrainType.SAND);
        int targetX = 2;
        int targetY = 1;

        runtime.assignMoveTarget(UNIT_ID, targetX, targetY);
        runtime.simulateOneTurn();

        Unit moved = unitFrom(runtime);
        assertEquals(targetX, moved.tileX());
        assertEquals(targetY, moved.tileY());
    }

    @Test
    void simulateOneTurn_withOutOfBoundsTarget_leavesUnitInPlaceAndDoesNotThrow() {
        var runtime = runtimeFor(TerrainType.SAND);

        assertDoesNotThrow(() -> {
            runtime.assignMoveTarget(UNIT_ID, -1, 5);
            runtime.simulateOneTurn();
        });

        Unit unit = unitFrom(runtime);
        assertEquals(START_X, unit.tileX());
        assertEquals(START_Y, unit.tileY());
    }

    @Test
    void simulateOneTurn_withImpassableTerrainTarget_leavesUnitInPlaceAndDoesNotThrow() {
        var runtime = runtimeFor(TerrainType.VOID);

        assertDoesNotThrow(() -> {
            runtime.assignMoveTarget(UNIT_ID, 3, 3);
            runtime.simulateOneTurn();
        });

        Unit unit = unitFrom(runtime);
        assertEquals(START_X, unit.tileX());
        assertEquals(START_Y, unit.tileY());
    }
}
