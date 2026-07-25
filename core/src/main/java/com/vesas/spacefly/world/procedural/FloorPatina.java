package com.vesas.spacefly.world.procedural;

import java.nio.ByteBuffer;
import java.util.Random;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Per-feature floor texture splatting. Each room / corridor reads as its own surface without ever
 * making a hard seam at a doorway, via two layers:
 *
 * <ul>
 *   <li><b>Material</b> — a subtle surface texture (grain / brushed / mottle / fleck) <b>baked</b>
 *       into the floor pixmap ({@link #bakeMaterial}) from continuous world-space noise at a fixed
 *       scale. Because it is sampled in world units (not stretched over the quad) it never distorts,
 *       whatever the room / corridor aspect ratio. Its grey is centred on the floor colour so it
 *       reads both darker and lighter, and it fades to the neutral floor colour at the edges.</li>
 *   <li><b>Wear</b> — organic grime pooled toward the interior, drawn as a per-room overlay (a soft
 *       vignette texture). Stains tolerate stretching, so this stays a cheap stretched quad.</li>
 * </ul>
 *
 * <p>{@link #pick} / {@link #pickMaterial} are pure maths (no GL, no {@code GenSeed.random}); the
 * wear textures build lazily on first use. This keeps floor generation deterministic + headless-safe.
 */
public final class FloorPatina
{
    private FloorPatina() {}

    // --- wear layer: organic grime, vignette mask, pooled toward the interior ------------------
    public static final int STREAKS  = 0;
    public static final int BLOTCHES = 1;
    public static final int SPECKLE  = 2;
    public static final int PATCHES  = 3;
    public static final int CLEAN    = 4;
    private static final int WEAR_ARCHETYPES = 5;
    private static final int WEAR_VARIANTS   = 3;
    private static final int WEAR_TOTAL = WEAR_ARCHETYPES * WEAR_VARIANTS; // 15

    // --- material layer: subtle surface texture, baked into the floor pixmap -------------------
    public static final int GRAIN   = 0; // fine cast / sandblasted grain
    public static final int BRUSHED = 1; // directional brushed metal
    public static final int MOTTLE  = 2; // soft uneven cast
    public static final int FLECK   = 3; // sparse darker flecks / speckled stone
    private static final int MAT_ARCHETYPES = 4;
    private static final int MAT_VARIANTS   = 2;

    // World units that one "tile" of material noise spans. Fixes grain density in world space, so a
    // long room and a short room show the same-sized grain (no stretching).
    private static final float MATCH_UNITS = 8f;

    private static final int TEX_SIZE = 256;

    private static TextureRegion[] wearRegions; // lazily built on the GL thread

    // ===========================================================================================
    // Material (baked)
    // ===========================================================================================

    /** A material selection for one feature: which surface, its noise seed, tint and alpha. */
    public static final class MatPick
    {
        public int archetype;
        public int seed;
        public float r, g, b, a;
    }

    /** Stable per-feature seed from the feature centre — never draws from GenSeed.random. */
    public static long featureSeed( long worldSeed, int floor, float cx, float cy )
    {
        return worldSeed * 1000003L + floor * 9176L
                + Math.round( cx * 7f ) * 73856093L + Math.round( cy * 7f ) * 19349663L;
    }

    /** Pick a material for a room / corridor. Grey is centred on the floor colour by the tint. */
    public static MatPick pickMaterial( long seed, FloorTheme theme )
    {
        Random rnd = new Random( seed );
        MatPick m = new MatPick();
        m.archetype = rnd.nextInt( MAT_ARCHETYPES );
        int sub = rnd.nextInt( MAT_VARIANTS );
        m.seed = 5000 + (m.archetype * MAT_VARIANTS + sub) * 6151;
        m.a = 0.45f + rnd.nextFloat() * 0.22f;
        float mj = 0.05f;
        m.r = clamp01( theme.floorColor.r * 2f * (1f - rnd.nextFloat() * mj) );
        m.g = clamp01( theme.floorColor.g * 2f * (1f - rnd.nextFloat() * mj) );
        m.b = clamp01( theme.floorColor.b * 2f * (1f - rnd.nextFloat() * mj) );
        return m;
    }

    /** Material grey (0..1) at sample coords (already in "tile" units). Low-contrast surface. */
    public static float materialGrey( int archetype, int seed, float sx, float sy )
    {
        float dev;
        switch( archetype )
        {
            case GRAIN:
                dev = (fbm( sx * 38f, sy * 38f, seed, 2 ) - 0.5f) * 0.22f;
                break;
            case BRUSHED:
                dev = (fbm( sx * 3.0f, sy * 55f, seed, 2 ) - 0.5f) * 0.20f;
                break;
            case MOTTLE:
                dev = (fbm( sx * 6.0f, sy * 6.0f, seed, 3 ) - 0.5f) * 0.26f;
                break;
            case FLECK:
            default:
                dev = -smoothstep( 0.60f, 0.82f, fbm( sx * 30f, sy * 30f, seed, 2 ) ) * 0.24f;
                break;
        }
        return clamp01( 0.5f + dev );
    }

    /**
     * Bake the material into a floor pixmap, in place. Only touches non-transparent pixels (so walls,
     * columns and octagon corners left CLEAR by the floor build stay clear). The material is sampled
     * from continuous world-space noise, so it never stretches. It fades to zero over {@code fadeDist}
     * world units at the feature edges selected by {@code fadeX}/{@code fadeY}, so the surface meets
     * the neutral floor colour (and the corridors) seamlessly at doorways.
     *
     * @param ppu    pixels per world unit of the pixmap (64)
     * @param x0,y0  world position of the pixmap's lower-left, for continuous cross-feature variety
     * @param wUnits,hUnits  feature size in world units
     * @param fadeX  fade near the left/right edges (rooms: yes; WE corridor: yes; SN corridor: no)
     * @param fadeY  fade near the bottom/top edges (rooms: yes; SN corridor: yes; WE corridor: no)
     */
    public static void bakeMaterial( Pixmap floor, float ppu, float x0, float y0,
            float wUnits, float hUnits, MatPick m, boolean fadeX, boolean fadeY, float fadeDist )
    {
        final int W = floor.getWidth();
        final int H = floor.getHeight();

        // Coarse material grid (~0.25 world units), bilinearly sampled per pixel — the noise is
        // smooth, so this is visually identical to per-pixel evaluation but far cheaper on big rooms.
        final float cell = 0.25f;
        final int gw = Math.max( 2, (int) (wUnits / cell) + 2 );
        final int gh = Math.max( 2, (int) (hUnits / cell) + 2 );
        float[] grid = new float[gw * gh];
        for( int gy = 0; gy < gh; gy++ )
            for( int gx = 0; gx < gw; gx++ )
                grid[gy * gw + gx] = materialGrey( m.archetype, m.seed,
                        (x0 + gx * cell) / MATCH_UNITS, (y0 + gy * cell) / MATCH_UNITS );

        final float mr = clamp01( m.r ), mg = clamp01( m.g ), mb = clamp01( m.b );
        ByteBuffer buf = floor.getPixels();

        for( int py = 0; py < H; py++ )
        {
            float ly = (py + 0.5f) / ppu;         // local world units, from the bottom
            float gyf = ly / cell;
            int gy0 = clampi( (int) gyf, 0, gh - 2 );
            float ty = gyf - gy0;
            float fadeYv = fadeY ? plateauDist( ly, hUnits, fadeDist ) : 1f;

            for( int px = 0; px < W; px++ )
            {
                int idx = (py * W + px) * 4;
                if( (buf.get( idx + 3 ) & 0xff) == 0 )
                    continue; // not floor

                float lx = (px + 0.5f) / ppu;
                float gxf = lx / cell;
                int gx0 = clampi( (int) gxf, 0, gw - 2 );
                float tx = gxf - gx0;

                float g00 = grid[gy0 * gw + gx0],       g10 = grid[gy0 * gw + gx0 + 1];
                float g01 = grid[(gy0 + 1) * gw + gx0], g11 = grid[(gy0 + 1) * gw + gx0 + 1];
                float grey = lerp( lerp( g00, g10, tx ), lerp( g01, g11, tx ), ty );

                float fade = fadeYv * (fadeX ? plateauDist( lx, wUnits, fadeDist ) : 1f);
                float a = m.a * fade;
                if( a <= 0f )
                    continue;

                int r = buf.get( idx )     & 0xff;
                int g = buf.get( idx + 1 ) & 0xff;
                int b = buf.get( idx + 2 ) & 0xff;
                buf.put( idx,     (byte) (int) (mr * grey * 255f * a + r * (1f - a)) );
                buf.put( idx + 1, (byte) (int) (mg * grey * 255f * a + g * (1f - a)) );
                buf.put( idx + 2, (byte) (int) (mb * grey * 255f * a + b * (1f - a)) );
            }
        }
    }

    /** 1 across the interior, easing to 0 within {@code fade} units of either end of [0,size]. */
    private static float plateauDist( float pos, float size, float fade )
    {
        return smoothstep( 0f, fade, pos ) * smoothstep( 0f, fade, size - pos );
    }

    // ===========================================================================================
    // Wear (overlay)
    // ===========================================================================================

    /** A per-room wear selection: which texture, its tint, alpha and mirror flips. */
    public static final class Pick
    {
        public int wearVariant;
        public float wearR, wearG, wearB, wearA;
        public boolean wearFlipX, wearFlipY;
    }

    /**
     * Choose the wear layer for a room. Pure computation, fully deterministic in {@code seed}.
     *
     * @param depth01 normalized distance from the floor start (0 at entrance .. 1 at the far exit)
     */
    public static Pick pick( long seed, float depth01, FloorTheme theme )
    {
        Random rnd = new Random( seed );
        Pick p = new Pick();

        int wearArch = rnd.nextInt( WEAR_ARCHETYPES );
        int wearSub = rnd.nextInt( WEAR_VARIANTS );
        p.wearVariant = wearArch * WEAR_VARIANTS + wearSub;
        p.wearFlipX = rnd.nextBoolean();
        p.wearFlipY = rnd.nextBoolean();

        float wear = 0.14f + rnd.nextFloat() * 0.16f; // ~0.14 .. 0.30
        if( wearArch == CLEAN ) wear *= 0.5f;
        p.wearA = wear;

        // Neutral with a small per-room jitter, drifting toward a per-theme "deep" tint with depth.
        // The bulk of the perceptible depth drift rides the ambient light, not this.
        float wj = 0.06f;
        float jr = 1f - rnd.nextFloat() * wj;
        float jg = 1f - rnd.nextFloat() * wj;
        float jb = 1f - rnd.nextFloat() * wj;
        float drift = 0.18f * clamp01( depth01 );
        p.wearR = clamp01( jr * ((1f - drift) + drift * destR( theme )) );
        p.wearG = clamp01( jg * ((1f - drift) + drift * destG( theme )) );
        p.wearB = clamp01( jb * ((1f - drift) + drift * destB( theme )) );

        return p;
    }

    /** Lazily builds the wear set and returns the requested region. */
    public static TextureRegion wearRegion( int variant )
    {
        if( wearRegions == null )
            buildWear();
        return wearRegions[variant];
    }

    public static void dispose()
    {
        if( wearRegions == null )
            return;
        for( int i = 0; i < wearRegions.length; i++ )
            if( wearRegions[i] != null )
                wearRegions[i].getTexture().dispose();
        wearRegions = null;
    }

    private static void buildWear()
    {
        wearRegions = new TextureRegion[WEAR_TOTAL];
        for( int v = 0; v < WEAR_TOTAL; v++ )
            wearRegions[v] = new TextureRegion( buildWearTexture( v ) );
    }

    private static Texture buildWearTexture( int index )
    {
        Pixmap pm = new Pixmap( TEX_SIZE, TEX_SIZE, Pixmap.Format.RGBA8888 );
        pm.setBlending( Pixmap.Blending.None );

        final int archetype = index / WEAR_VARIANTS;
        final int seed = 1000 + index * 7919;
        final float inv = 1f / TEX_SIZE;

        for( int y = 0; y < TEX_SIZE; y++ )
        {
            for( int x = 0; x < TEX_SIZE; x++ )
            {
                float u = x * inv, w = y * inv;
                float n, cov, grey;

                switch( archetype )
                {
                    case STREAKS:
                        n = fbm( u * 2.0f, w * 13.0f, seed, 4 );
                        cov = smoothstep( 0.42f, 0.72f, n ); grey = 0.42f; break;
                    case BLOTCHES:
                        n = fbm( u * 2.2f, w * 2.2f, seed, 3 );
                        cov = smoothstep( 0.48f, 0.72f, n ); grey = 0.50f; break;
                    case SPECKLE:
                        n = fbm( u * 11f, w * 11f, seed, 3 );
                        cov = smoothstep( 0.58f, 0.85f, n ) * 0.85f; grey = 0.40f; break;
                    case PATCHES:
                        n = fbm( u * 5.0f, w * 5.0f, seed, 3 );
                        cov = smoothstep( 0.46f, 0.66f, n ); grey = 0.44f; break;
                    case CLEAN:
                    default:
                        n = fbm( u * 4.0f, w * 4.0f, seed, 3 );
                        cov = smoothstep( 0.64f, 0.82f, n ) * 0.55f; grey = 0.52f; break;
                }

                float dx = (u - 0.5f) * 2f, dy = (w - 0.5f) * 2f;
                float d = (float) Math.sqrt( dx * dx + dy * dy );
                float mask = 1f - smoothstep( 0.55f, 1.0f, d );

                float alpha = clamp01( cov ) * mask;
                float gg = clamp01( grey + (n - 0.5f) * 0.16f );
                pm.drawPixel( x, y, rgba( gg, gg, gg, alpha ) );
            }
        }

        Texture tex = new Texture( pm );
        tex.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
        pm.dispose();
        return tex;
    }

    // --- per-theme depth-drift destination tint (multipliers, <= 1) --------------------------

    private static float destR( FloorTheme t )
    {
        switch( t )
        {
            case REACTOR: return 1.00f;
            case BIOLAB:  return 0.90f;
            case CRYO:    return 0.86f;
            case STATION:
            default:      return 0.92f;
        }
    }

    private static float destG( FloorTheme t )
    {
        switch( t )
        {
            case REACTOR: return 0.88f;
            case BIOLAB:  return 1.00f;
            case CRYO:    return 0.94f;
            case STATION:
            default:      return 0.95f;
        }
    }

    private static float destB( FloorTheme t )
    {
        switch( t )
        {
            case REACTOR: return 0.80f;
            case BIOLAB:  return 0.90f;
            case CRYO:    return 1.00f;
            case STATION:
            default:      return 1.00f;
        }
    }

    // --- noise + helpers ----------------------------------------------------------------------

    private static float fbm( float x, float y, int seed, int octaves )
    {
        float amp = 1f, freq = 1f, sum = 0f, norm = 0f;
        for( int i = 0; i < octaves; i++ )
        {
            sum += amp * valueNoise( x * freq, y * freq, seed + i * 101 );
            norm += amp;
            amp *= 0.5f;
            freq *= 2f;
        }
        return sum / norm;
    }

    private static float valueNoise( float x, float y, int seed )
    {
        int xi = fastFloor( x );
        int yi = fastFloor( y );
        float tx = smooth( x - xi );
        float ty = smooth( y - yi );
        float c00 = vhash( xi, yi, seed );
        float c10 = vhash( xi + 1, yi, seed );
        float c01 = vhash( xi, yi + 1, seed );
        float c11 = vhash( xi + 1, yi + 1, seed );
        return lerp( lerp( c00, c10, tx ), lerp( c01, c11, tx ), ty );
    }

    private static float vhash( int x, int y, int seed )
    {
        int h = seed;
        h = h * 31 + x;
        h = h * 31 + y;
        h ^= (h >>> 15);
        h *= 0x2c1b3c6d;
        h ^= (h >>> 12);
        h *= 0x297a2d39;
        h ^= (h >>> 15);
        return (h & 0x7fffffff) / (float) 0x7fffffff;
    }

    private static int fastFloor( float f )
    {
        int i = (int) f;
        return f < i ? i - 1 : i;
    }

    private static int clampi( int v, int lo, int hi )
    {
        return v < lo ? lo : (v > hi ? hi : v);
    }

    private static float smooth( float t )
    {
        return t * t * (3f - 2f * t);
    }

    private static float smoothstep( float edge0, float edge1, float x )
    {
        float t = clamp01( (x - edge0) / (edge1 - edge0) );
        return t * t * (3f - 2f * t);
    }

    private static float lerp( float a, float b, float t )
    {
        return a + (b - a) * t;
    }

    private static float clamp01( float v )
    {
        return v < 0f ? 0f : (v > 1f ? 1f : v);
    }

    private static int rgba( float r, float g, float b, float a )
    {
        int ri = (int) (r * 255f + 0.5f);
        int gi = (int) (g * 255f + 0.5f);
        int bi = (int) (b * 255f + 0.5f);
        int ai = (int) (a * 255f + 0.5f);
        return (ri << 24) | (gi << 16) | (bi << 8) | ai;
    }
}
