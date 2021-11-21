package com.vesas.spacefly.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.vesas.spacefly.SpaceflyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "spacefly";
		config.fullscreen = false;
		config.vSyncEnabled = true;
		config.foregroundFPS = 60;
//		cfg.backgroundFPS = 20;
//		cfg.width = 1000;
//		cfg.height = 700;
		
		config.width = 1800;
		config.height = 1200;

		new LwjglApplication(new SpaceflyGame(), config);

	}
}
