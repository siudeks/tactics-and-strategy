package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.resources.WaterTextures;
import com.mygdx.game.runtime.GameComponentBase;

@Singleton
public final class CoastWithLandToTheSouthStrategy extends GameComponentBase
                                                   implements ITileStrategy {

    private final WaterTextures textures;

    @Inject
    public CoastWithLandToTheSouthStrategy(WaterTextures waterTextures) {
        this.textures = waterTextures;
    }

    public boolean canExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;

        return true;

    }

    public TextureHolder execute(LocationType[] neighbors) {
        return textures.getLandSouth();
    }
}
