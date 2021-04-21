package com.mygdx.game.extensions;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.view.Vector2;
import com.mygdx.game.view.TextureHolder;

public class SpriteBatchUtils {

    public static void draw(SpriteBatch spriteBatch, Vector2 position, TextureHolder texture) {
        spriteBatch.draw(texture.texture, position.getX(), position.getY(), texture.source.getSrcX(), texture.source.getSrcY(), texture.source.getWidth(), texture.source.getHeight());
    }
}
