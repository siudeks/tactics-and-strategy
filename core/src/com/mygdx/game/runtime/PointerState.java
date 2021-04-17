package com.mygdx.game.runtime;

import com.mygdx.game.domain.GeoPoint;

import lombok.Value;

/**
 * Contains data related to pointer (e.g. mouse pointer on desktop app) known location and 
 * pointer command. 
 * <p>
 * To implement that abstract Pointer state we need to translate every platform-dependatn 
 * input operation as well-known pointer command like 'select', 'zoom' etc.
 */
@Value
public class PointerState {
    private GeoPoint position;
}

