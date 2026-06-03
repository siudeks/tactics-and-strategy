package game.screens;

import game.domain.Unit;
import game.engine.MovementPlayback;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

final class UnitRenderPositionResolver {

    private UnitRenderPositionResolver() {
    }

    static RenderTilePosition resolveTilePosition(Unit unit, @Nullable MovementPlaybackRenderState movementState) {
        Objects.requireNonNull(unit, "unit must not be null");
        if (movementState == null) {
            return new RenderTilePosition(unit.tileX(), unit.tileY());
        }
        for (MovementPlayback playback : movementState.playback()) {
            if (!playback.unitId().equals(unit.id()) || !playback.moved()) {
                continue;
            }
            float progress = movementState.progress();
            float tileX = playback.from().x() + (playback.to().x() - playback.from().x()) * progress;
            float tileY = playback.from().y() + (playback.to().y() - playback.from().y()) * progress;
            return new RenderTilePosition(tileX, tileY);
        }
        return new RenderTilePosition(unit.tileX(), unit.tileY());
    }

    record RenderTilePosition(float tileX, float tileY) {
    }
}