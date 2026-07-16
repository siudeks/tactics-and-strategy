package game.screens;

import game.domain.Unit;
import game.engine.MovementPlayback;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

final class UnitRenderPositionResolver {

    private UnitRenderPositionResolver() {
    }

    /**
     * Resolves the visual tile position for {@code unit} given optional RTS and playback state.
     * <p>
     * Priority: RTS continuous position (if present) &gt; turn-animation playback &gt; static integer tile.
     *
     * @param unit          the unit to resolve a position for
     * @param rtsPosition   float {@code [x, y]} from the RTS movement tracker, or {@code null}
     * @param movementState turn-animation playback state, or {@code null} when not animating
     */
    static RenderTilePosition resolveTilePosition(
        Unit unit,
        float @Nullable [] rtsPosition,
        @Nullable MovementPlaybackRenderState movementState
    ) {
        Objects.requireNonNull(unit, "unit must not be null");
        if (rtsPosition != null) {
            return new RenderTilePosition(rtsPosition[0], rtsPosition[1]);
        }
        if (movementState == null) {
            return new RenderTilePosition(unit.tileX(), unit.tileY());
        }
        for (MovementPlayback playback : movementState.playback()) {
            if (!playback.unitId().equals(unit.id()) || !playback.moved()) {
                continue;
            }
            var progress = movementState.progress();
            var tileX = playback.from().x() + (playback.to().x() - playback.from().x()) * progress;
            var tileY = playback.from().y() + (playback.to().y() - playback.from().y()) * progress;
            return new RenderTilePosition(tileX, tileY);
        }
        return new RenderTilePosition(unit.tileX(), unit.tileY());
    }

    record RenderTilePosition(float tileX, float tileY) {
    }
}