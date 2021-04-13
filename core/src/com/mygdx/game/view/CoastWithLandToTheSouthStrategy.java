package com.mygdx.game.view;

import com.google.inject.Singleton;
import com.mygdx.game.resources.WaterTextures;

@Singleton
public final class CoastWithLandToTheSouthStrategy implements ITileStrategy {

    private final TextureHolder texture;

    public CoastWithLandToTheSouthStrategy(WaterTextures waterTextures) {
        this.texture = waterTextures.getCoastWithLandToTheSouth();
    }

    public boolean CanExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;

        return true;

    }

    public TextureHolder Execute(LocationType[] neighbors) {
        return texture;
    }
}
