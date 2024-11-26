package com.vesas.spacefly;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private Stage stage;
    private Table table;

    private ShaderProgram shader;
    private Mesh fullscreenQuad;
    private float time = 0;

    private SpaceflyGame game;

    private static final int MENU_ITEMS = 3;
    private static final String[] MENU_LABELS = {"New game", "Settings", "Exit"};
    private MainMenuLabel[] menuItems;

    private int currentSelectedMenu = 0;

    public MainMenuScreen(SpaceflyGame game) {
		super();
		this.game = game;

        init();
	}

    private void init() {
        setupInputProcessor();
        createUI();
        setupShader();  // Add this line
    }

    private void setupShader() {
        // Create shader
        shader = new ShaderProgram(
            Gdx.files.internal("shaders/default.vert").readString(),
            Gdx.files.internal("shaders/background.frag").readString()
        );
        
        // Create fullscreen quad
        fullscreenQuad = new Mesh(
            true, 4, 6,
            new VertexAttribute(Usage.Position, 2, "a_position")
        );
        
        fullscreenQuad.setVertices(new float[] {
            -1, -1,
            1, -1,
            1, 1,
            -1, 1
        });
        
        fullscreenQuad.setIndices(new short[] {0, 1, 2, 2, 3, 0});
    }

    private void setupInputProcessor() {

        stage = new Stage(new ScreenViewport()) {
            @Override
            public boolean keyDown(int keyCode) {
                switch(keyCode) {
                    case Keys.ENTER: handleMenuSelection(); break;
                    case Keys.DOWN: updateSelectedMenu(1); break;
                    case Keys.UP: updateSelectedMenu(-1); break;
                    case Keys.SPACE: Gdx.app.exit(); break;
                }
                updateMenuVisuals();
                return super.keyDown(keyCode);
            }

            @Override 
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Actor hitActor = getHitActor(screenX, screenY);
                for (int i = 0; i < menuItems.length; i++) {
                    if (hitActor == menuItems[i]) {
                        currentSelectedMenu = i;
                        handleMenuSelection();
                        return true;
                    }
                }
                return false;
            }

            @Override 
            public boolean mouseMoved(int screenX, int screenY) {
                Actor hitActor = getHitActor(screenX, screenY);
                for (int i = 0; i < menuItems.length; i++) {
                    if (hitActor == menuItems[i]) {
                        currentSelectedMenu = i;
                        updateMenuVisuals();
                        return true;
                    }
                }
                return false;
            }
        };

    }

    private void handleMenuSelection() {
        switch(currentSelectedMenu) {
            case 0: game.setGameScreen(); break;
            case 1: /* game.setScreen(new SettingsScreen(game)); */ break;
            case 2: Gdx.app.exit(); break;
        }
    }

    private void updateSelectedMenu(int direction) {
        currentSelectedMenu = (currentSelectedMenu + direction + MENU_ITEMS) % MENU_ITEMS;
    }

    private void updateMenuVisuals() {
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i].setActive(i == currentSelectedMenu);
        }
    }

    private void createUI() {
        Skin skin = new Skin(Gdx.files.internal("data/menuskin.json"));
        setupTable(skin);
        createTitle(skin);
        createMenuItems(skin);
        
        Gdx.input.setInputProcessor(stage);
    }

    private void setupTable(Skin skin) {
        table = new Table(skin);
        table.pad(110);
        table.setFillParent(true);
        stage.addActor(table);
    }

    private void createTitle(Skin skin) {
        Label testLabel = new Label("Spacefly", skin);
        testLabel.setAlignment(Align.center);
        testLabel.setFontScale(calculateScale());
        table.row();
		table.add(testLabel).align(Align.center).expandY().fill().center();
    }

    private void createMenuItems(Skin skin) {
        menuItems = new MainMenuLabel[MENU_ITEMS];
        float scale = calculateScale();
        
        for (int i = 0; i < MENU_ITEMS; i++) {
            table.row();
            menuItems[i] = new MainMenuLabel(MENU_LABELS[i], skin, new Color(0,0,0,0));
            menuItems[i].setAlignment(Align.center);
            menuItems[i].setFontScale(scale);
            table.add(menuItems[i])
                .align(Align.center)
                .expandY().fill().center()
                .padLeft(50).padRight(50)
                .padBottom(10)  // Added vertical spacing between items
                .minWidth(300)  // Added minimum width
                .expand(0, 1);
        }
        
        table.row();
        table.add().align(Align.center).expandY().fill().center();
        menuItems[0].setActive(true);
    }

    private Actor getHitActor(int screenX, int screenY) {
        Vector2 mouseScreenPosition = new Vector2(screenX, screenY);
        Vector2 mouseLocalPosition = stage.screenToStageCoordinates(mouseScreenPosition);
        return stage.hit(mouseLocalPosition.x, mouseLocalPosition.y, false);
    }

    private float calculateScale() {
        int screenWidth = stage.getViewport().getScreenWidth();
        return 0.35f * screenWidth / 640.0f;
    }

    @Override
    public void show() {
        
    }

    @Override
    public void render(float delta) {
        time += delta;
        
        // Render background
        shader.bind();
        shader.setUniformf("u_time", time);
        shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fullscreenQuad.render(shader, GL20.GL_TRIANGLES);
        
        // Render UI
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
        shader.dispose();
        fullscreenQuad.dispose();
        stage.dispose();
    }
    
}
