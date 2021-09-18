package com.mygdx.game.runtime;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.resources.GameComponent;

/**
 * Simplifies inheritance of GameComponen by overriding with empty methods all livvecycles on the Component life.
 * Toy may override only methods which you really want to support by your component
 */
public class GameComponentBase implements GameComponent, IBatchDrawer {
  
  protected GameComponentBase() {
  }

  @Override
  public void loadTextures() {
  }

  @Override
  public void useTextures() {
  }

  @Override
  public void initialize() {
  }

  @Override
  public void OnDraw(SpriteBatch spriteBatch) {
  }
}
