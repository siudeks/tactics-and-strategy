package com.mygdx.game.view;

public interface Strategy {
  boolean CanExecute(LocationType[] neighbors);
  TextureHolder Execute(LocationType[] neighbors);
}
