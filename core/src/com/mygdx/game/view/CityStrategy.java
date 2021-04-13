package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class CityStrategy implements ITileStrategy {
    
    private final TextureHolder texture;

    @Inject
    public CityStrategy(CityTextures texture) {
        this.texture = texture;
    }

    public boolean canExecute(LocationType[] neighbors) {
        return neighbors[Directions.NeighborThis] == LocationType.City;

    }

    public TextureHolder execute(LocationType[] neighbors) {
        return texture;
    }
}
