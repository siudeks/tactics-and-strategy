package com.mygdx.game.resources;

import com.google.inject.AbstractModule;

public class DiModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(DesertRatsTexture.class);
    bind(TerrainTexture.class);
  }
}
