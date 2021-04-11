package com.mygdx.game.extensions;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.view.Vector2;
import com.mygdx.game.view.TextureHolder;

public class SpriteBatchUtils {

    public static void draw(SpriteBatch spriteBatch, Vector2 position, TextureHolder texture) {
        spriteBatch.draw(texture.Texture2D, position.getX(), position.getY(), texture.Source.width, texture.Source.height);
    }
}
