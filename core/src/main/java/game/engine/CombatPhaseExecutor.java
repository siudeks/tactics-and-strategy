package game.engine;

import game.domain.CampaignState;

import java.util.List;

final class CombatPhaseExecutor implements TurnPhaseExecutor {
    @Override
    public TurnPhase phase() {
        return TurnPhase.COMBAT;
    }

    @Override
    public PhaseExecution execute(CampaignState state) {
        return new PhaseExecution(state, List.of());
    }
}