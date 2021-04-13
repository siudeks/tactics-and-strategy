package com.mygdx.game.view;

import com.badlogic.gdx.math.Rectangle;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.resources.TerrainTexture;

@Singleton
public class CityTextures extends TextureHolder {
  public CityTextures(TerrainTexture terrain) {
    super(terrain.getTexture(), new Rectangle(7 * Config.SpriteSize, 9 * Config.SpriteSize, Config.SpriteSize, Config.SpriteSize));
  }
}
