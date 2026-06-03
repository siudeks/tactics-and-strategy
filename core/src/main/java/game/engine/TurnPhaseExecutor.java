package game.engine;

import game.domain.CampaignState;

interface TurnPhaseExecutor {
    TurnPhase phase();

    PhaseExecution execute(CampaignState state);
}