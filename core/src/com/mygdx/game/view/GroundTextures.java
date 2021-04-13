package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.resources.TerrainTexture;

@Singleton
public class GroundTextures extends TextureHolder {

  @Inject
  public GroundTextures(TerrainTexture terrain) {
    super(terrain.getTexture(), IntRectangle.of(0 * Config.SpriteSize, 0, Config.SpriteSize, Config.SpriteSize));
  }
}
