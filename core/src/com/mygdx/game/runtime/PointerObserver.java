package com.mygdx.game.runtime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mygdx.game.Config;
import com.mygdx.game.domain.GeoPoint;

import io.vavr.control.Option;

/**
 * Observes which GeoPoint is currently selected
 * and streams events about the selection.
 */
@Singleton
public final class PointerObserver extends GameComponentBase
                                   implements InputProcessor {

    @Inject
    public PointerState pointerState;

    @Override
    public void initialize() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        var x = screenX / Config.SpriteSize;
        var y = (Gdx.graphics.getHeight() - screenY) / Config.SpriteSize;

        pointerState.setPosition(Option.of(new GeoPoint(x,y)));
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // TODO Auto-generated method stub
        return false;
    }

}
