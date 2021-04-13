package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class LandUnitStrategy implements ITileStrategy {

    private final TextureHolder texture;

    @Inject
    public LandUnitStrategy(LandUnitTextures landUnitTexture) {
        texture = landUnitTexture;
    }

    public boolean canExecute(LocationType[] neighbors) {
        return neighbors[Directions.NeighborThis] == LocationType.LandUnit;
    }

    public TextureHolder execute(LocationType[] neighbors) {
        return texture;
    }
}
