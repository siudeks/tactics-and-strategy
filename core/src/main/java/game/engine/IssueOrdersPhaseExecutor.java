package game.engine;

import game.domain.CampaignState;

import java.util.List;

final class IssueOrdersPhaseExecutor implements TurnPhaseExecutor {
    @Override
    public TurnPhase phase() {
        return TurnPhase.ISSUE_ORDERS;
    }

    @Override
    public PhaseExecution execute(CampaignState state) {
        return new PhaseExecution(state, List.of());
    }
}