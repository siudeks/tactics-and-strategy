package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.google.inject.Singleton;
import com.mygdx.game.runtime.GameComponentBase;

import lombok.Getter;

@Singleton
public class DesertRatsTexture extends GameComponentBase {

  @Getter
  private Texture texture;

  @Override
  public void loadTextures() {
    texture = new Texture("DesertRatsSprites.png");
  }
}
