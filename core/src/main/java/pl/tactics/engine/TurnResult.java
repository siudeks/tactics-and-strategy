package pl.tactics.engine;

import pl.tactics.domain.CampaignState;
import java.util.List;
import java.util.Objects;

public record TurnResult(
    CampaignState state,
    List<TurnPhase> phaseTrace,
    long seed,
    long timeMillis,
    String canonicalSnapshot
) {
    public TurnResult {
        Objects.requireNonNull(state, "state must not be null");
        phaseTrace = List.copyOf(Objects.requireNonNull(phaseTrace, "phaseTrace must not be null"));
        Objects.requireNonNull(canonicalSnapshot, "canonicalSnapshot must not be null");
    }
}
