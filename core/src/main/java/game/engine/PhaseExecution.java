package game.engine;

import game.domain.CampaignState;

import java.util.List;
import java.util.Objects;

record PhaseExecution(
    CampaignState state,
    List<MovementPlayback> movementPlayback
) {
    PhaseExecution {
        Objects.requireNonNull(state, "state must not be null");
        movementPlayback = List.copyOf(Objects.requireNonNull(movementPlayback, "movementPlayback must not be null"));
    }
}