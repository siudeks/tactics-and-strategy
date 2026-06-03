package game.engine;

import game.domain.CampaignState;

import java.util.List;

final class RetreatPhaseExecutor implements TurnPhaseExecutor {
    @Override
    public TurnPhase phase() {
        return TurnPhase.RETREAT;
    }

    @Override
    public PhaseExecution execute(CampaignState state) {
        return new PhaseExecution(state, List.of());
    }
}