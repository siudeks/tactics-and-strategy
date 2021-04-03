package com.mygdx.game.view;

// should be invoked at the end of strategies
public class DefaultStrategy implements ITileStrategy {
    private final TextureHolder texture;

    public DefaultStrategy(TextureHolder texture) {
        this.texture = texture;
    }

    public boolean CanExecute(LocationType[] neighbors) {
        return true;
    }

    public TextureHolder Execute(LocationType[] neighbors) {
        return texture;
    }
}
