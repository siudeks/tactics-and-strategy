package com.mygdx.game.domain;

import lombok.Value;

/// <summary>
/// Represents an island definition.
/// </summary>
/// <remarks>
/// Corners field represents ordered in clock-wise order collection of corners of the island.
/// </remarks>
@Value
public class IslandEntity
{
    public GeoPoint[] Corners;
}

