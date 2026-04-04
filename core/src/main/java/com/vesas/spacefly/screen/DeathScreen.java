package com.vesas.spacefly.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vesas.spacefly.SpaceflyGame;

public class DeathScreen implements Screen {

    private final SpaceflyGame game;
    private final int floorReached;
    private Stage stage;

    public DeathScreen(SpaceflyGame game, int floorReached) {
        this.game = game;
        this.floorReached = floorReached;
        init();
    }

    private void init() {
        stage = new Stage(new ScreenViewport()) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Keys.ENTER || keyCode == Keys.SPACE) {
                    game.restartGame();
                    return true;
                }
                if (keyCode == Keys.ESCAPE) {
                    Gdx.app.exit();
                    return true;
                }
                return super.keyDown(keyCode);
            }
        };

        Gdx.input.setInputProcessor(stage);

        Skin skin = new Skin(Gdx.files.internal("data/menuskin.json"));

        Table table = new Table(skin);
        table.setFillParent(true);
        stage.addActor(table);

        int screenWidth = stage.getViewport().getScreenWidth();
        float scale = 0.31f * screenWidth / 640.0f;

        MainMenuLabel titleLabel = new MainMenuLabel("YOU DIED", skin, new Color(0.9f, 0.15f, 0.15f, 1.0f));
        titleLabel.setAlignment(Align.center);
        titleLabel.setFontScale(1.6f * scale);
        table.row();
        table.add(titleLabel).align(Align.center).expandY().fill().center();

        MainMenuLabel floorLabel = new MainMenuLabel("Floor reached: " + floorReached, skin, new Color(0.8f, 0.8f, 0.8f, 0.9f));
        floorLabel.setAlignment(Align.center);
        floorLabel.setFontScale(0.9f * scale);
        table.row();
        table.add(floorLabel).align(Align.center).expandY().fill().center();

        MainMenuLabel restartLabel = new MainMenuLabel("Press ENTER to restart", skin, new Color(0.75f, 0.75f, 0.75f, 0.85f));
        restartLabel.setAlignment(Align.center);
        restartLabel.setFontScale(0.7f * scale);
        table.row();
        table.add(restartLabel).align(Align.center).expandY().fill().center();

        MainMenuLabel quitLabel = new MainMenuLabel("Press ESC to quit", skin, new Color(0.5f, 0.5f, 0.5f, 0.75f));
        quitLabel.setAlignment(Align.center);
        quitLabel.setFontScale(0.6f * scale);
        table.row();
        table.add(quitLabel).align(Align.center).expandY().fill().center();
    }

    @Override
    public void render(float delta) {
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
    @Override public void dispose() { stage.dispose(); }
}
