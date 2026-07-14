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

final class SimultaneousMovePhaseExecutor implements TurnPhaseExecutor {
    private final ScenarioDefinition scenarioDefinition;

    SimultaneousMovePhaseExecutor(ScenarioDefinition scenarioDefinition) {
        this.scenarioDefinition = scenarioDefinition;
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
            if (target != null && TurnEngine.isValidMove(scenarioDefinition, target.targetX(), target.targetY())) {
                updatedUnits.add(new Unit(unit.id(), unit.side(), unit.type(), unit.size(), target.targetX(), target.targetY()));
                movementPlayback.add(new MovementPlayback(
                    unit.id(),
                    new TileCoordinate(unit.tileX(), unit.tileY()),
                    new TileCoordinate(target.targetX(), target.targetY()),
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