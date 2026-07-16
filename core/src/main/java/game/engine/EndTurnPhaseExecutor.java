package game.engine;

import game.domain.CampaignState;

import java.util.List;

final class EndTurnPhaseExecutor implements TurnPhaseExecutor {
    @Override
    public TurnPhase phase() {
        return TurnPhase.END_TURN;
    }

    @Override
    public PhaseExecution execute(CampaignState state) {
        var nextState = new CampaignState(
            state.campaignId(),
            state.scenarioId(),
            state.turnNumber() + 1,
            TurnEngine.flipSide(state.activeSide()),
            state.units(),
            List.of()
        );
        return new PhaseExecution(nextState, List.of());
    }
}