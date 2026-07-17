package game.engine;

import game.domain.CampaignState;

import java.util.List;
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
        this.phase = phase;
        this.state = state;
        this.movementPlayback = List.copyOf(movementPlayback);
        this.completedTurnResult = completedTurnResult;
    }

    public boolean turnCompleted() {
        return completedTurnResult.isPresent();
    }
}