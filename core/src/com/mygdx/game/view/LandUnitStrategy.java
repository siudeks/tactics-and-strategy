package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.resources.LandUnitTextures;

@Singleton
public final class LandUnitStrategy implements ITileStrategy {

    private final LandUnitTextures textures;

    @Inject
    public LandUnitStrategy(LandUnitTextures landUnitTexture) {
        textures = landUnitTexture;
    }

    public boolean canExecute(LocationType[] neighbors) {
        return neighbors[Directions.NeighborThis] == LocationType.LandUnit;
    }

    public TextureHolder execute(LocationType[] neighbors) {
        return textures.getTexture();
    }
}
