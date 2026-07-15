package game.engine;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderBook;
import game.domain.ScenarioDefinition;
import game.domain.TileCoordinate;
import game.domain.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class SimultaneousMovePhaseExecutor implements TurnPhaseExecutor {
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
        Map<String, Order> moveTargets = new OrderBook(state.pendingOrders()).activeMoveOrdersByUnit();

        List<Unit> updatedUnits = new ArrayList<>(state.units().size());
        List<MovementPlayback> movementPlayback = new ArrayList<>(state.units().size());
        for (Unit unit : state.units()) {
            Order target = moveTargets.get(unit.id());
            Optional<CostAwareMovementResolver.ResolvedMove> resolvedMove = target == null
                ? Optional.empty()
                : movementResolver.resolve(unit, target);
            if (resolvedMove.isPresent()) {
                TileCoordinate destination = resolvedMove.orElseThrow().destination();
                updatedUnits.add(new Unit(unit.id(), unit.side(), unit.type(), unit.size(), destination.x(), destination.y()));
                movementPlayback.add(new MovementPlayback(
                    unit.id(),
                    new TileCoordinate(unit.tileX(), unit.tileY()),
                    destination,
                    MovementPlaybackOutcome.MOVED
                ));
            } else {
                updatedUnits.add(unit);
                movementPlayback.add(new MovementPlayback(
                    unit.id(),
                    new TileCoordinate(unit.tileX(), unit.tileY()),
                    new TileCoordinate(unit.tileX(), unit.tileY()),
                    MovementPlaybackOutcome.SKIPPED
                ));
            }
        }

        CampaignState nextState = new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber(),
            state.activeSide(),
            updatedUnits,
            state.pendingOrders()
        );
        return new PhaseExecution(nextState, movementPlayback);
    }
}