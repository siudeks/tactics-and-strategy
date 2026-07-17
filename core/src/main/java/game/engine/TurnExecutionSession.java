package game.engine;

import game.domain.CampaignState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

public final class TurnExecutionSession {
    private final TurnEngine engine;
    private final long startTimeMillis;
    private final List<TurnPhase> phaseTrace;
    private CampaignState currentState;
    private int nextPhaseIndex;
    @Nullable
    private TurnResult completedTurnResult;

    TurnExecutionSession(TurnEngine engine, CampaignState startingState) {
        this.engine = engine;
        this.currentState = startingState;
        this.startTimeMillis = System.currentTimeMillis();
        this.phaseTrace = new ArrayList<>(TurnEngine.phaseSequence().size());
    }

    public RuntimePhase currentPhase() {
        if (completedTurnResult != null) {
            return RuntimePhase.COMPLETE;
        }
        return RuntimePhase.fromTurnPhase(TurnEngine.phaseSequence().get(nextPhaseIndex));
    }

    public CampaignState currentState() {
        return currentState;
    }

    public boolean isComplete() {
        return completedTurnResult != null;
    }

    public Optional<TurnResult> completedTurnResult() {
        return Optional.ofNullable(completedTurnResult);
    }

    public PhaseStepResult advance() {
        if (isComplete()) {
            throw new IllegalStateException("Turn execution session is already complete");
        }
        var turnPhase = TurnEngine.phaseSequence().get(nextPhaseIndex);
        var execution = engine.executePhase(turnPhase, currentState);
        currentState = execution.state();
        phaseTrace.add(turnPhase);
        nextPhaseIndex++;

        var turnResult = Optional.<TurnResult>empty();
        if (turnPhase == TurnPhase.END_TURN) {
            completedTurnResult = engine.buildTurnResult(currentState, List.copyOf(phaseTrace), startTimeMillis);
            turnResult = Optional.of(completedTurnResult);
        }

        return new PhaseStepResult(
            RuntimePhase.fromTurnPhase(turnPhase),
            currentState,
            execution.movementPlayback(),
            turnResult
        );
    }

    void replaceCurrentState(CampaignState updatedState) {
        currentState = updatedState;
    }
}