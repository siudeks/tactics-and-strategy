package com.mygdx.game.domain;

import com.google.inject.AbstractModule;
public class DiModule extends AbstractModule {
 
    @Override
    protected void configure() {
        bind(IntendedMapCentre.class).to(IntendedMapCentre.class);
    }
    
}