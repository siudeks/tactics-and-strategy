package com.mygdx.game;

import java.util.Set;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import com.mygdx.game.resources.GameComponent;
import com.mygdx.game.view.Window;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	
	@Inject
	Window window;
	
	@Inject
	Set<GameComponent> resourceLoaders;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		resourceLoaders.forEach(GameComponent::loadTextures);
		resourceLoaders.forEach(GameComponent::useTextures);
		resourceLoaders.forEach(GameComponent::initialize);
		window.Initialize();
	}

	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();

		window.OnDraw(batch);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
