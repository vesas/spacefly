package com.vesas.spacefly.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.vesas.spacefly.SpaceflyGame;

public class DesktopLauncher {

	
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		
		config.setTitle("spacefly");
		// config.useVsync(false);

		// config.fullscreen = false;
		// config.vSyncEnabled = false;
		// config.foregroundFPS = 60;
		// config.width = 1100;
		// config.height = 650;
		// config.width = 640*6;
		// config.height = 480*4;aaaaaa
		// config.width = 640*4;
		// config.height = 480*4;
		config.setWindowedMode(640*2, 480*2);
		// config.width = (int)(640*1.0);
		// config.height = (int)(480*1.0);

		new Lwjgl3Application(new SpaceflyGame(), config);

	}
}
