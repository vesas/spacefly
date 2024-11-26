package com.vesas.spacefly.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.vesas.spacefly.SpaceflyGame;

public final class DesktopLauncher {

	private DesktopLauncher() {
		// empty
	}

	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		
		config.setTitle("spacefly");
		config.useVsync(true);
		config.setWindowedMode(1920, 1280);
		config.setForegroundFPS(60);
		
		new Lwjgl3Application(new SpaceflyGame(), config);
	}
}
