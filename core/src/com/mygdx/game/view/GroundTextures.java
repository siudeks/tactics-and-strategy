package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.resources.TerrainTexture;
import com.mygdx.game.runtime.GameComponentBase;

import lombok.Getter;

@Singleton
public class GroundTextures extends GameComponentBase {

  private final TerrainTexture terrain;

  @Getter
  private TextureHolder texture;

  @Inject
  public GroundTextures(TerrainTexture terrain) {
    this.terrain = terrain;
  }

  @Override
  public void useTextures() {
    this.texture = new TextureHolder(terrain.getTexture(), IntRectangle.of(0 * Config.SpriteSize, 0, Config.SpriteSize, Config.SpriteSize));
  }
}
