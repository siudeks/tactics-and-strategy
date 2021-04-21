package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.runtime.GameComponentBase;

@Singleton
public final class GroundStrategy extends GameComponentBase
                                  implements ITileStrategy {

    private final GroundTextures textures;

    @Inject
    public GroundStrategy(GroundTextures textures) {
        this.textures = textures;
    }

    public boolean canExecute(LocationType[] neighbors) {
        return neighbors[Directions.NeighborThis] == LocationType.Ground;
    }

    public TextureHolder execute(LocationType[] neighbors) {
        return textures.getTexture();
    }
}
