package com.mygdx.game.resources;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.runtime.GameComponentBase;
import com.mygdx.game.view.IntRectangle;
import com.mygdx.game.view.TextureHolder;

import lombok.Getter;

@Singleton
public class LandUnitTextures extends GameComponentBase {

  private DesertRatsTexture desertRatsTextures;
  @Getter
  private TextureHolder texture;

  @Inject
  public LandUnitTextures(DesertRatsTexture desertRatsTextures) {
    this.desertRatsTextures = desertRatsTextures;
  }

  @Override
  public void useTextures() {
    texture = new TextureHolder(desertRatsTextures.getTexture() , IntRectangle.of(1 + 0 * Config.SpriteSize, 1 + 0, Config.SpriteSize, Config.SpriteSize));
  }
}
