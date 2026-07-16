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
        Unit unit = new Unit("u1", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        Order order = Order.of("o1", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(3, 4));
        CampaignState state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        TurnResult result = engineFor(sandMap()).runOneTurn(state);

        Unit movedUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(3, movedUnit.tileX());
        assertEquals(4, movedUnit.tileY());
    }

    @Test
    void moveOrder_outOfBounds_unitStaysInPlace() {
        Unit unit = new Unit("u1", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        Order order = Order.of("o1", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(-1, 0));
        CampaignState state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        TurnResult result = engineFor(sandMap()).runOneTurn(state);

        Unit movedUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(1, movedUnit.tileX());
        assertEquals(1, movedUnit.tileY());
    }

    @Test
    void moveOrder_toVoidTile_unitStaysInPlace() {
        Unit unit = new Unit("u1", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        Order order = Order.of("o1", "u1", Side.ALLIES, OrderType.MOVE, new TileCoordinate(3, 3));
        CampaignState state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        // Void terrain — all moves to any tile are blocked
        TurnResult result = engineFor(voidMap()).runOneTurn(state);

        Unit movedUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(1, movedUnit.tileX());
        assertEquals(1, movedUnit.tileY());
    }

    @Test
    void holdOrder_unitDoesNotMove() {
        Unit unit = new Unit("u1", Side.ALLIES, UnitType.FOOT_INFANTRY, UnitSize.BRIGADE, 5, 5);
        Order order = Order.of("o1", "u1", Side.ALLIES, OrderType.HOLD, new TileCoordinate(0, 0));
        CampaignState state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(unit), List.of(order)
        );

        TurnResult result = engineFor(sandMap()).runOneTurn(state);

        Unit heldUnit = result.state().units().stream()
            .filter(u -> u.id().equals("u1"))
            .findFirst()
            .orElseThrow();
        assertEquals(5, heldUnit.tileX());
        assertEquals(5, heldUnit.tileY());
    }

    @Test
    void moveOrders_twoUnitsContendForSameDestination_onlyDeterministicWinnerMoves() {
        Unit alpha = new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        Unit bravo = new Unit("bravo", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 1);
        Order alphaOrder = Order.of("o-alpha", "alpha", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 1));
        Order bravoOrder = Order.of("o-bravo", "bravo", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 1));
        CampaignState state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(alpha, bravo), List.of(alphaOrder, bravoOrder)
        );

        TurnResult result = engineFor(sandMap()).runOneTurn(state);
        Map<String, Unit> unitsById = unitsById(result);

        Unit movedAlpha = unitsById.get("alpha");
        Unit blockedBravo = unitsById.get("bravo");
        assertNotNull(movedAlpha);
        assertNotNull(blockedBravo);
        assertEquals(2, movedAlpha.tileX());
        assertEquals(1, movedAlpha.tileY());
        assertEquals(3, blockedBravo.tileX());
        assertEquals(1, blockedBravo.tileY());
    }

    @Test
    void moveOrders_threeUnitsContendForSameDestination_lowestCostWinnerMoves() {
        Unit alpha = new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        Unit bravo = new Unit("bravo", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 1);
        Unit charlie = new Unit("charlie", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 2, 3);
        Order alphaOrder = Order.of("o-alpha", "alpha", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        Order bravoOrder = Order.of("o-bravo", "bravo", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        Order charlieOrder = Order.of("o-charlie", "charlie", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        CampaignState state = new CampaignState(
            "c1",
            "s1",
            1,
            Side.ALLIES,
            List.of(alpha, bravo, charlie),
            List.of(alphaOrder, bravoOrder, charlieOrder)
        );

        TurnResult result = engineFor(sandMap()).runOneTurn(state);
        Map<String, Unit> unitsById = unitsById(result);

        Unit movedCharlie = unitsById.get("charlie");
        Unit blockedAlpha = unitsById.get("alpha");
        Unit blockedBravo = unitsById.get("bravo");
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
        Unit alpha = new Unit("alpha", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        Unit bravo = new Unit("bravo", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 3, 1);
        Unit charlie = new Unit("charlie", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 2, 3);
        Order alphaOrder = Order.of("o-alpha", "alpha", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        Order bravoOrder = Order.of("o-bravo", "bravo", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));
        Order charlieOrder = Order.of("o-charlie", "charlie", Side.ALLIES, OrderType.MOVE, new TileCoordinate(2, 2));

        CampaignState orderedState = new CampaignState(
            "c1",
            "s1",
            1,
            Side.ALLIES,
            List.of(alpha, bravo, charlie),
            List.of(alphaOrder, bravoOrder, charlieOrder)
        );
        CampaignState shuffledState = new CampaignState(
            "c1",
            "s1",
            1,
            Side.ALLIES,
            List.of(bravo, charlie, alpha),
            List.of(charlieOrder, alphaOrder, bravoOrder)
        );

        TurnResult orderedResult = engineFor(sandMap()).runOneTurn(orderedState);
        TurnResult shuffledResult = engineFor(sandMap()).runOneTurn(shuffledState);

        assertEquals(unitsById(orderedResult), unitsById(shuffledResult));
    }

    @Test
    void turnNumber_incrementsAfterRunOneTurn() {
        CampaignState state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(), List.of()
        );

        TurnResult result = engineFor(sandMap()).runOneTurn(state);

        assertEquals(2, result.state().turnNumber());
    }

    @Test
    void activeSide_flipsAfterRunOneTurn() {
        CampaignState state = new CampaignState(
            "c1", "s1", 1, Side.ALLIES, List.of(), List.of()
        );

        TurnResult result = engineFor(sandMap()).runOneTurn(state);

        assertEquals(Side.AXIS, result.state().activeSide());
    }
}
