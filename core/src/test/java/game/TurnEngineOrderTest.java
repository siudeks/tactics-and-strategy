package game;

import org.junit.jupiter.api.Test;
import game.domain.*;
import game.engine.DeterministicContext;
import game.engine.TurnEngine;
import game.engine.TurnResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class TurnEngineOrderTest {

    private static ScenarioDefinition sandMap() {
        return new ScenarioDefinition("test", "Test", 10, 10, TerrainType.SAND, List.of());
    }

    private static ScenarioDefinition voidMap() {
        return new ScenarioDefinition("test-void", "Test Void", 10, 10, TerrainType.VOID, List.of());
    }

    private static TurnEngine engineFor(ScenarioDefinition sd) {
        return TurnEngine.fixedContext(DeterministicContext.withSeed(42L), sd);
    }

    private static Map<String, Unit> unitsById(TurnResult result) {
        return result.state().units().stream().collect(Collectors.toMap(Unit::id, unit -> unit));
    }

    @Test
    void moveOrder_movesUnitToTarget() {
        var unit = new Unit("u1", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        var order = Order.of("o1", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(3, 4));
        var state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        var result = engineFor(sandMap()).runOneTurn(state);

        var movedUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(3, movedUnit.tileX());
        assertEquals(4, movedUnit.tileY());
    }

    @Test
    void moveOrder_outOfBounds_unitStaysInPlace() {
        var unit = new Unit("u1", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        var order = Order.of("o1", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(-1, 0));
        var state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        var result = engineFor(sandMap()).runOneTurn(state);

        var movedUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(1, movedUnit.tileX());
        assertEquals(1, movedUnit.tileY());
    }

    @Test
    void moveOrder_toVoidTile_unitStaysInPlace() {
        var unit = new Unit("u1", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        var order = Order.of("o1", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(3, 3));
        var state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        // Void terrain — all moves to any tile are blocked
        var result = engineFor(voidMap()).runOneTurn(state);

        var movedUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(1, movedUnit.tileX());
        assertEquals(1, movedUnit.tileY());
    }

    @Test
    void holdOrder_unitDoesNotMove() {
        var unit = new Unit("u1", Side.ALLIES, UnitType.FOOT_INFANTRY, UnitSize.BRIGADE, 5, 5);
        var order = Order.of("o1", "u1", Side.ALLIES, OrderType.HOLD, new TileCoordinate(0, 0));
        var state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        var result = engineFor(sandMap()).runOneTurn(state);

        var heldUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(5, heldUnit.tileX());
        assertEquals(5, heldUnit.tileY());
    }

    @Test
    void moveOrders_twoUnitsContendForSameDestination_onlyDeterministicWinnerMoves() {
        var alpha = new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        var bravo = new Unit("bravo", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 1);
        var alphaOrder = Order.of("o-alpha", "alpha", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 1));
        var bravoOrder = Order.of("o-bravo", "bravo", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 1));
        var state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(alpha, bravo), List.of(alphaOrder, bravoOrder)
        );

        var result = engineFor(sandMap()).runOneTurn(state);
        var unitsById = unitsById(result);

        var movedAlpha = unitsById.get("alpha");
        var blockedBravo = unitsById.get("bravo");
        assertNotNull(movedAlpha);
        assertNotNull(blockedBravo);
        assertEquals(2, movedAlpha.tileX());
        assertEquals(1, movedAlpha.tileY());
        assertEquals(3, blockedBravo.tileX());
        assertEquals(1, blockedBravo.tileY());
    }

    @Test
    void moveOrders_threeUnitsContendForSameDestination_lowestCostWinnerMoves() {
        var alpha = new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        var bravo = new Unit("bravo", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 1);
        var charlie = new Unit("charlie", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 2, 3);
        var alphaOrder = Order.of("o-alpha", "alpha", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        var bravoOrder = Order.of("o-bravo", "bravo", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        var charlieOrder = Order.of("o-charlie", "charlie", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        var state = new CampaignState(
            "c1",
            "s1",
            1,
            Side.ALLIES,
            List.of(alpha, bravo, charlie),
            List.of(alphaOrder, bravoOrder, charlieOrder)
        );

        var result = engineFor(sandMap()).runOneTurn(state);
        var unitsById = unitsById(result);

        var movedCharlie = unitsById.get("charlie");
        var blockedAlpha = unitsById.get("alpha");
        var blockedBravo = unitsById.get("bravo");
        assertNotNull(movedCharlie);
        assertNotNull(blockedAlpha);
        assertNotNull(blockedBravo);
        assertEquals(2, movedCharlie.tileX());
        assertEquals(2, movedCharlie.tileY());
        assertEquals(1, blockedAlpha.tileX());
        assertEquals(1, blockedAlpha.tileY());
        assertEquals(3, blockedBravo.tileX());
        assertEquals(1, blockedBravo.tileY());
    }

    @Test
    void moveOrders_contestedDestination_isIndependentOfInputUnitOrder() {
        var alpha = new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        var bravo = new Unit("bravo", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 1);
        var charlie = new Unit("charlie", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 2, 3);
        var alphaOrder = Order.of("o-alpha", "alpha", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        var bravoOrder = Order.of("o-bravo", "bravo", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        var charlieOrder = Order.of("o-charlie", "charlie", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));

        var orderedState = new CampaignState(
            "c1",
            "s1",
            1,
            Side.ALLIES,
            List.of(alpha, bravo, charlie),
            List.of(alphaOrder, bravoOrder, charlieOrder)
        );
        var shuffledState = new CampaignState(
            "c1",
            "s1",
            1,
            Side.ALLIES,
            List.of(bravo, charlie, alpha),
            List.of(charlieOrder, alphaOrder, bravoOrder)
        );

        var orderedResult = engineFor(sandMap()).runOneTurn(orderedState);
        var shuffledResult = engineFor(sandMap()).runOneTurn(shuffledState);

        assertEquals(unitsById(orderedResult), unitsById(shuffledResult));
    }

    @Test
    void turnNumber_incrementsAfterRunOneTurn() {
        var state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(), List.of()
        );

        var result = engineFor(sandMap()).runOneTurn(state);

        assertEquals(2, result.state().turnNumber());
    }

    @Test
    void activeSide_flipsAfterRunOneTurn() {
        var state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(), List.of()
        );

        var result = engineFor(sandMap()).runOneTurn(state);

        assertEquals(Side.AXIS, result.state().activeSide());
    }
}
