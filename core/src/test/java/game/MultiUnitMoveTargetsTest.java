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
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// REQ-ORD-MOVE-003: Multiple units on the same side may receive distinct MOVE targets
// in a single command phase, and each unit advances toward its own target independently.
class MultiUnitMoveTargetsTest {

    private record UnitStart(String id, int x, int y) {}

    private static GameRuntime runtimeWith(List<UnitStart> starts) {
        var units = starts.stream()
            .map(s -> new Unit(s.id(), Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, s.x(), s.y()))
            .toList();
        var scenarioDefinition = new ScenarioDefinition(
            "test-move-003", "Test Move 003", 10, 10, TerrainType.SAND, List.of()
        );
        var state = new CampaignState(
            "c1", "test-move-003", 1, Side.ALLIES, units, List.of()
        );
        return new GameRuntime(new LoadedScenario(scenarioDefinition, state));
    }

    private static Unit unitFrom(GameRuntime runtime, String unitId) {
        return runtime.getCurrentCampaignState().units().stream()
            .filter(u -> u.id().equals(unitId))
            .findFirst()
            .orElseThrow();
    }

    private static Map<String, Unit> unitsById(GameRuntime runtime) {
        return runtime.getCurrentCampaignState().units().stream()
            .collect(Collectors.toMap(Unit::id, unit -> unit));
    }

    @Test
    void simulateOneTurn_withDistinctMoveTargetsForTwoUnits_movesEachToOwnTarget() {
        var alphaStart = new UnitStart("alpha", 1, 1);
        var bravoStart = new UnitStart("bravo", 8, 8);
        var runtime = runtimeWith(List.of(alphaStart, bravoStart));

        int alphaTargetX = 3;
        int alphaTargetY = 2;
        int bravoTargetX = 6;
        int bravoTargetY = 7;

        runtime.assignMoveTarget("alpha", alphaTargetX, alphaTargetY);
        runtime.assignMoveTarget("bravo", bravoTargetX, bravoTargetY);
        runtime.simulateOneTurn();

        var alpha = unitFrom(runtime, "alpha");
        var bravo = unitFrom(runtime, "bravo");

        // Each unit advanced from its own start position.
        assertNotEquals(alphaStart.x(), alpha.tileX(), "alpha must have advanced from start X");
        assertNotEquals(bravoStart.x(), bravo.tileX(), "bravo must have advanced from start X");

        // Each unit reached its own independently-assigned target.
        assertEquals(alphaTargetX, alpha.tileX());
        assertEquals(alphaTargetY, alpha.tileY());
        assertEquals(bravoTargetX, bravo.tileX());
        assertEquals(bravoTargetY, bravo.tileY());
    }

    @Test
    void simulateOneTurn_withOppositeCornerTargets_unitsMoveIndependently() {
        var alphaStart = new UnitStart("alpha", 1, 1);
        var bravoStart = new UnitStart("bravo", 8, 8);
        var runtime = runtimeWith(List.of(alphaStart, bravoStart));

        // Alpha heads toward bottom-right; bravo heads toward top-left — proves
        // targets are not swapped or shared between units.
        int alphaTargetX = 7;
        int alphaTargetY = 6;
        int bravoTargetX = 2;
        int bravoTargetY = 3;

        runtime.assignMoveTarget("alpha", alphaTargetX, alphaTargetY);
        runtime.assignMoveTarget("bravo", bravoTargetX, bravoTargetY);
        runtime.simulateOneTurn();

        var alpha = unitFrom(runtime, "alpha");
        var bravo = unitFrom(runtime, "bravo");

        assertEquals(alphaTargetX, alpha.tileX());
        assertEquals(alphaTargetY, alpha.tileY());
        assertEquals(bravoTargetX, bravo.tileX());
        assertEquals(bravoTargetY, bravo.tileY());
    }

    @Test
    void simulateOneTurn_twoUnitsContendForSameTile_onlyOneUnitOccupiesDestination() {
        var runtime = runtimeWith(List.of(
            new UnitStart("alpha", 1, 1),
            new UnitStart("bravo", 3, 1)
        ));

        runtime.assignMoveTarget("alpha", 2, 1);
        runtime.assignMoveTarget("bravo", 2, 1);
        runtime.simulateOneTurn();

        var unitsById = unitsById(runtime);
        var alpha = unitsById.get("alpha");
        var bravo = unitsById.get("bravo");
        assertNotNull(alpha);
        assertNotNull(bravo);

        assertEquals(2, alpha.tileX());
        assertEquals(1, alpha.tileY());
        assertEquals(3, bravo.tileX());
        assertEquals(1, bravo.tileY());
    }

    @Test
    void simulateOneTurn_threeUnitsContendForSameTile_lowestCostCandidateWins() {
        var runtime = runtimeWith(List.of(
            new UnitStart("alpha", 1, 1),
            new UnitStart("bravo", 3, 1),
            new UnitStart("charlie", 2, 3)
        ));

        runtime.assignMoveTarget("alpha", 2, 2);
        runtime.assignMoveTarget("bravo", 2, 2);
        runtime.assignMoveTarget("charlie", 2, 2);
        runtime.simulateOneTurn();

        var unitsById = unitsById(runtime);
        var alpha = unitsById.get("alpha");
        var bravo = unitsById.get("bravo");
        var charlie = unitsById.get("charlie");
        assertNotNull(alpha);
        assertNotNull(bravo);
        assertNotNull(charlie);

        assertEquals(1, alpha.tileX());
        assertEquals(1, alpha.tileY());
        assertEquals(3, bravo.tileX());
        assertEquals(1, bravo.tileY());
        assertEquals(2, charlie.tileX());
        assertEquals(2, charlie.tileY());
    }
}
