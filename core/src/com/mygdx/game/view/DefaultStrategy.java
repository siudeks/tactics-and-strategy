package com.mygdx.game.view;

import com.google.inject.Singleton;
import com.mygdx.game.resources.WaterTextures;

/** Should be invoked at the end of strategies. */
@Singleton
public class DefaultStrategy implements ITileFallbackStrategy {
    private final TextureHolder texture;

    public DefaultStrategy(WaterTextures waterTextures) {
        this.texture = waterTextures.getSea();
    }

    public boolean CanExecute(LocationType[] neighbors) {
        return true;
    }

    public TextureHolder Execute(LocationType[] neighbors) {
        return texture;
    }
}
