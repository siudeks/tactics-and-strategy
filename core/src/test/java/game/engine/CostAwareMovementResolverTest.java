package game.engine;

import game.domain.Order;
import game.domain.OrderType;
import game.domain.ScenarioDefinition;
import game.domain.Side;
import game.domain.TerrainType;
import game.domain.TileCoordinate;
import game.domain.Unit;
import game.domain.UnitSize;
import game.domain.UnitType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CostAwareMovementResolverTest {

    private static final Unit UNIT = new Unit(
        "u1",
        Side.ALLIES,
        UnitType.MEDIUM_TANK,
        UnitSize.BATTALION,
        0,
        0
    );

    private static ScenarioDefinition scenario(String id, TerrainType defaultTerrain) {
        return new ScenarioDefinition(id, id, 6, 6, defaultTerrain, List.of());
    }

    @Test
    void resolve_withSameDistanceOverDifferentTerrain_assignsDifferentTotalCost() {
        Order move = new Order("o1", UNIT.id(), Side.ALLIES, OrderType.MOVE, 0, 2);

        CostAwareMovementResolver sandResolver = new CostAwareMovementResolver(scenario("sand", TerrainType.SAND));
        CostAwareMovementResolver mountainResolver = new CostAwareMovementResolver(scenario("mountain", TerrainType.MOUNTAIN));

        var sandResult = sandResolver.resolve(UNIT, move).orElseThrow();
        var mountainResult = mountainResolver.resolve(UNIT, move).orElseThrow();

        assertEquals(2, sandResult.totalCost());
        assertEquals(6, mountainResult.totalCost());
    }

    @Test
    void resolve_equalCostRoute_usesDeterministicTieBreak() {
        CostAwareMovementResolver resolver = new CostAwareMovementResolver(scenario("tie", TerrainType.SAND));

        var route = resolver.resolve(new TileCoordinate(0, 0), new TileCoordinate(1, 1))
            .orElseThrow()
            .route();

        assertEquals(List.of(
            new TileCoordinate(0, 0),
            new TileCoordinate(1, 0),
            new TileCoordinate(1, 1)
        ), route);
    }

    @Test
    void resolve_repeatedRunsReturnIdenticalRouteAndCost() {
        CostAwareMovementResolver resolver = new CostAwareMovementResolver(scenario("det", TerrainType.SAND));
        var expected = resolver.resolve(new TileCoordinate(0, 0), new TileCoordinate(3, 2)).orElseThrow();

        for (int i = 0; i < 20; i++) {
            var next = resolver.resolve(new TileCoordinate(0, 0), new TileCoordinate(3, 2)).orElseThrow();
            assertEquals(expected.totalCost(), next.totalCost());
            assertEquals(expected.route(), next.route());
        }
    }

    @Test
    void resolve_impassableDefaultTerrain_returnsEmptyResult() {
        CostAwareMovementResolver resolver = new CostAwareMovementResolver(scenario("void", TerrainType.VOID));

        var maybeResult = resolver.resolve(new TileCoordinate(1, 1), new TileCoordinate(1, 2));

        assertNotNull(maybeResult);
        assertTrue(maybeResult.isEmpty());
    }
}