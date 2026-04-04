package com.vesas.spacefly.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.vesas.spacefly.SpaceflyGame;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.world.AbstractGameWorld;

public class Hud {

	/** Reused each frame to measure name text width without allocating. */
	private final GlyphLayout nameLayout = new GlyphLayout();

	private static final float NAME_SCALE  = 0.45f;
	private static final float LABEL_SCALE = 0.32f;
	private static final float HUD_SCALE   = 0.45f;

	private static final float BAR_W      = 14f;
	private static final float BAR_H      =  9f;
	private static final float BAR_GAP    =  3f;
	/** Total pixel width of one bar row: 5 bars + 4 gaps. */
	private static final float BARS_ROW_W = 5 * BAR_W + 4 * BAR_GAP;

	// Gaps used in layout calculations — all in screen pixels
	private static final float LINE_GAP    =  8f;  // between label and value in a two-line block
	private static final float BAR_ABOVE   =  5f;  // gap between bar top and label text bottom
	private static final float ROW_PAD     =  8f;  // extra space between stat rows
	private static final float MARGIN_EDGE = 15f;  // margin from screen edges

	public void draw(final GameScreen screen) {
		final int width  = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();
		final Weapon     currentWeapon = Player.INSTANCE.getCurrentWeapon();
		final WeaponDrop nearbyDrop    = Player.INSTANCE.getNearbyWeaponDrop();

		// ── Measure font metrics at each scale we use ──────────────────────
		G.wFont.getData().setScale(HUD_SCALE, HUD_SCALE);
		final float hudCapH = G.wFont.getCapHeight();   // height of a capital letter

		G.wFont.getData().setScale(LABEL_SCALE, LABEL_SCALE);
		final float lblCapH = G.wFont.getCapHeight();

		// One stat row = label text + gap + bar + padding below
		final float rowStep = lblCapH + BAR_ABOVE + BAR_H + ROW_PAD;

		// ── Compute weapon-block y positions (bottom-right, built upward) ──
		// Row 0 = DMG (top), row 3 = SHT (bottom)
		final float bottomMargin = MARGIN_EDGE;
		final float[] barY   = new float[4];
		final float[] labelY = new float[4];  // TOP of label text (LibGDX y-up)
		for (int i = 0; i < 4; i++) {
			// row 3 is at the bottom, row 0 at the top
			barY[i]   = bottomMargin + (3 - i) * rowStep;
			labelY[i] = barY[i] + BAR_H + BAR_ABOVE + lblCapH;
		}
		// Weapon name sits above row 0 with a small gap
		final float nameY = labelY[0] + LINE_GAP + hudCapH;

		// Anchor bars to the right edge so nothing overflows.
		// Labels ("DMG" etc.) sit to the left of the bars.
		final float weaponBarX   = width - MARGIN_EDGE - BARS_ROW_W;
		final float weaponLabelX = weaponBarX - 55f;

		// ── Text pass ──────────────────────────────────────────────────────
		G.wFont.getData().setScale(HUD_SCALE, HUD_SCALE);

		// Health — top-left
		final float topY = height - MARGIN_EDGE;
		G.wFont.draw(screen.screenBatch, "Health",
				50f, topY);
		G.wFont.draw(screen.screenBatch,
				Player.INSTANCE.getHealth() + "/" + Player.INSTANCE.getHealthMax(),
				50f, topY - hudCapH - LINE_GAP);

		// Ammo — top-left, below health  (avoids top-right minimap)
		final float ammoLabelY = topY - 2f * hudCapH - 3f * LINE_GAP;
		G.wFont.draw(screen.screenBatch, "Ammo", 50f, ammoLabelY);
		G.wFont.draw(screen.screenBatch,
				Player.INSTANCE.getAmmo() + "/" + Player.INSTANCE.getAmmoMax(),
				50f, ammoLabelY - hudCapH - LINE_GAP);

		// Floor — top-centre
		G.wFont.draw(screen.screenBatch,
				"Floor " + SpaceflyGame.displayFloor,
				width * 0.5f - 40f, topY);

		// Weapon name — right-aligned to bar block right edge so long names don't overflow
		G.wFont.getData().setScale(NAME_SCALE, NAME_SCALE);
		nameLayout.setText(G.wFont, currentWeapon.name);
		float nameX = width - MARGIN_EDGE - nameLayout.width;
		G.wFont.draw(screen.screenBatch, currentWeapon.name, nameX, nameY);

		// Stat row labels
		G.wFont.getData().setScale(LABEL_SCALE, LABEL_SCALE);
		final String[] STAT_NAMES = { "DMG", "SPD", "RNG", "SHT" };
		for (int i = 0; i < 4; i++) {
			G.wFont.draw(screen.screenBatch, STAT_NAMES[i], weaponLabelX, labelY[i]);
		}

		// Comparison text (only when near a drop)
		if (nearbyDrop != null) {
			drawComparisonText(screen, width, height, currentWeapon, nearbyDrop.getWeapon(),
					hudCapH, lblCapH, rowStep);
		}

		screen.screenBatch.end();

		// ── Shape pass ─────────────────────────────────────────────────────
		G.shapeRenderer.setProjectionMatrix(screen.screenCamera.combined);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		// Current weapon bars
		int[] curBars = bars(currentWeapon);
		G.shapeRenderer.begin(ShapeType.Filled);
		for (int i = 0; i < 4; i++) {
			drawBarRow(curBars[i], weaponBarX, barY[i], 1.0f, 1.0f, 1.0f);
		}
		G.shapeRenderer.end();

		// Comparison panel
		if (nearbyDrop != null) {
			drawComparisonPanel(width, height, currentWeapon, nearbyDrop.getWeapon(),
					hudCapH, lblCapH, rowStep);
		}

		Minimap.draw(screen);
		screen.viewport.apply(false);
	}

	// ── Comparison ─────────────────────────────────────────────────────────

	private void drawComparisonText(GameScreen screen, int width, int height,
			Weapon current, Weapon drop,
			float hudCapH, float lblCapH, float rowStep) {

		float cx = width * 0.5f;

		// panel bottom is 20px above weapon block top; panel grows upward
		float panelBottom = MARGIN_EDGE + 4 * rowStep + hudCapH + LINE_GAP * 2 + 20f;
		float namesY  = panelBottom + 4 * rowStep + lblCapH + BAR_ABOVE + BAR_H + LINE_GAP + hudCapH;
		float swapY   = panelBottom + LINE_GAP;

		G.wFont.getData().setScale(0.38f, 0.38f);
		float nameCapH = G.wFont.getCapHeight();
		G.wFont.draw(screen.screenBatch, current.name, cx - 165f, namesY);
		G.wFont.draw(screen.screenBatch, drop.name,    cx +  15f, namesY);

		G.wFont.getData().setScale(LABEL_SCALE, LABEL_SCALE);
		float lx = cx - 200f;
		for (int i = 0; i < 4; i++) {
			float ly = panelBottom + (3 - i) * rowStep + BAR_H + BAR_ABOVE + lblCapH;
			G.wFont.draw(screen.screenBatch, new String[]{"DMG","SPD","RNG","SHT"}[i], lx, ly);
		}

		G.wFont.getData().setScale(0.40f, 0.40f);
		G.wFont.draw(screen.screenBatch, "[ E ] swap", cx - 38f, swapY);

		G.wFont.getData().setScale(HUD_SCALE, HUD_SCALE); // restore
	}

	private void drawComparisonPanel(int width, int height,
			Weapon current, Weapon drop,
			float hudCapH, float lblCapH, float rowStep) {

		float cx = width * 0.5f;
		float panelBottom = MARGIN_EDGE + 4 * rowStep + hudCapH + LINE_GAP * 2 + 20f;
		float panelHeight = 4 * rowStep + BAR_H + BAR_ABOVE + lblCapH
				+ hudCapH + LINE_GAP * 3 + 20f;

		int[] curBars  = bars(current);
		int[] dropBars = bars(drop);

		float curX  = cx - 162f;
		float dropX = cx +  18f;

		G.shapeRenderer.begin(ShapeType.Filled);

		// background
		G.shapeRenderer.setColor(0.05f, 0.05f, 0.12f, 0.88f);
		G.shapeRenderer.rect(cx - 210f, panelBottom - 8f, 420f, panelHeight);

		for (int i = 0; i < 4; i++) {
			float y = panelBottom + (3 - i) * rowStep;

			drawBarRow(curBars[i], curX, y, 0.70f, 0.70f, 0.70f);

			float r, g, b;
			if      (dropBars[i] > curBars[i]) { r = 0.30f; g = 0.90f; b = 0.30f; }
			else if (dropBars[i] < curBars[i]) { r = 0.90f; g = 0.30f; b = 0.30f; }
			else                               { r = 0.70f; g = 0.70f; b = 0.70f; }
			drawBarRow(dropBars[i], dropX, y, r, g, b);
		}

		G.shapeRenderer.end();
	}

	// ── Helpers ────────────────────────────────────────────────────────────

	private static int[] bars(Weapon w) {
		return new int[]{ w.damageBars(), w.speedBars(), w.rangeBars(), w.shotBars() };
	}

	private void drawBarRow(int filled, float x, float y, float r, float g, float b) {
		for (int i = 0; i < 5; i++) {
			if (i < filled) {
				G.shapeRenderer.setColor(r, g, b, 1.0f);
			} else {
				G.shapeRenderer.setColor(0.12f, 0.12f, 0.16f, 0.90f);
			}
			G.shapeRenderer.rect(x + i * (BAR_W + BAR_GAP), y, BAR_W, BAR_H);
		}
	}

	// ── Minimap ─────────────────────────────────────────────────────────────

	private static class Minimap {

		private static final float MINIMAP_SIZE_PERCENT = 0.3f;
		private static final float BORDER_THICKNESS     = 3f;
		private static final float PADDING              = 20f;

		public static void draw(final GameScreen screen) {
			int width  = Gdx.graphics.getWidth();
			int height = Gdx.graphics.getHeight();

			int mapSize = (int)(height * MINIMAP_SIZE_PERCENT);
			int mapX    = width  - mapSize - (int)PADDING;
			int mapY    = height - mapSize - (int)PADDING;

			screen.minimapBatch.begin();
			screen.minimapBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			drawMinimapBorder(screen, mapX, mapY, mapSize);

			G.shapeRenderer.setProjectionMatrix(screen.minimapCamera.combined);
			Gdx.gl.glViewport(mapX, mapY, mapSize, mapSize);

			AbstractGameWorld.INSTANCE.drawMiniMap(screen);
			Player.INSTANCE.drawMiniMap(screen);

			screen.minimapBatch.end();
			screen.screenBatch.begin();
			G.shapeRenderer.setProjectionMatrix(screen.camera.combined);
		}

		private static void drawMinimapBorder(GameScreen screen, int x, int y, int size) {
			G.shapeRenderer.setProjectionMatrix(screen.screenCamera.combined);
			G.shapeRenderer.begin(ShapeType.Filled);
			G.shapeRenderer.setColor(1.0f, 1.0f, 1.0f, 1.0f);
			G.shapeRenderer.rect(x - BORDER_THICKNESS, y,        BORDER_THICKNESS, size);
			G.shapeRenderer.rect(x - BORDER_THICKNESS, y + size, size + BORDER_THICKNESS, BORDER_THICKNESS);
			G.shapeRenderer.rect(x + size,             y,        BORDER_THICKNESS, size);
			G.shapeRenderer.rect(x - BORDER_THICKNESS, y,        size + BORDER_THICKNESS, BORDER_THICKNESS);
			G.shapeRenderer.end();
		}
	}
}
