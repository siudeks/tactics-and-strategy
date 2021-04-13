package com.mygdx.game.view;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mygdx.game.runtime.IBatchDrawer;

public class DiModuleView extends AbstractModule {
  @Override
  protected void configure() {
    bind(IBatchDrawer.class).to(Window.class);
    var strategyBinder = Multibinder.newSetBinder(binder(), ITileStrategy.class);
    strategyBinder.addBinding().to(CityStrategy.class);
    strategyBinder.addBinding().to(CoastWithLandToTheNorthStrategy.class);
    strategyBinder.addBinding().to(CoastWithLandToTheSouthStrategy.class);
    strategyBinder.addBinding().to(GroundStrategy.class);
    strategyBinder.addBinding().to(LandUnitStrategy.class);
    bind(ITileFallbackStrategy.class).to(DefaultStrategy.class);
  }
}
