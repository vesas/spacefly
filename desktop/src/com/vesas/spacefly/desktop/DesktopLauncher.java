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
		config.width = 1600;
		config.height = 1000;

		new LwjglApplication(new SpaceflyGame(), config);

	}
}
