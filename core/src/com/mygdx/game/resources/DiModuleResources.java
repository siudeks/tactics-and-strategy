package com.mygdx.game.resources;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mygdx.game.view.DefaultStrategy;

public class DiModuleResources extends AbstractModule {

  @Override
  protected void configure() {
    bind(DesertRatsTexture.class);
    bind(TerrainTexture.class);
    var resourceLoaderBinder = Multibinder.newSetBinder(binder(), GameComponent.class);
    resourceLoaderBinder.addBinding().to(TerrainTexture.class);
    resourceLoaderBinder.addBinding().to(WaterTextures.class);
    resourceLoaderBinder.addBinding().to(DefaultStrategy.class);
    
  }
}
