package com.mygdx.game.runtime;

import com.google.inject.Singleton;
import com.mygdx.game.domain.GeoPoint;

import io.vavr.control.Option;
import lombok.Data;

/**
 * Contains data related to pointer (e.g. mouse pointer on desktop app) known location and 
 * pointer command. 
 * <p>
 * To implement that abstract Pointer state we need to translate every platform-dependatn 
 * input operation as well-known pointer command like 'select', 'zoom' etc.
 */
@Data
@Singleton
public class PointerState {
    private Option<GeoPoint> position = Option.none();
}

