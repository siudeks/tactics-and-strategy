package com.mygdx.game.view;

import com.badlogic.gdx.graphics.Texture;

/**
 *  TextureHolder represents part of a texture. Because of Monogame/XNA limitations, can' create texture
 *  in unit tests, so TextureHolder is natural replacement of Texture2D.
 *  THe calss can be extended for purpose of Dependency Injection to mark individual types types of textured
 */

public class TextureHolder {
    /**
     * Parameterless constructor used in tests to allow create an instance and make assertion.
     */
    public TextureHolder() {
        this(null, null);
    }

    public final Texture Texture2D;
    public final IntRectangle Source;

    public TextureHolder(Texture texture, IntRectangle source)
    {
        this.Texture2D = texture;
        this.Source = source;
    }
}
