package com.mygdx.game.extensions;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.view.Vector2;
import com.mygdx.game.view.TextureHolder;

public class SpriteBatchExtensions {

    public static void Draw(SpriteBatch spriteBatch, Vector2 position, TextureHolder texture) {
        spriteBatch.draw(texture.Texture2D, position.X(), position.Y(), texture.Source.width, texture.Source.height);
    }
}
