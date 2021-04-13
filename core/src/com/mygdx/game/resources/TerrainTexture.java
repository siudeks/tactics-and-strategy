package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.google.inject.Singleton;

import lombok.Getter;

@Singleton
public class TerrainTexture implements GameComponent {

  @Getter
  private Texture texture;

  @Override
  public void loadTextures() {
    texture = new Texture("Terrain.bmp");
  }

  @Override
  public void useTextures() {
    // TODO Auto-generated method stub
  }
}
