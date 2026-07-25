package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

/**
 * Global game settings, persisted via libGDX {@link Preferences}.
 *
 * Currently holds the master sound volume (0..1) applied to every sound played
 * through {@link G#playSound}. Load once after the application is created.
 */
public class Settings {

    private static final String PREFS_NAME = "spacefly-settings";
    private static final String KEY_MASTER_VOLUME = "masterVolume";
    private static final float DEFAULT_MASTER_VOLUME = 1.0f;

    private static float masterVolume = DEFAULT_MASTER_VOLUME;
    private static Preferences prefs;

    private Settings() {}

    /** Load persisted settings. Call once after the LibGDX application is created. */
    public static void load() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        masterVolume = prefs.getFloat(KEY_MASTER_VOLUME, DEFAULT_MASTER_VOLUME);
    }

    public static float getMasterVolume() {
        return masterVolume;
    }

    /** Set and persist the master volume, clamped to [0, 1]. */
    public static void setMasterVolume(float value) {
        masterVolume = MathUtils.clamp(value, 0f, 1f);
        if (prefs != null) {
            prefs.putFloat(KEY_MASTER_VOLUME, masterVolume);
            prefs.flush();
        }
    }
}
