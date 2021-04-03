package com.mygdx.game.view;

public final class CoastWithLandToTheNorthStrategy implements ITileStrategy {
    
    private final TextureHolder texture;

    public CoastWithLandToTheNorthStrategy(TextureHolder texture) {
        this.texture = texture;
    }

    public boolean CanExecute(LocationType[] neighbors) {
        if (neighbors[Directions.NeighborWest] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborEast] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborNorth] != LocationType.Water) return false;
        if (neighbors[Directions.NeighborSouth] == LocationType.Water) return false;
        if (neighbors[Directions.NeighborThis] != LocationType.Water) return false;

        return true;

    }

    public TextureHolder Execute(LocationType[] neighbors) {
        return texture;
    }
}
