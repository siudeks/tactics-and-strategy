package com.mygdx.game.domain;

import com.google.inject.Singleton;

import lombok.Data;

/** Contains intended position of the center of the Window. */
@Data
@Singleton
public class IntendedMapCentre {
  private int x;
  private int y;
}
