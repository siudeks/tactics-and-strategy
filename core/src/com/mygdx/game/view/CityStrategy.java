package com.mygdx.game.view;

import com.google.inject.Singleton;

@Singleton
public final class CityStrategy implements ITileStrategy {
    
    private final TextureHolder texture;

    public CityStrategy(CityTextures texture)
    {
        this.texture = texture;
    }

    public boolean CanExecute(LocationType[] neighbors)
    {
        return neighbors[Directions.NeighborThis] == LocationType.City;

    }

    public TextureHolder Execute(LocationType[] neighbors)
    {
        return texture;
    }
}
