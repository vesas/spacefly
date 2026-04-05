package com.vesas.spacefly.game;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.screen.GameScreen;

public class LevelExit {

    private final float x;
    private final float y;
    private final float radius = 0.8f;
    private float animTimer = 0;

    public LevelExit(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isPlayerInRange(Vector2 playerPos) {
        float dx = playerPos.x - x;
        float dy = playerPos.y - y;
        return dx * dx + dy * dy < radius * radius;
    }

    public void tick(float delta) {
        animTimer += delta;
    }

    public void draw(GameScreen screen) {
        float pulse     = 0.7f + 0.3f * (float) Math.sin(animTimer * 3.0f);
        float fastPulse = 0.6f + 0.4f * (float) Math.sin(animTimer * 5.5f);
        float rot       = animTimer * 40f;

        Sprite portal = G.effects[4]; // portal1
        float size = radius * 1.8f;

        screen.worldBatch.begin();
        screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        // Outer corona — huge, very faint, slow counter-rotation
        drawPortalLayer(screen, portal, size * 2.5f, 0.0f, 0.88f, 0.68f, 0.07f * pulse,    -rot * 0.35f);

        // Mid glow ring
        drawPortalLayer(screen, portal, size * 1.55f, 0.0f, 0.75f, 1.0f,  0.22f * fastPulse, rot * 0.55f);

        // Core bright ring — main rotation
        drawPortalLayer(screen, portal, size,          0.1f, 1.0f,  0.82f, 0.80f * pulse,    rot);

        // Hot inner centre — fastest counter-spin
        drawPortalLayer(screen, portal, size * 0.58f,  0.65f, 1.0f, 1.0f, 0.95f,            -rot * 1.9f);

        screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        screen.worldBatch.end();
    }

    private void drawPortalLayer(GameScreen screen, Sprite portal,
                                 float size, float r, float g, float b, float a, float rotation) {
        portal.setSize(size, size);
        portal.setOriginCenter();
        portal.setColor(r, g, b, a);
        portal.setRotation(rotation);
        portal.setPosition(x - portal.getWidth() * 0.5f, y - portal.getHeight() * 0.5f);
        portal.draw(screen.worldBatch);
    }

    public void drawMiniMap() {
        G.shapeRenderer.setColor(0.0f, 1.0f, 0.3f, 1.0f);
        G.shapeRenderer.circle(x, y, 1.5f, 8);
    }
}
