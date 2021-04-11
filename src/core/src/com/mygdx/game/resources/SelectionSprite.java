package com.mygdx.game.resources;

import com.badlogic.gdx.graphics.Texture;

import lombok.Value;

/**
 * Sprite used to mark a GeoPoint as selected.
 * Singleton instance.
 */
@Value
public class SelectionSprite {
  private Texture texture;
}
