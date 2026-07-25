package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.vesas.spacefly.game.G;

/**
 * A static background decoration sprite (vent, junction box, floor grate, hazard strip),
 * drawn tinted and optionally rotated, with an optional slow-pulsing LED overlay.
 * Placed by the feature builders and drawn during the lit (visibility) pass.
 */
public class Decal {

    private final TextureRegion region;
    private final float x, y, w, h, rot;
    private final float r, g, b, a;

    // optional pulsing LED overlay
    private TextureRegion ledRegion;
    private float ledX, ledY, ledSize;
    private float ledR, ledG, ledB, ledPhase, ledSpeed = 2.6f;

    public Decal(String regionName, float x, float y, float w, float h, float rot,
                 float r, float g, float b, float a) {
        this.region = G.getAtlas().findRegion(regionName);
        this.x = x; this.y = y; this.w = w; this.h = h; this.rot = rot;
        this.r = r; this.g = g; this.b = b; this.a = a;
    }

    /**
     * Wall-mounted decal centred on the inner wall face at position `along`.
     * side: 0=N, 1=S, 2=W, 3=E. `al` runs along the wall, `dp` is depth across the face.
     */
    public static Decal onWall(String region, int side, float along,
                               float innerL, float innerR, float innerB, float innerT,
                               float al, float dp, float r, float g, float b, float a) {
        switch (side) {
            case 0:  return new Decal(region, along - al / 2f, innerT - dp / 2f, al, dp, 0f,  r, g, b, a);
            case 1:  return new Decal(region, along - al / 2f, innerB - dp / 2f, al, dp, 0f,  r, g, b, a);
            case 2:  return new Decal(region, innerL - al / 2f, along - dp / 2f, al, dp, 90f, r, g, b, a);
            default: return new Decal(region, innerR - al / 2f, along - dp / 2f, al, dp, 90f, r, g, b, a);
        }
    }

    /** Centre of a wall-mounted decal (where a status LED sits), for the given side/along. */
    public static float wallCenterX(int side, float along, float innerL, float innerR) {
        return side == 2 ? innerL : side == 3 ? innerR : along;
    }
    public static float wallCenterY(int side, float along, float innerB, float innerT) {
        return side == 0 ? innerT : side == 1 ? innerB : along;
    }

    /** Adds a pulsing LED dot at (lx,ly), colour c, phase offset, pulse speed (rad/s). */
    public Decal led(float lx, float ly, float size, Color c, float phase, float speed) {
        this.ledRegion = G.getAtlas().findRegion("led");
        this.ledX = lx; this.ledY = ly; this.ledSize = size;
        this.ledR = c.r; this.ledG = c.g; this.ledB = c.b;
        this.ledPhase = phase; this.ledSpeed = speed;
        return this;
    }

    public void draw(Batch batch) {
        float prev = batch.getPackedColor();
        batch.setColor(r, g, b, a);
        if (region != null) {
            if (rot == 0f) batch.draw(region, x, y, w, h);
            else           batch.draw(region, x, y, w / 2f, h / 2f, w, h, 1f, 1f, rot);
        }
        if (ledRegion != null) {
            float t = (float) (System.nanoTime() * 1e-9);
            float pulse = 0.30f + 0.70f * (0.5f + 0.5f * (float) Math.sin(t * ledSpeed + ledPhase));
            batch.setColor(ledR, ledG, ledB, pulse);
            batch.draw(ledRegion, ledX - ledSize / 2f, ledY - ledSize / 2f, ledSize, ledSize);
        }
        batch.setPackedColor(prev);
    }
}
