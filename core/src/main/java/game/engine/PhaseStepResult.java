package game.engine;

import game.domain.CampaignState;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record PhaseStepResult(
    RuntimePhase phase,
    CampaignState state,
    List<MovementPlayback> movementPlayback,
    Optional<TurnResult> completedTurnResult
) {
    public PhaseStepResult(
        RuntimePhase phase,
        CampaignState state,
        List<MovementPlayback> movementPlayback,
        Optional<TurnResult> completedTurnResult
    ) {
        this.phase = Objects.requireNonNull(phase, "phase must not be null");
        this.state = Objects.requireNonNull(state, "state must not be null");
        this.movementPlayback = List.copyOf(Objects.requireNonNull(movementPlayback, "movementPlayback must not be null"));
        this.completedTurnResult = Objects.requireNonNull(completedTurnResult, "completedTurnResult must not be null");
    }

    public boolean turnCompleted() {
        return completedTurnResult.isPresent();
    }
}