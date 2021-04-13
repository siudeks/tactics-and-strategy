package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.resources.WaterTextures;

@Singleton
public final class CoastWithLandToTheNorthStrategy implements ITileStrategy {
    
    private final TextureHolder texture;

    @Inject
    public CoastWithLandToTheNorthStrategy(WaterTextures waterTextures) {
        this.texture = waterTextures.getCoastWithLandToTheNorth();
    }

    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;

        return true;

    }

    public TextureHolder execute(LocationType[] neighbors) {
        return texture;
    }
}
