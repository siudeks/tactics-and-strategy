package com.mygdx.game.resources;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mygdx.game.ScanModuleBase;
import com.mygdx.game.runtime.GameComponentBase;
import com.mygdx.game.runtime.PointerDrawer;
import com.mygdx.game.runtime.PointerObserver;
import com.mygdx.game.view.FallBackStrategy;
import com.mygdx.game.view.GroundStrategy;
import com.mygdx.game.view.GroundTextures;

import io.vavr.collection.List;

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
