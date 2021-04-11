package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		var config = new LwjglApplicationConfiguration();
		// config.fullscreen = true;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
