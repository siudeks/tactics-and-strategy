package com.mygdx.game.view;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mygdx.game.runtime.IBatchDrawer;
import com.mygdx.game.runtime.PointerDrawer;

public class DiModuleView extends AbstractModule {
  @Override
  protected void configure() {
    var batchDriverBinder = Multibinder.newSetBinder(binder(), IBatchDrawer.class);
    batchDriverBinder.addBinding().to(PointerDrawer.class);

    var strategyBinder = Multibinder.newSetBinder(binder(), ITileStrategy.class);
    strategyBinder.addBinding().to(CityStrategy.class);
    strategyBinder.addBinding().to(CoastWithLandToTheNorthStrategy.class);
    strategyBinder.addBinding().to(CoastWithLandToTheSouthStrategy.class);
    strategyBinder.addBinding().to(GroundStrategy.class);
    strategyBinder.addBinding().to(LandUnitStrategy.class);
    bind(ITileFallbackStrategy.class).to(DefaultStrategy.class);
  }
}
