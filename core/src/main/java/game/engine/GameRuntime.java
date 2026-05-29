package game.engine;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderType;
import game.domain.Unit;
import game.scenario.LoadedScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GameRuntime {

    /**
     * Result of simulating one turn.
     */
    public record TurnSimulationResult(TurnResult turnResult) {
        public TurnSimulationResult {
            Objects.requireNonNull(turnResult, "turnResult must not be null");
        }
    }

    private LoadedScenario loadedScenario;
    private final TurnEngine engine;

    public GameRuntime(LoadedScenario loadedScenario) {
        this.loadedScenario = Objects.requireNonNull(loadedScenario, "loadedScenario must not be null");
        DeterministicContext ctx = DeterministicContext.withSeed(0L);
        this.engine = TurnEngine.fixedContext(ctx, loadedScenario.scenarioDefinition());
    }

    public TurnSimulationResult simulateOneTurn() {
        TurnResult result = engine.runOneTurn(loadedScenario.campaignState());
        // Update internal campaign state to the result state
        loadedScenario = new LoadedScenario(
            loadedScenario.scenarioDefinition(),
            result.state()
        );
        return new TurnSimulationResult(result);
    }

    public int getTurnNumber() {
        return loadedScenario.campaignState().turnNumber();
    }

    public CampaignState getCurrentCampaignState() {
        return loadedScenario.campaignState();
    }

    public String getActiveSideCode() {
        return loadedScenario.campaignState().activeSide().name();
    }

    public void assignMoveTarget(String unitId, int tileX, int tileY) {
        Objects.requireNonNull(unitId, "unitId");
        CampaignState state = loadedScenario.campaignState();
        Unit unit = state.units().stream()
            .filter(u -> u.id().equals(unitId))
            .findFirst()
            .orElse(null);
        if (unit == null) {
            return;
        }
        List<Order> next = new ArrayList<>(state.pendingOrders().size() + 1);
        for (Order existing : state.pendingOrders()) {
            if (existing.type() == OrderType.MOVE && existing.unitId().equals(unitId)) {
                continue;
            }
            next.add(existing);
        }
        next.add(new Order("move-" + unitId, unitId, unit.side(), OrderType.MOVE, tileX, tileY));
        CampaignState updated = new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber(),
            state.activeSide(),
            state.units(),
            next
        );
        loadedScenario = new LoadedScenario(loadedScenario.scenarioDefinition(), updated);
    }
}
