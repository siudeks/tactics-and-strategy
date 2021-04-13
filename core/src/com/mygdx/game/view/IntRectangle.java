package com.mygdx.game.view;

import lombok.Value;

@Value(staticConstructor = "of")
public class IntRectangle {
  private int srcX;
  private int srcY;
  private int width;
  private int height;
}
