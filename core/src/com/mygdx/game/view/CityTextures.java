package com.mygdx.game.view;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.resources.TerrainTexture;

@Singleton
public class CityTextures extends TextureHolder {

  @Inject
  public CityTextures(TerrainTexture terrain) {
    super(terrain.getTexture(), IntRectangle.of(7 * Config.SpriteSize, 9 * Config.SpriteSize, Config.SpriteSize, Config.SpriteSize));
  }
}
