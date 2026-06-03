package game.engine;

import java.util.Objects;

public record MovementPlayback(
    String unitId,
    TileCoordinate from,
    TileCoordinate to,
    MovementPlaybackOutcome outcome
) {
    public MovementPlayback {
        Objects.requireNonNull(unitId, "unitId must not be null");
        Objects.requireNonNull(from, "from must not be null");
        Objects.requireNonNull(to, "to must not be null");
        Objects.requireNonNull(outcome, "outcome must not be null");
    }

    public boolean moved() {
        return outcome.moved();
    }

    public boolean skipped() {
        return outcome.skipped();
    }
}