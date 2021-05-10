package com.mygdx.game.domain;

import lombok.Data;
import lombok.Value;

public interface IntendedViewPosition {

/**
 * Contains intended position of the center of the Window.
 * 
 * <p>Window shows only part of the map, so, to visualise it's content, we
 */
  interface Provider {
    Current current();
  }

  @Value(staticConstructor = "of")
  class Current {
    private int x;
    private int y;
  }
}

