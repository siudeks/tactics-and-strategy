package com.mygdx.game.resources;

import com.google.inject.multibindings.Multibinder;
import com.mygdx.game.ScanModuleBase;

public class DiModuleResources extends ScanModuleBase {

  @Override
  protected void configure() {
    bind(DesertRatsTexture.class);
    bind(TerrainTexture.class);
    var resourceLoaderBinder = Multibinder.newSetBinder(binder(), GameComponent.class);
    super.findImplementations(GameComponent.class)
      .forEach(it -> resourceLoaderBinder.addBinding().to(it));
  }
}
