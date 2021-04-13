package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;
import com.google.inject.Singleton;

import lombok.Getter;

@Singleton
public class DesertRatsTexture {

  @Getter
  private Texture texture;

  public DesertRatsTexture() {
    texture = new Texture("DesertRatsSprites.png");
  }
}
