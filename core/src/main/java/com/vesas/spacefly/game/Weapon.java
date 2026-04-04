package com.vesas.spacefly.game;

import java.util.Random;

public class Weapon {

    public enum Archetype {
        BLASTER, SCATTER_GUN, BEAM_REPEATER, RAIL_LAUNCHER
    }

    private static final Archetype[] DROPPABLE = {
        Archetype.SCATTER_GUN, Archetype.BEAM_REPEATER, Archetype.RAIL_LAUNCHER
    };

    public final Archetype archetype;
    public final String name;
    public final float fireCooldown;
    public final float bulletSpeed;
    public final int damage;
    public final float spread;
    public final int projectileCount;
    public final int ammoCost;

    public Weapon(Archetype archetype, String name, float fireCooldown, float bulletSpeed,
                  int damage, float spread, int projectileCount, int ammoCost) {
        this.archetype = archetype;
        this.name = name;
        this.fireCooldown = fireCooldown;
        this.bulletSpeed = bulletSpeed;
        this.damage = damage;
        this.spread = spread;
        this.projectileCount = projectileCount;
        this.ammoCost = ammoCost;
    }

    public static Weapon forArchetype(Archetype archetype) {
        switch (archetype) {
            case BLASTER:       return new Weapon(Archetype.BLASTER,       "Blaster",       0.085f, 11.6f, 1, 0.00f, 1, 1);
            case SCATTER_GUN:   return new Weapon(Archetype.SCATTER_GUN,   "Scatter Gun",   0.35f,   7.0f, 2, 0.35f, 5, 3);
            case BEAM_REPEATER: return new Weapon(Archetype.BEAM_REPEATER, "Beam Repeater", 0.04f,  13.0f, 1, 0.18f, 1, 1);
            case RAIL_LAUNCHER: return new Weapon(Archetype.RAIL_LAUNCHER, "Rail Launcher", 0.70f,  18.0f, 4, 0.00f, 1, 2);
            default: throw new IllegalArgumentException("Unknown archetype: " + archetype);
        }
    }

    public static Weapon randomDrop(Random rng) {
        return forArchetype(DROPPABLE[rng.nextInt(DROPPABLE.length)]);
    }

    // 1–5 bar ratings for the HUD
    public int damageBars() {
        return Math.max(1, Math.min(5, damage));
    }

    public int speedBars() {
        if (fireCooldown >= 0.5f)  return 1;
        if (fireCooldown >= 0.25f) return 2;
        if (fireCooldown >= 0.10f) return 3;
        if (fireCooldown >= 0.05f) return 4;
        return 5;
    }

    public int rangeBars() {
        if (bulletSpeed >= 17f) return 5;
        if (bulletSpeed >= 13f) return 4;
        if (bulletSpeed >= 10f) return 3;
        if (bulletSpeed >= 8f)  return 2;
        return 1;
    }

    /** Shot pattern: 1 = tight single shot, 5 = wide multi-pellet. */
    public int shotBars() {
        if (projectileCount >= 5) return 5;
        if (projectileCount >= 3) return 4;
        if (spread > 0.2f)        return 3;
        if (spread > 0.0f)        return 2;
        return 1;
    }
}
