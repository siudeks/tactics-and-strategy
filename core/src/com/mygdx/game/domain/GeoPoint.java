package com.mygdx.game.domain;

/**
 * Represents a square on 2D map.
 */
public class GeoPoint
{
    /** X location of the middle of the square. */
    public int X;

    /** Y location of the middle of the square. */
    public int Y;

    /**
     * Creates a new instance of {@link GeoPoint}.
     * @param x X coordinate
     * @param y Y coordinate
     */
    public GeoPoint(int x, int y) {
        X = x;
        Y = y;
    }

    public GeoPoint Top() { return new GeoPoint(X, Y + 1);}
    public GeoPoint Down() { return new GeoPoint(X, Y - 1);}
    public GeoPoint Left() { return new GeoPoint(X - 1, Y);}
    public GeoPoint Right() { return new GeoPoint(X + 1, Y);}
    public GeoPoint TopLeft() { return new GeoPoint(X - 1, Y + 1);}
    public GeoPoint TopRight() { return new GeoPoint(X + 1, Y + 1);}
    public GeoPoint DownLeft() { return new GeoPoint(X - 1, Y - 1);}
    public GeoPoint DownRight() { return new GeoPoint(X + 1, Y - 1);}
}

