package com.mygdx.game.view;

public interface Strategy {
  boolean canExecute(LocationType[] neighbors);
  TextureHolder execute(LocationType[] neighbors);
}
