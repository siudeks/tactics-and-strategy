package com.mygdx.game.domain;

import lombok.Value;

/**
 * Represents a square on 2D map.
 */
@Value
public class GeoPoint
{
    /** X location of the middle of the square. */
    public int x;

    /** Y location of the middle of the square. */
    public int y;

    public GeoPoint top() { return new GeoPoint(x, y + 1);}
    public GeoPoint down() { return new GeoPoint(x, y - 1);}
    public GeoPoint left() { return new GeoPoint(x - 1, y);}
    public GeoPoint right() { return new GeoPoint(x + 1, y);}
    public GeoPoint topLeft() { return new GeoPoint(x - 1, y + 1);}
    public GeoPoint topRight() { return new GeoPoint(x + 1, y + 1);}
    public GeoPoint downLeft() { return new GeoPoint(x - 1, y - 1);}
    public GeoPoint downRight() { return new GeoPoint(x + 1, y - 1);}
}

