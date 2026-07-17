package game.engine;

import game.domain.CampaignState;

import java.util.List;

record PhaseExecution(
    CampaignState state,
    List<MovementPlayback> movementPlayback
) {
    PhaseExecution {
        movementPlayback = List.copyOf(movementPlayback);
    }
}