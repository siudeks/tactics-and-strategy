package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.google.inject.Guice;
import com.mygdx.game.GameModule;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.domain.DomainDiModule;
import com.mygdx.game.view.DiModule;

public class DesktopLauncher {
	public static void main (String[] arg) {
		var config = new LwjglApplicationConfiguration();
		// config.fullscreen = true;

		var injector = Guice.createInjector(
			new GameModule(),
			new DiModule(),
			new DomainDiModule());
		
		var game = injector.getInstance(MyGdxGame.class);
		new LwjglApplication(game, config);
	}
}
