package com.mygdx.game.domain;

import com.google.inject.AbstractModule;
import com.mygdx.game.runtime.WindowMoveProcessor;
public class DomainDiModule extends AbstractModule {
 
    @Override
    protected void configure() {
        bind(IntendedViewPosition.Provider.class).to(WindowMoveProcessor.class);
    }
    
}