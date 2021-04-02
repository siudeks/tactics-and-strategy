package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;

public interface ITextureConsumer
{
    void OnLoaded(Texture texture, TextureItem item);
    void LoadFinished();
}

enum TextureItem { 
    TERRAIN,
    DESERT_RATES
}
