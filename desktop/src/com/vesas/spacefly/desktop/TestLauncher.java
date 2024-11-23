package com.vesas.spacefly.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.vesas.spacefly.TestGame;

public final class TestLauncher {

	private TestLauncher() {
		// empty
	}
	
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		
		config.setTitle("spacefly");
		config.setWindowedMode(640*2, 480*2);
		test1(config);
	}

	public static void test1(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new TestGame(1), config);
	}
}
