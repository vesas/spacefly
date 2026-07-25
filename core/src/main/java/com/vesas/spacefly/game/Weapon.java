package com.vesas.spacefly.game;

import java.util.Random;

public class Weapon {

    /**
     * All weapon damage and monster health live on this integer scale. Base damage and
     * monster health values were multiplied by this from their original small integers,
     * so hit-counts are unchanged — the only effect is that damage numbers read big
     * (Borderlands-style "40!" instead of "4!").
     */
    public static final int DMG_SCALE = 10;

    /** A critical hit deals this multiple of the shot's damage and pops a yellow number. */
    public static final float CRIT_MULT = 2.0f;

    public enum Archetype {
        BLASTER, SCATTER_GUN, BEAM_REPEATER, RAIL_LAUNCHER
    }

    /**
     * Loot tier. Higher tiers roll better stats on a wider band, and colour the drop
     * sprite + HUD name. {@code statMult} is the central multiplier applied to a rolled
     * weapon's damage / fire-rate / projectile-speed; {@code variance} is the +/- band
     * each of those stats is rolled through independently, so a high tier can be great
     * in one stat and merely good in another — that's what makes the comparison matter.
     */
    public enum Rarity {
        COMMON   ( "",           0.80f, 0.82f, 0.86f, 1.00f, 0.00f ),
        UNCOMMON ( "Enhanced",   0.42f, 0.86f, 0.38f, 1.10f, 0.10f ),
        RARE     ( "Superior",   0.33f, 0.62f, 1.00f, 1.22f, 0.14f ),
        EPIC     ( "Prototype",  0.74f, 0.42f, 1.00f, 1.38f, 0.18f ),
        LEGENDARY( "Vault-Tech", 1.00f, 0.60f, 0.10f, 1.58f, 0.22f );

        public final String prefix;
        public final float r, g, b;
        public final float statMult;
        public final float variance;

        Rarity( String prefix, float r, float g, float b, float statMult, float variance ) {
            this.prefix = prefix;
            this.r = r;
            this.g = g;
            this.b = b;
            this.statMult = statMult;
            this.variance = variance;
        }
    }

    private static final Archetype[] DROPPABLE = {
        Archetype.SCATTER_GUN, Archetype.BEAM_REPEATER, Archetype.RAIL_LAUNCHER
    };

    public final Archetype archetype;
    public final Rarity rarity;
    public final String name;
    public final float fireCooldown;
    public final float bulletSpeed;
    public final int damage;
    public final float spread;
    public final int projectileCount;
    public final int ammoCost;
    public final float critChance;

    public Weapon(Archetype archetype, Rarity rarity, String name, float fireCooldown, float bulletSpeed,
                  int damage, float spread, int projectileCount, int ammoCost, float critChance) {
        this.archetype = archetype;
        this.rarity = rarity;
        this.name = name;
        this.fireCooldown = fireCooldown;
        this.bulletSpeed = bulletSpeed;
        this.damage = damage;
        this.spread = spread;
        this.projectileCount = projectileCount;
        this.ammoCost = ammoCost;
        this.critChance = critChance;
    }

    /** Base, unrolled Common instance for an archetype. This is the fixed reference the
     *  starting Blaster and the "press 1-4" debug weapons use, so early-game balance is
     *  identical. Rolled drops are built from these bases in {@link #rollStats}. */
    public static Weapon forArchetype(Archetype archetype) {
        switch (archetype) {
            case BLASTER:       return new Weapon(Archetype.BLASTER,       Rarity.COMMON, "Blaster",       0.085f, 11.6f, 10, 0.00f, 1, 1, 0.08f);
            case SCATTER_GUN:   return new Weapon(Archetype.SCATTER_GUN,   Rarity.COMMON, "Scatter Gun",   0.35f,   7.0f, 20, 0.35f, 5, 3, 0.05f);
            case BEAM_REPEATER: return new Weapon(Archetype.BEAM_REPEATER, Rarity.COMMON, "Beam Repeater", 0.04f,  13.0f, 10, 0.18f, 1, 1, 0.07f);
            case RAIL_LAUNCHER: return new Weapon(Archetype.RAIL_LAUNCHER, Rarity.COMMON, "Rail Launcher", 0.70f,  18.0f, 40, 0.00f, 1, 2, 0.12f);
            default: throw new IllegalArgumentException("Unknown archetype: " + archetype);
        }
    }

    public static Weapon randomDrop(Random rng) {
        return randomDrop(rng, 1);
    }

    /** A droppable weapon with a floor-biased rarity and rolled stats. Deeper floors shift
     *  probability mass toward the higher tiers. */
    public static Weapon randomDrop(Random rng, int floorNumber) {
        Archetype a = DROPPABLE[rng.nextInt(DROPPABLE.length)];
        Rarity rarity = rollRarity(rng, floorNumber);
        return rollStats(a, rarity, rng);
    }

    private static Rarity rollRarity(Random rng, int floorNumber) {
        float f = Math.max(0, floorNumber - 1);
        // Weighted table; deeper floors bleed weight out of Common and into the top tiers.
        float wCommon    = Math.max(3f, 52f - f * 6f);
        float wUncommon  = 30f;
        float wRare      = 12f + f * 2.5f;
        float wEpic      = 5f  + f * 2.2f;
        float wLegendary = 1f  + f * 1.1f;

        float total = wCommon + wUncommon + wRare + wEpic + wLegendary;
        float roll = rng.nextFloat() * total;

        if ((roll -= wCommon)   < 0) return Rarity.COMMON;
        if ((roll -= wUncommon) < 0) return Rarity.UNCOMMON;
        if ((roll -= wRare)     < 0) return Rarity.RARE;
        if ((roll -= wEpic)     < 0) return Rarity.EPIC;
        return Rarity.LEGENDARY;
    }

    private static Weapon rollStats(Archetype a, Rarity rarity, Random rng) {
        Weapon base = forArchetype(a);

        // Each stat is rolled independently around the rarity's central multiplier, so a
        // drop might have punchy damage but sluggish fire rate — a real trade to weigh.
        float dMult = rollMult(rng, rarity);
        float rMult = rollMult(rng, rarity);
        float sMult = rollMult(rng, rarity);

        int   damage       = Math.max(1, Math.round(base.damage * dMult));
        float fireCooldown = base.fireCooldown / rMult;   // higher mult -> shorter cooldown -> faster
        float bulletSpeed  = base.bulletSpeed * sMult;
        float critChance   = Math.min(0.75f, base.critChance * rollMult(rng, rarity));

        String name = rarity.prefix.isEmpty() ? base.name : rarity.prefix + " " + base.name;

        // Spread / projectile count / ammo cost stay at base: they are the archetype's identity.
        return new Weapon(a, rarity, name, fireCooldown, bulletSpeed, damage,
                base.spread, base.projectileCount, base.ammoCost, critChance);
    }

    private static float rollMult(Random rng, Rarity rarity) {
        return rarity.statMult * (1f + (rng.nextFloat() * 2f - 1f) * rarity.variance);
    }

    // 1–5 bar ratings for the HUD
    public int damageBars() {
        // Undo DMG_SCALE so the bar scale matches the original 1–5 archetype spread; rolled
        // high-tier weapons push above their base bar count.
        return Math.max(1, Math.min(5, Math.round(damage / (float) DMG_SCALE)));
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
