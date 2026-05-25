package game;

import org.junit.jupiter.api.Test;
import game.domain.*;
import game.engine.DeterministicContext;
import game.engine.TurnEngine;
import game.engine.TurnResult;

import java.util.List;

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

    @Test
    void moveOrder_movesUnitToTarget() {
        Unit unit = new Unit("u1", Side.ALLIES, UnitType.MEDIUM_TANK, UnitSize.BATTALION, 1, 1);
        Order order = new Order("o1", "u1", Side.ALLIES, OrderType.MOVE, 3, 4);
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
        Order order = new Order("o1", "u1", Side.ALLIES, OrderType.MOVE, -1, 0);
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
        Order order = new Order("o1", "u1", Side.ALLIES, OrderType.MOVE, 3, 3);
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
        Order order = new Order("o1", "u1", Side.ALLIES, OrderType.HOLD, 0, 0);
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
