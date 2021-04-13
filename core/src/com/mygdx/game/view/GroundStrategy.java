package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public final class GroundStrategy implements ITileStrategy {

    private final TextureHolder texture;

    @Inject
    public GroundStrategy(GroundTextures texture) {
        this.texture = texture;
    }

    public boolean canExecute(LocationType[] neighbors) {
        return neighbors[Directions.NeighborThis] == LocationType.Ground;
    }

    public TextureHolder execute(LocationType[] neighbors) {
        return texture;
    }
}
