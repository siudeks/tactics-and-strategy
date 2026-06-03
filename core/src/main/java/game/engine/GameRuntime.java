package game.engine;

import game.domain.CampaignState;
import game.domain.Order;
import game.domain.OrderType;
import game.domain.Unit;
import game.scenario.LoadedScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;

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
    @Nullable
    private TurnExecutionSession activeTurnExecution;

    public GameRuntime(LoadedScenario loadedScenario) {
        this.loadedScenario = Objects.requireNonNull(loadedScenario, "loadedScenario must not be null");
        DeterministicContext ctx = DeterministicContext.withSeed(0L);
        this.engine = TurnEngine.fixedContext(ctx, loadedScenario.scenarioDefinition());
    }

    public TurnExecutionSession beginTurnExecution() {
        if (activeTurnExecution != null && !activeTurnExecution.isComplete()) {
            throw new IllegalStateException("Turn execution is already active");
        }
        activeTurnExecution = engine.beginExecution(currentCampaignState());
        return activeTurnExecution;
    }

    public PhaseStepResult advanceTurnExecution() {
        if (activeTurnExecution == null) {
            throw new IllegalStateException("No active turn execution");
        }
        PhaseStepResult stepResult = activeTurnExecution.advance();
        if (stepResult.completedTurnResult().isPresent()) {
            loadedScenario = new LoadedScenario(
                loadedScenario.scenarioDefinition(),
                stepResult.completedTurnResult().orElseThrow().state()
            );
            activeTurnExecution = null;
        }
        return stepResult;
    }

    public boolean hasActiveTurnExecution() {
        return activeTurnExecution != null;
    }

    public RuntimePhase currentRuntimePhase() {
        if (activeTurnExecution == null) {
            throw new IllegalStateException("No active turn execution");
        }
        return activeTurnExecution.currentPhase();
    }

    public TurnSimulationResult simulateOneTurn() {
        if (activeTurnExecution == null) {
            beginTurnExecution();
        }

        PhaseStepResult stepResult;
        do {
            stepResult = advanceTurnExecution();
        } while (!stepResult.turnCompleted());

        return new TurnSimulationResult(stepResult.completedTurnResult().orElseThrow());
    }

    public int getTurnNumber() {
        return currentCampaignState().turnNumber();
    }

    public CampaignState getCurrentCampaignState() {
        return currentCampaignState();
    }

    public String getActiveSideCode() {
        return currentCampaignState().activeSide().name();
    }

    public void assignMoveTarget(String unitId, int tileX, int tileY) {
        Objects.requireNonNull(unitId, "unitId");
        CampaignState state = currentCampaignState();
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
        updateCurrentCampaignState(updated);
    }

    private CampaignState currentCampaignState() {
        if (activeTurnExecution != null) {
            return activeTurnExecution.currentState();
        }
        return loadedScenario.campaignState();
    }

    private void updateCurrentCampaignState(CampaignState updatedState) {
        if (activeTurnExecution != null) {
            activeTurnExecution.replaceCurrentState(updatedState);
            return;
        }
        loadedScenario = new LoadedScenario(loadedScenario.scenarioDefinition(), updatedState);
    }
}
