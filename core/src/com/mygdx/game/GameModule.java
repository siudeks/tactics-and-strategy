package com.mygdx.game;

import com.google.inject.AbstractModule;

public class GameModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(MyGdxGame.class);
  }
}
