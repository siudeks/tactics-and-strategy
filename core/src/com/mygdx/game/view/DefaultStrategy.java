package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.resources.WaterTextures;
import com.mygdx.game.runtime.GameComponentBase;

/** Should be invoked at the end of strategies. */
@Singleton
public class DefaultStrategy extends GameComponentBase
                             implements ITileFallbackStrategy {

    private TextureHolder texture;

    @Inject
    WaterTextures waterTextures;

    public boolean canExecute(LocationType[] neighbors) {
        return true;
    }

    public TextureHolder execute(LocationType[] neighbors) {
        return texture;
    }

    @Override
    public void useTextures() {
        this.texture = waterTextures.getSea();
    }
}
