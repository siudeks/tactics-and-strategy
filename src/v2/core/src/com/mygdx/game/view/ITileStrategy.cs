package com.mygdx.game.view;

public interface ITileStrategy
{
    bool CanExecute(LocationType[] neighbors);

    TextureHolder Execute(LocationType[] neighbors);
}
