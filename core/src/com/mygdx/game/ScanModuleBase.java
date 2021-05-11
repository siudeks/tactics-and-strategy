package com.mygdx.game;

import com.google.inject.AbstractModule;

import org.reflections.Reflections;

import io.vavr.collection.Iterator;

public abstract class ScanModuleBase extends AbstractModule {
    private final String packageName = ScanModuleBase.class.getPackage().getName();

    protected <T> Iterator<Class<? extends T>> findImplementations(Class<T> contract) {
        var packageReflections = new Reflections(packageName);
        return Iterator.ofAll(packageReflections
            .getSubTypesOf(contract));
    }
}