package com.mygdx.game.resources;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class DiModuleResources extends AbstractModule {

  @Override
  protected void configure() {
    bind(DesertRatsTexture.class);
    bind(TerrainTexture.class);
    var resourceLoaderBinder = Multibinder.newSetBinder(binder(), ResourceLoader.class);
    resourceLoaderBinder.addBinding().to(TerrainTexture.class);
  }
}
