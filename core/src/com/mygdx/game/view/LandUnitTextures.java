package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.resources.TerrainTexture;

@Singleton
public class LandUnitTextures extends TextureHolder {

  @Inject
  public LandUnitTextures(TerrainTexture terrain) {
    super(terrain.getTexture(), IntRectangle.of(1 + 0 * Config.SpriteSize, 1 + 0, Config.SpriteSize, Config.SpriteSize));
  }
}
