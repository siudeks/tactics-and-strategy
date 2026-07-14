package game.engine;

import game.domain.CampaignState;
import game.domain.OrderBook;
import game.domain.Unit;
import game.scenario.LoadedScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final GameClock gameClock;
    private final RtsMovementTracker rtsMovementTracker = new RtsMovementTracker();
    @Nullable
    private TurnExecutionSession activeTurnExecution;

    public GameRuntime(LoadedScenario loadedScenario) {
        this.loadedScenario = Objects.requireNonNull(loadedScenario, "loadedScenario must not be null");
        DeterministicContext ctx = DeterministicContext.withSeed(0L);
        this.engine = TurnEngine.fixedContext(ctx, loadedScenario.scenarioDefinition());
        this.gameClock = new GameClock();
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
            rtsMovementTracker.clear();
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

    /**
     * Advances the in-game clock by the given real-time delta.
     * Call once per render frame with the frame's delta time.
     * Has no effect while the clock is paused.
     *
     * @param deltaSeconds real-time seconds elapsed since the last frame
     */
    public void advanceClock(float deltaSeconds) {
        gameClock.advance(deltaSeconds);
    }

    /**
     * Toggles the game clock between paused and running.
     * When paused, {@link #advanceClock(float)} calls are ignored.
     */
    public void togglePause() {
        gameClock.togglePause();
    }

    /** Returns {@code true} when the game clock is paused. */
    public boolean isPaused() {
        return gameClock.isPaused();
    }

    /**
     * Returns the current in-game time as a human-readable string (e.g. {@code "Day 1  06:00"}).
     */
    public String formattedInGameTime() {
        return gameClock.formattedTime();
    }

    public MoveCommandResult assignMoveTarget(String unitId, int tileX, int tileY) {
        MoveCommandResult commandResult = assignMoveTargetOrder(unitId, tileX, tileY);
        if (commandResult.outcome() != MoveCommandOutcome.UNKNOWN_UNIT) {
            projectMoveTarget(unitId, tileX, tileY);
        }
        return commandResult;
    }

    /**
     * Persists or replaces a unit-scoped MOVE order in campaign-state pending orders.
     * This method updates command state only and does not affect render-side movement projection.
     */
    public MoveCommandResult assignMoveTargetOrder(String unitId, int tileX, int tileY) {
        Objects.requireNonNull(unitId, "unitId");
        CampaignState state = currentCampaignState();
        Unit unit = findUnit(state, unitId);
        if (unit == null) {
            return new MoveCommandResult(MoveCommandOutcome.UNKNOWN_UNIT);
        }
        OrderBook.MoveUpsertResult upsert = new OrderBook(state.pendingOrders())
            .upsertMove(unitId, unit.side(), tileX, tileY);
        CampaignState updated = new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber(),
            state.activeSide(),
            state.units(),
            upsert.orderBook().asPendingOrders()
        );
        updateCurrentCampaignState(updated);
        return new MoveCommandResult(upsert.replacedExisting()
            ? MoveCommandOutcome.REPLACED_EXISTING
            : MoveCommandOutcome.ACCEPTED);
    }

    /**
     * Starts or replaces render-side movement projection for a unit.
     * This method does not mutate command orders and may be called independently from command assignment.
     *
     * @return {@code true} when projection started for a known unit; {@code false} for unknown units
     */
    public boolean projectMoveTarget(String unitId, int tileX, int tileY) {
        Objects.requireNonNull(unitId, "unitId");
        CampaignState state = currentCampaignState();
        Unit unit = findUnit(state, unitId);
        if (unit == null) {
            return false;
        }
        float @Nullable [] existingPos = rtsMovementTracker.currentPosition(unitId);
        float fromX = existingPos != null ? existingPos[0] : unit.tileX();
        float fromY = existingPos != null ? existingPos[1] : unit.tileY();
        rtsMovementTracker.startMovement(unitId, fromX, fromY, tileX, tileY);
        return true;
    }

    /**
     * Advances all active RTS unit movements by {@code deltaSeconds}.
     * Has no effect while the clock is paused or during an active turn-execution session.
     * Units that reach their target have their tile coordinates updated in campaign state.
     *
     * @param deltaSeconds real-time seconds elapsed since the last frame
     */
    public void advanceMovements(float deltaSeconds) {
        if (gameClock.isPaused() || activeTurnExecution != null) {
            return;
        }
        Map<String, int[]> arrived = rtsMovementTracker.advance(deltaSeconds);
        if (arrived.isEmpty()) {
            return;
        }
        CampaignState state = currentCampaignState();
        List<Unit> updatedUnits = new ArrayList<>(state.units().size());
        for (Unit u : state.units()) {
            int[] destination = arrived.get(u.id());
            if (destination != null) {
                updatedUnits.add(new Unit(u.id(), u.side(), u.type(), u.size(), destination[0], destination[1]));
            } else {
                updatedUnits.add(u);
            }
        }
        updateCurrentCampaignState(new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber(),
            state.activeSide(),
            updatedUnits,
            state.pendingOrders()
        ));
    }

    /**
     * Returns the current interpolated float tile positions ({@code [x, y]}) for all
     * actively moving units, keyed by unit ID.  Units that are not moving are absent.
     * Intended for the render layer; not part of deterministic game state.
     */
    public Map<String, float[]> rtsMovementPositions() {
        return rtsMovementTracker.currentPositions();
    }

    private CampaignState currentCampaignState() {
        if (activeTurnExecution != null) {
            return activeTurnExecution.currentState();
        }
        return loadedScenario.campaignState();
    }

    private static @Nullable Unit findUnit(CampaignState state, String unitId) {
        return state.units().stream()
            .filter(u -> u.id().equals(unitId))
            .findFirst()
            .orElse(null);
    }

    private void updateCurrentCampaignState(CampaignState updatedState) {
        if (activeTurnExecution != null) {
            activeTurnExecution.replaceCurrentState(updatedState);
            return;
        }
        loadedScenario = new LoadedScenario(loadedScenario.scenarioDefinition(), updatedState);
    }
}
