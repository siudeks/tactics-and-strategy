package game.engine;

import game.domain.Order;
import game.domain.OrderType;
import game.domain.ScenarioDefinition;
import game.domain.TerrainType;
import game.domain.TileCoordinate;
import game.domain.Unit;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import org.jspecify.annotations.Nullable;

final class CostAwareMovementResolver {

    record ResolvedMove(TileCoordinate destination, int totalCost, List<TileCoordinate> route) {
        ResolvedMove {
            route = List.copyOf(route);
        }
    }

    private record FrontierEntry(TileCoordinate tile, int totalCost) {
    }

    private static final Comparator<FrontierEntry> FRONTIER_ORDER = Comparator
        .comparingInt(FrontierEntry::totalCost)
        .thenComparingInt(entry -> entry.tile().y())
        .thenComparingInt(entry -> entry.tile().x());

    private static final int[][] ORTHOGONAL_STEPS = {
        {1, 0},
        {0, 1},
        {-1, 0},
        {0, -1}
    };

    private final ScenarioDefinition scenarioDefinition;

    CostAwareMovementResolver(ScenarioDefinition scenarioDefinition) {
        this.scenarioDefinition = scenarioDefinition;
    }

    Optional<ResolvedMove> resolve(Unit unit, Order order) {
        if (order.type() != OrderType.MOVE) {
            return Optional.empty();
        }
        TileCoordinate start = new TileCoordinate(unit.tileX(), unit.tileY());
        TileCoordinate target = new TileCoordinate(order.targetX(), order.targetY());
        return resolve(start, target);
    }

    Optional<ResolvedMove> resolve(TileCoordinate start, TileCoordinate target) {
        if (!TurnEngine.isValidMove(scenarioDefinition, start.x(), start.y())) {
            return Optional.empty();
        }
        if (!TurnEngine.isValidMove(scenarioDefinition, target.x(), target.y())) {
            return Optional.empty();
        }
        if (start.equals(target)) {
            return Optional.of(new ResolvedMove(target, 0, List.of(start)));
        }

        int stepCost = terrainStepCost(scenarioDefinition.defaultTerrain());
        PriorityQueue<FrontierEntry> frontier = new PriorityQueue<>(FRONTIER_ORDER);
        Map<TileCoordinate, Integer> bestCostByTile = new HashMap<>();
        Map<TileCoordinate, TileCoordinate> previousByTile = new HashMap<>();

        bestCostByTile.put(start, 0);
        frontier.add(new FrontierEntry(start, 0));

        while (!frontier.isEmpty()) {
            FrontierEntry currentEntry = frontier.poll();
            Integer knownCost = bestCostByTile.get(currentEntry.tile());
            if (knownCost == null || currentEntry.totalCost() != knownCost) {
                continue;
            }

            for (int[] step : ORTHOGONAL_STEPS) {
                TileCoordinate neighbour = new TileCoordinate(
                    currentEntry.tile().x() + step[0],
                    currentEntry.tile().y() + step[1]
                );
                if (!TurnEngine.isValidMove(scenarioDefinition, neighbour.x(), neighbour.y())) {
                    continue;
                }

                int candidateCost = currentEntry.totalCost() + stepCost;
                Integer neighbourCost = bestCostByTile.get(neighbour);
                if (neighbourCost == null || candidateCost < neighbourCost) {
                    bestCostByTile.put(neighbour, candidateCost);
                    previousByTile.put(neighbour, currentEntry.tile());
                    frontier.add(new FrontierEntry(neighbour, candidateCost));
                    continue;
                }

                if (candidateCost == neighbourCost
                    && isPreferredTieBreakPredecessor(currentEntry.tile(), previousByTile.get(neighbour))) {
                    previousByTile.put(neighbour, currentEntry.tile());
                    frontier.add(new FrontierEntry(neighbour, candidateCost));
                }
            }
        }

        Integer targetCost = bestCostByTile.get(target);
        if (targetCost == null) {
            return Optional.empty();
        }
        return Optional.of(new ResolvedMove(target, targetCost, buildRoute(start, target, previousByTile)));
    }

    private static List<TileCoordinate> buildRoute(
        TileCoordinate start,
        TileCoordinate target,
        Map<TileCoordinate, TileCoordinate> previousByTile
    ) {
        ArrayDeque<TileCoordinate> reversedRoute = new ArrayDeque<>();
        TileCoordinate cursor = target;
        reversedRoute.addFirst(cursor);
        while (!cursor.equals(start)) {
            TileCoordinate predecessor = previousByTile.get(cursor);
            if (predecessor == null) {
                return List.of();
            }
            cursor = predecessor;
            reversedRoute.addFirst(cursor);
        }
        return new ArrayList<>(reversedRoute);
    }

    private static int terrainStepCost(TerrainType terrainType) {
        return switch (terrainType) {
            case SAND -> 1;
            case MOUNTAIN -> 3;
            case VOID, WATER -> throw new IllegalArgumentException("Impassable terrain has no movement cost");
        };
    }

    private static boolean isPreferredTieBreakPredecessor(
        TileCoordinate candidate,
        @Nullable TileCoordinate current
    ) {
        if (current == null) {
            return true;
        }
        if (candidate.y() != current.y()) {
            return candidate.y() < current.y();
        }
        return candidate.x() < current.x();
    }
}