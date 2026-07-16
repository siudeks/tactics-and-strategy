package game.engine;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderBook;
import game.domain.ScenarioDefinition;
import game.domain.TileCoordinate;
import game.domain.Unit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class SimultaneousMovePhaseExecutor implements TurnPhaseExecutor {
    private static final int MAX_UNITS_PER_TILE = 1;
    private static final Comparator<MoveCandidate> COLLISION_TIE_BREAK = Comparator
        .comparingInt((MoveCandidate candidate) -> candidate.move().totalCost())
        .thenComparingInt(candidate -> candidate.from().y())
        .thenComparingInt(candidate -> candidate.from().x())
        .thenComparing(candidate -> candidate.unit().id());

    private record MoveCandidate(Unit unit, TileCoordinate from, CostAwareMovementResolver.ResolvedMove move) {
    }

    private final CostAwareMovementResolver movementResolver;

    SimultaneousMovePhaseExecutor(ScenarioDefinition scenarioDefinition) {
        this.movementResolver = new CostAwareMovementResolver(scenarioDefinition);
    }

    @Override
    public TurnPhase phase() {
        return TurnPhase.SIMULTANEOUS_MOVE;
    }

    @Override
    public PhaseExecution execute(CampaignState state) {
        var moveTargets = new OrderBook(state.pendingOrders()).activeMoveOrdersByUnit();
        Map<TileCoordinate, List<Unit>> occupantsByTile = occupantsByTile(state.units());

        Map<TileCoordinate, List<MoveCandidate>> contendersByDestination = new HashMap<>();
        for (Unit unit : state.units()) {
            var target = moveTargets.get(unit.id());
            if (target == null) {
                continue;
            }
            var resolvedMove = movementResolver.resolve(unit, target);
            if (resolvedMove.isEmpty()) {
                continue;
            }

            var from = TileCoordinate.of(unit.tileX(), unit.tileY());
            var move = resolvedMove.orElseThrow();
            contendersByDestination.computeIfAbsent(move.destination(), key -> new ArrayList<>())
                .add(new MoveCandidate(unit, from, move));
        }

        var winningMovesByUnitId = chooseWinnersPerDestination(contendersByDestination);
        var successfulMoves = resolveSuccessfulMoves(winningMovesByUnitId, occupantsByTile);

        var updatedUnits = new ArrayList<Unit>(state.units().size());
        var movementPlayback = new ArrayList<MovementPlayback>(state.units().size());
        for (Unit unit : state.units()) {
            var winningMove = winningMovesByUnitId.get(unit.id());
            if (winningMove != null && successfulMoves.contains(unit.id())) {
                var destination = winningMove.move().destination();
                updatedUnits.add(new Unit(unit.id(), unit.side(), unit.type(), unit.size(), destination.x(), destination.y()));
                movementPlayback.add(new MovementPlayback(
                    unit.id(),
                    TileCoordinate.of(unit.tileX(), unit.tileY()),
                    destination,
                    MovementPlaybackOutcome.MOVED
                ));
            } else {
                updatedUnits.add(unit);
                movementPlayback.add(new MovementPlayback(
                    unit.id(),
                    TileCoordinate.of(unit.tileX(), unit.tileY()),
                    TileCoordinate.of(unit.tileX(), unit.tileY()),
                    MovementPlaybackOutcome.SKIPPED
                ));
            }
        }

        var nextState = new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber(),
            state.activeSide(),
            updatedUnits,
            state.pendingOrders()
        );
        return new PhaseExecution(nextState, movementPlayback);
    }

    private static Map<TileCoordinate, List<Unit>> occupantsByTile(List<Unit> units) {
        Map<TileCoordinate, List<Unit>> occupants = new HashMap<>();
        for (Unit unit : units) {
            var tile = TileCoordinate.of(unit.tileX(), unit.tileY());
            occupants.computeIfAbsent(tile, key -> new ArrayList<>()).add(unit);
        }
        return occupants;
    }

    private static Map<String, MoveCandidate> chooseWinnersPerDestination(
        Map<TileCoordinate, List<MoveCandidate>> contendersByDestination
    ) {
        var winners = new HashMap<String, MoveCandidate>();
        contendersByDestination.entrySet().stream()
            .sorted(Comparator
                .comparingInt((Map.Entry<TileCoordinate, List<MoveCandidate>> entry) -> entry.getKey().y())
                .thenComparingInt(entry -> entry.getKey().x()))
            .forEach(entry -> {
                var contenders = entry.getValue().stream()
                    .sorted(COLLISION_TIE_BREAK)
                    .toList();
                var accepted = 0;
                for (MoveCandidate contender : contenders) {
                    if (accepted >= MAX_UNITS_PER_TILE) {
                        break;
                    }
                    winners.put(contender.unit().id(), contender);
                    accepted++;
                }
            });
        return winners;
    }

    private static Set<String> resolveSuccessfulMoves(
        Map<String, MoveCandidate> winningMovesByUnitId,
        Map<TileCoordinate, List<Unit>> occupantsByTile
    ) {
        var successfulMoves = new HashSet<>(winningMovesByUnitId.keySet());
        boolean changed;
        do {
            changed = false;
            var moveIds = List.copyOf(successfulMoves);
            for (String moveId : moveIds) {
                var winningMove = winningMovesByUnitId.get(moveId);
                if (winningMove == null) {
                    continue;
                }

                var incumbents = occupantsByTile.get(winningMove.move().destination());
                if (incumbents == null) {
                    continue;
                }

                var blockedByIncumbent = incumbents.stream()
                    .filter(incumbent -> !incumbent.id().equals(moveId))
                    .anyMatch(incumbent -> !successfulMoves.contains(incumbent.id()));
                if (blockedByIncumbent && successfulMoves.remove(moveId)) {
                    changed = true;
                }
            }
        } while (changed);

        return successfulMoves;
    }
}