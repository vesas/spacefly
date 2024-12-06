package com.vesas.spacefly.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.vesas.spacefly.TestGame;

public final class TestLauncher {

	private TestLauncher() {
		// empty
	}
	
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		
		config.setTitle("spacefly");
		config.setWindowedMode(1920, 1280);
		test3(config);
	}

	// Quadtree experiment
	public static void test1(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new TestGame(1), config);
	}

	// Noise expriment
	public static void test2(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new TestGame(2), config);
	}

	// Procedural room experiment
	public static void test3(Lwjgl3ApplicationConfiguration config) {
		new Lwjgl3Application(new TestGame(3), config);
	}
}
