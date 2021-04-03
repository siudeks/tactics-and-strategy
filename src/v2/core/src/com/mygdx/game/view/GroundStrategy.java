package com.mygdx.game.view;

public final class GroundStrategy implements ITileStrategy {

    private final TextureHolder texture;

    public GroundStrategy(TextureHolder texture) {
        this.texture = texture;
    }

    public boolean CanExecute(LocationType[] neighbors) {
        return neighbors[Directions.NeighborThis] == LocationType.Ground;
    }

    public TextureHolder Execute(LocationType[] neighbors) {
        return texture;
    }
}
