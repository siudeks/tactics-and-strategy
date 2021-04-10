package com.mygdx.game.domain;

import lombok.Value;

/**
 * Represents an island definition.
 *
 * Corners field represents ordered in clock-wise order collection of corners of the island.
 */
@Value
public class IslandEntity {
  private GeoPoint[] corners;
}

