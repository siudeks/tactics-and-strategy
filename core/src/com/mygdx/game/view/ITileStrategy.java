package com.mygdx.game.view;

public interface ITileStrategy
{
    boolean CanExecute(LocationType[] neighbors);

    TextureHolder Execute(LocationType[] neighbors);
}
