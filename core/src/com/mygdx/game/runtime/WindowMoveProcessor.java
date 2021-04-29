package com.mygdx.game.runtime;

import java.time.Duration;
import java.time.LocalDateTime;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.domain.IntendedMapCentre;

@Singleton
public class WindowMoveProcessor extends GameComponentBase {

    @Inject
    public IntendedMapCentre intendedMapCentre;

    private final LocalDateTime start = LocalDateTime.now();
    private long totalSeconds = 0;

    @Override
    public void OnDraw(SpriteBatch spriteBatch) {
        var now = LocalDateTime.now();
        var currentTotalSeconds = Duration.between(start, now).toSeconds();
        if (currentTotalSeconds == totalSeconds) return;

        totalSeconds = currentTotalSeconds;
        OnUpdate();
    }

    private void OnUpdate()
    {
        // intendedMapCentre.setX(intendedMapCentre.getX() + 1);
        // intendedMapCentre.setY(intendedMapCentre.getY() + 1);
    }
}
