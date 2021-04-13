package com.mygdx.game.view;

import com.google.inject.AbstractModule;
import com.mygdx.game.runtime.IBatchDrawer;

public class ViewDiModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(IBatchDrawer.class).to(Window.class);
    bind(ITileStrategy.class).to(CityStrategy.class);
    bind(ITileStrategy.class).to(CoastWithLandToTheNorthStrategy.class);
    bind(ITileStrategy.class).to(CoastWithLandToTheSouthStrategy.class);
    bind(ITileStrategy.class).to(GroundStrategy.class);
    bind(ITileStrategy.class).to(LandUnitStrategy.class);
    bind(ITileFallbackStrategy.class).to(DefaultStrategy.class);
    //builder.RegisterType<Window>().As<IGameComponent, IBatchDrawer, ITextureConsumer>().SingleInstance().PropertiesAutowired();
  }
}
