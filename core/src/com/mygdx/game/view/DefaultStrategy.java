package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.resources.WaterTextures;

/** Should be invoked at the end of strategies. */
@Singleton
public class DefaultStrategy implements ITileFallbackStrategy {
    private final TextureHolder texture;

    @Inject
    public DefaultStrategy(WaterTextures waterTextures) {
        this.texture = waterTextures.getSea();
    }

    public boolean canExecute(LocationType[] neighbors) {
        return true;
    }

    public TextureHolder execute(LocationType[] neighbors) {
        return texture;
    }
}
