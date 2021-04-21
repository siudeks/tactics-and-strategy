package com.mygdx.game.view;

import com.badlogic.gdx.graphics.Texture;

/**
 *  TextureHolder represents part of a texture. Because of game engine limitation, can' create texture
 *  in unit tests, so TextureHolder is a natural replacement of Texture.
 *  THe calss can be extended for purpose of Dependency Injection to mark individual types types of textured
 */

public class TextureHolder {
    /**
     * Parameterless constructor used in tests to allow create an instance and make assertion.
     */
    public TextureHolder() {
        this(null, null);
    }

    public final Texture texture;
    public final IntRectangle source;

    public TextureHolder(Texture texture, IntRectangle source)
    {
        this.texture = texture;
        this.source = source;
    }
}
