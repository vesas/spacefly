package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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

    private MainMenuLabel menu0;
    private MainMenuLabel menu1;
    private MainMenuLabel menu2;

    private int currentSelectedMenu = 0;

    public MainMenuScreen(SpaceflyGame game) {
		super();
		this.game = game;

        init();
	}

    private void init() {

        stage = new Stage(new ScreenViewport()) {
            @Override
            public boolean keyDown(int keyCode) {
                if (keyCode == Keys.DOWN) {
                    currentSelectedMenu++;
                    if(currentSelectedMenu > 2) {
                        currentSelectedMenu = 0;
                    }

                    System.out.println("currentSelectedMenu: " + currentSelectedMenu);
                }
                if (keyCode == Keys.UP) {
                    currentSelectedMenu--;
                    if(currentSelectedMenu < 0) {
                        currentSelectedMenu = 2;
                    }
                    System.out.println("currentSelectedMenu: " + currentSelectedMenu);
                }
                if (keyCode == Keys.SPACE) {
                    Gdx.app.exit();
                }

                if(currentSelectedMenu == 0) {
                    menu0.setActive(true);
                    menu1.setActive(false);
                    menu2.setActive(false);
                }
                else if(currentSelectedMenu == 1) {
                    menu0.setActive(false);
                    menu1.setActive(true);
                    menu2.setActive(false);
                }
                else if(currentSelectedMenu == 2) {
                    menu0.setActive(false);
                    menu1.setActive(false);
                    menu2.setActive(true);
                }

                return super.keyDown(keyCode);
            }

            @Override public boolean mouseMoved (int screenX, int screenY) {
                // we can also handle mouse movement without anything pressed
                Vector2 mouseScreenPosition = new Vector2(Gdx.input.getX(), Gdx.input.getY());
                
                Vector2 mouseLocalPosition = stage.screenToStageCoordinates(mouseScreenPosition);

                Actor hitActor = stage.hit(mouseLocalPosition.x, mouseLocalPosition.y, false);
                if(hitActor == menu0) {
                    menu0.setActive(true);
                    menu1.setActive(false);
                    menu2.setActive(false);
                }
                if(hitActor == menu1) {
                    menu0.setActive(false);
                    menu1.setActive(true);
                    menu2.setActive(false);
                }
                if(hitActor == menu2) {
                    menu0.setActive(false);
                    menu1.setActive(false);
                    menu2.setActive(true);
                }
                
                return false;
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
        menu0 = new MainMenuLabel("New game", skin);
        menu0.setActive(true);
        menu0.setAlignment(Align.center);
        menu0.setFontScale(scale);
		table.add(menu0).align(Align.center).expandY().fill().center();

        table.row();
        menu1 = new MainMenuLabel("Settings", skin);
        menu1.setAlignment(Align.center);
        menu1.setFontScale(scale);
		table.add(menu1).align(Align.center).expandY().fill().center();

        table.row();
        menu2 = new MainMenuLabel("Exit", skin);
        menu2.setAlignment(Align.center);
        menu2.setFontScale(scale);
        // menu2.setDebug(true);
		table.add(menu2).align(Align.center).expandY().fill().center();
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
