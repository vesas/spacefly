package com.vesas.spacefly.game.floater;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Util;
import com.vesas.spacefly.screen.GameScreen;

public final class Floater
{
	// G.font is the default 15px BitmapFont; drawn into the world batch (world units) at
	// scale 1 a glyph would be ~15 world units tall. Shrink it to a readable in-world size
	// (~0.7 world units, about half a monster's height).
	private static final float FONT_SCALE = 0.025f;

	//position
	private float x;
	private float y;

	private float lifetime;
	private float maxLifetime;

	private boolean active = true;

	private String displayString;

	private float r = 1f, g = 1f, b = 1f;
	private float scaleMult = 1f;

	public void setString( String val ) {
		displayString = val;
	}

	public void setColor( float r, float g, float b ) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public void setScaleMult( float s ) {
		scaleMult = s;
	}

	public void setLife( float val ) {
		lifetime = val;
	}

	public void setMaxLife( float val ) {
		maxLifetime = val;
	}

	public void setPos( Vector2 pos ) {
		x = pos.x;
		y = pos.y;

	}

	public void setPos( float px, float py ) {
		x = px;
		y = py;
	}

	public void init() {
		active = true;
		lifetime = 0.0f;

		maxLifetime = 1.1f;

		r = 1f;
		g = 1f;
		b = 1f;
		scaleMult = 1f;
	}

	public boolean isActive() {
		return active;
	}

	public void tick( float delta ) {
		lifetime += delta;

		if( lifetime > maxLifetime ) {
			active = false;
		}

		// Rise as it fades — the classic damage-number pop.
		y += delta * 0.9f;
	}

	private static Color tmpColor = new Color();

	public void render( GameScreen screen ) {
		if (!active)
			return;

		float pos = lifetime / maxLifetime;
		float tempA = Util.floatLerp(maxLifetime, 0, pos) / maxLifetime;

		float oldScaleX = G.font.getData().scaleX;
		float oldScaleY = G.font.getData().scaleY;
		boolean oldIntPos = G.font.usesIntegerPositions();
		tmpColor.set(G.font.getColor());

		// G.font defaults to integer positions, which rounds every glyph to the nearest WORLD
		// unit here — collapsing multi-digit numbers into one overlapping blob. Turn it off.
		G.font.setUseIntegerPositions(false);
		G.font.getData().setScale(FONT_SCALE * scaleMult, FONT_SCALE * scaleMult);

		// Dark drop-shadow first, so the number stays legible over the bright yellow-orange
		// explosion as well as over dark floor.
		float shadow = 0.05f;
		G.font.setColor(0f, 0f, 0f, tempA * 0.8f);
		G.font.draw(screen.worldBatch, displayString, x + shadow, y - shadow);

		G.font.setColor(r, g, b, tempA);
		G.font.draw(screen.worldBatch, displayString, x, y);

		G.font.setColor(tmpColor);
		G.font.getData().setScale(oldScaleX, oldScaleY);
		G.font.setUseIntegerPositions(oldIntPos);
	}
}
