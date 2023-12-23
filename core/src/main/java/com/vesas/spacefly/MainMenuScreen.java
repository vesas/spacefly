package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Table table;

    private SpaceflyGame game;

    public MainMenuScreen(SpaceflyGame game) {
		super();
		this.game = game;

        init();
	}

    private void init() {

        stage = new Stage(new ScreenViewport()) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Keys.SPACE) {
                    Gdx.app.exit();
                }
                return super.keyDown(keyCode);
            }
        };
        
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);
		table.setDebug(true);
		stage.addActor(table);

		Label testLabel = new Label("TEST TEST", VisUI.getSkin());
		table.add(testLabel).expand().fill().center();
    }

    @Override
    public void show() {
        
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

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
    
}
