package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

        Skin skin = new Skin(Gdx.files.internal("data/menuskin.json"));

        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.RED);
        bgPixmap.fill();

        TextureRegionDrawable inactiveBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

		Gdx.input.setInputProcessor(stage);

        table = new Table(skin);
		table.setFillParent(true);
		// table.setDebug(true);
		stage.addActor(table);

        int screenWidth = stage.getViewport().getScreenWidth();
        float scale = 0.35f * screenWidth / 640.0f;

		Label testLabel = new Label("Spacefly", skin);
        testLabel.setAlignment(Align.center);
        testLabel.setFontScale(1.5f * scale);
        table.row();
		table.add(testLabel).align(Align.center).expandY().fill().center();

        table.row();
        Label testLabel2 = new Label("New game", skin);
        
        testLabel2.setAlignment(Align.center);
        testLabel2.setFontScale(scale);
		table.add(testLabel2).align(Align.center).expandY().fill().center();

        table.row();
        Label testLabel3 = new Label("Settings", skin);
        testLabel3.setAlignment(Align.center);
        testLabel3.setFontScale(scale);
		table.add(testLabel3).align(Align.center).expandY().fill().center();

        table.row();
        Label testLabel4 = new Label("Exit", skin);
        testLabel4.setAlignment(Align.center);
        testLabel4.setFontScale(scale);
		table.add(testLabel4).align(Align.center).expandY().fill().center();
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
