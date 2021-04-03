package com.mygdx.game.view;

public final class CityStrategy implements ITileStrategy {
    
    private final TextureHolder texture;

    public CityStrategy(TextureHolder texture)
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
