package com.vesas.spacefly.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vesas.spacefly.SpaceflyGame;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Settings;

/**
 * Settings menu. Currently exposes the master sound volume.
 *
 * Reachable from the main menu and from the in-game pause menu; {@link Origin}
 * decides which screen the Back action returns to.
 */
public class SettingsScreen implements Screen {

    /** Where the Back action should return to. */
    public enum Origin { MAIN_MENU, PAUSE }

    private static final float VOLUME_STEP = 0.05f;

    private final SpaceflyGame game;
    private final Origin origin;

    private Stage stage;
    private Texture whiteTex;
    private Label percentLabel;

    public SettingsScreen(SpaceflyGame game, Origin origin) {
        this.game = game;
        this.origin = origin;
        init();
    }

    private void init() {
        whiteTex = createWhiteTexture();

        stage = new Stage(new ScreenViewport()) {
            @Override
            public boolean keyDown(int keyCode) {
                switch (keyCode) {
                    case Keys.LEFT:  adjustVolume(-VOLUME_STEP); return true;
                    case Keys.RIGHT: adjustVolume(VOLUME_STEP);  return true;
                    case Keys.ENTER:
                    case Keys.ESCAPE:
                        goBack();
                        return true;
                }
                return super.keyDown(keyCode);
            }
        };

        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("data/menuskin.json"));

        int screenWidth = stage.getViewport().getScreenWidth();
        float scale = 0.31f * screenWidth / 640.0f;

        Table table = new Table(skin);
        table.pad(110);
        table.setFillParent(true);
        stage.addActor(table);

        Label title = new Label("Settings", skin);
        title.setAlignment(Align.center);
        title.setFontScale(1.2f * scale);
        table.row();
        table.add(title).colspan(2).align(Align.center).padBottom(60 * scale);

        Label volumeLabel = new Label("Master volume", skin);
        volumeLabel.setFontScale(0.7f * scale);
        table.row();
        table.add(volumeLabel).align(Align.right).padRight(30 * scale);

        VolumeBar bar = new VolumeBar();
        table.add(bar)
            .width(340 * scale)
            .height(28 * scale)
            .align(Align.left);

        percentLabel = new Label(percentText(), skin);
        percentLabel.setFontScale(0.7f * scale);
        table.row();
        table.add(percentLabel).colspan(2).align(Align.center).padTop(20 * scale);

        Label hint = new Label("Left / Right to adjust      Enter or Esc to go back", skin);
        hint.setFontScale(0.4f * scale);
        table.row();
        table.add(hint).colspan(2).align(Align.center).padTop(70 * scale);
    }

    private static Texture createWhiteTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    private void adjustVolume(float delta) {
        Settings.setMasterVolume(Settings.getMasterVolume() + delta);
        percentLabel.setText(percentText());
        // Play a short blip so the new level is immediately audible.
        G.playSound(G.shot, 0.35f, 1.0f, 0f);
    }

    private void setVolumeFromFraction(float fraction) {
        Settings.setMasterVolume(fraction);
        percentLabel.setText(percentText());
        G.playSound(G.shot, 0.35f, 1.0f, 0f);
    }

    private String percentText() {
        return Math.round(Settings.getMasterVolume() * 100) + "%";
    }

    private void goBack() {
        Screen next = (origin == Origin.PAUSE) ? new EscScreen(game) : new MainMenuScreen(game);
        game.setScreen(next);
        dispose();
    }

    /** A click/drag-adjustable horizontal fill bar for the master volume. */
    private class VolumeBar extends Actor {

        private final Color track = new Color(0.22f, 0.22f, 0.28f, 1f);
        private final Color fill = new Color(0.40f, 0.85f, 1.0f, 1f);

        VolumeBar() {
            addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    setVolumeFromFraction(MathUtils.clamp(x / getWidth(), 0f, 1f));
                    return true;
                }

                @Override
                public void touchDragged(InputEvent event, float x, float y, int pointer) {
                    setVolumeFromFraction(MathUtils.clamp(x / getWidth(), 0f, 1f));
                }
            });
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            float x = getX();
            float y = getY();
            float w = getWidth();
            float h = getHeight();

            Color prev = batch.getColor();

            batch.setColor(track);
            batch.draw(whiteTex, x, y, w, h);

            batch.setColor(fill);
            batch.draw(whiteTex, x, y, w * Settings.getMasterVolume(), h);

            batch.setColor(prev);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void show() { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        whiteTex.dispose();
    }
}
