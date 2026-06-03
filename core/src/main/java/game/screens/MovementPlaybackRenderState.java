package game.screens;

import game.engine.MovementPlayback;

import java.util.List;
import java.util.Objects;

final class MovementPlaybackRenderState {
    private final List<MovementPlayback> playback;
    private final float progress;

    MovementPlaybackRenderState(List<MovementPlayback> playback, float progress) {
        this.playback = List.copyOf(Objects.requireNonNull(playback, "playback must not be null"));
        this.progress = Math.clamp(progress, 0f, 1f);
    }

    List<MovementPlayback> playback() {
        return playback;
    }

    float progress() {
        return progress;
    }
}