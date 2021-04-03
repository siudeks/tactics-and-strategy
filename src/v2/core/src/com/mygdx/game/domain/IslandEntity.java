package com.mygdx.game.domain;


/**
 * Represents an island definition.
 *
 * Corners field represents ordered in clock-wise order collection of corners of the island.
 */
public record IslandEntity (GeoPoint[] Corners) { }

