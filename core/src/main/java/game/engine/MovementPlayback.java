package game.engine;

import game.domain.TileCoordinate;
import game.domain.UnitId;

public record MovementPlayback(
    UnitId unitId,
    TileCoordinate from,
    TileCoordinate to,
    MovementPlaybackOutcome outcome
) {
    public MovementPlayback {
    }

    public boolean moved() {
        return outcome.moved();
    }

    public boolean skipped() {
        return outcome.skipped();
    }
}