package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.google.inject.Singleton;

import lombok.Getter;

@Singleton
public class TerrainTexture implements ResourceLoader {

  @Getter
  private Texture texture;

  @Override
  public void initialize() {
    texture = new Texture("Terrain.bmp");
  }
}
