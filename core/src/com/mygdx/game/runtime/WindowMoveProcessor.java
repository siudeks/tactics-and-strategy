package com.mygdx.game.runtime;

import java.time.Duration;
import java.time.LocalDateTime;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Singleton;
import com.mygdx.game.domain.IntendedViewPosition;
import com.mygdx.game.domain.IntendedViewPosition.Current;

@Singleton
public class WindowMoveProcessor extends GameComponentBase
                                         implements IntendedViewPosition.Provider {

    private final LocalDateTime start = LocalDateTime.now();
    private long totalSeconds = 0;

    @Override
    public void OnDraw(SpriteBatch spriteBatch) {
        var now = LocalDateTime.now();
        var currentTotalSeconds = Duration.between(start, now).toSeconds();
        if (currentTotalSeconds == totalSeconds) return;

        totalSeconds = currentTotalSeconds;
        onUpdate();
    }

    private int centerX;
    private int centerY;

    private void onUpdate() {
        centerX ++;
        centerY ++;
    }


    @Override
    public Current current() {
        return Current.of(centerX, centerY);
    }
}
