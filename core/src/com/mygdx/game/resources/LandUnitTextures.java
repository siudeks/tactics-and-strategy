package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Config;
import com.mygdx.game.view.TextureHolder;

public class LandUnitTextures extends TextureHolder {
  private Texture texture;
  public LandUnitTextures(DesertRatsTexture desertRatsTextures) {
    super(desertRatsTextures.getTexture() , new Rectangle(1 + 0 * Config.SpriteSize, 1 + 0, Config.SpriteSize, Config.SpriteSize));
  }
}
