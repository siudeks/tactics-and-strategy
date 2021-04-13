package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.google.inject.Singleton;

import lombok.Getter;

@Singleton
public class TerrainTexture {

  @Getter
  private Texture texture;

  public TerrainTexture() {
    texture = new Texture("Terrain.bmp");
  }
}
