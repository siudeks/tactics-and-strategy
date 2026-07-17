package game.engine;

import game.domain.CampaignState;
import java.util.List;

public record TurnResult(
    CampaignState state,
    List<TurnPhase> phaseTrace,
    long seed,
    long timeMillis,
    String canonicalSnapshot
) {
    public TurnResult {
        phaseTrace = List.copyOf(phaseTrace);
    }
}
