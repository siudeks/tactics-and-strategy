package com.mygdx.game.view;

public final class LandUnitStrategy implements ITileStrategy {

    private final TextureHolder texture;

    public LandUnitStrategy(TextureHolder landUnitTexture) {
        texture = landUnitTexture;
    }

    public boolean CanExecute(LocationType[] neighbors) {
        return neighbors[Directions.NeighborThis] == LocationType.LandUnit;
    }

    public TextureHolder Execute(LocationType[] neighbors) {
        return texture;
    }
}
