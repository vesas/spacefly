package com.vesas.spacefly.world.procedural;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vesas.spacefly.GameScreen;

public class TestFeature implements Feature {

    private Texture tex = null;
    
    private static Color col1 = new Color(0.24f, 0.25f, 0.33f, 1.0f);

    public TestFeature() {
    
        Pixmap pixmap = new Pixmap(4, 6, Pixmap.Format.RGBA8888 );
//		pixmap.setFilter(Filter.BiLinear);
		pixmap.setColor( Color.CLEAR );
		pixmap.fill();

		pixmap.setColor( col1 );
		pixmap.fillRectangle(0, 0, 4,6);

        tex = new Texture(pixmap);
		pixmap.dispose();
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void draw(SpriteBatch batch) {
    }

    @Override
    public void drawWithVisibility(GameScreen screen) {
        screen.worldBatch.draw( tex, 0, 0, 4, 6);
    }

    @Override
    public float getXpos() {
        return 0.0f;
    }

    @Override
    public float getYpos() {
        return 0.0f;
    }

    @Override
    public float getWidth() {
        return 4.0f;
    }

    @Override
    public float getHeight() {
        return 6.0f;
    }

    @Override
    public void tick(GameScreen screen, float delta) {
        // nothing
    }
}
