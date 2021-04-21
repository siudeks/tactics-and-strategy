package com.mygdx.game.resources;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mygdx.game.runtime.GameComponentBase;
import com.mygdx.game.runtime.PointerDrawer;
import com.mygdx.game.runtime.PointerObserver;
import com.mygdx.game.view.CoastWithLandToTheNorthStrategy;
import com.mygdx.game.view.CoastWithLandToTheSouthStrategy;
import com.mygdx.game.view.DefaultStrategy;
import com.mygdx.game.view.GroundStrategy;
import com.mygdx.game.view.GroundTextures;

import io.vavr.collection.List;

public class DiModuleResources extends AbstractModule {

  @Override
  protected void configure() {
    bind(DesertRatsTexture.class);
    bind(TerrainTexture.class);
    var resourceLoaderBinder = Multibinder.newSetBinder(binder(), GameComponent.class);
    List.<Class<? extends GameComponentBase>>of(TerrainTexture.class)
      .append(WaterTextures.class)
      .append(DefaultStrategy.class)
      .append(SelectionTexture.class)
      .append(PointerObserver.class)
      .append(PointerDrawer.class)
      .append(LandUnitTextures.class)
      .append(DesertRatsTexture.class)
      .append(GroundStrategy.class)
      .append(GroundTextures.class)
      .append(CoastWithLandToTheNorthStrategy.class)
      .append(CoastWithLandToTheSouthStrategy.class)
      .forEach(it -> resourceLoaderBinder.addBinding().to(it));
  }
}
