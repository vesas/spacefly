package com.vesas.spacefly.game;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class G
{
	public ShapeRenderer renderer = new ShapeRenderer();

	public static BitmapFont font = new BitmapFont();
	public static BitmapFont worldFont = new BitmapFont();
	
	public static BitmapFont wFont;
	
	public static float GRID_W = 3*32f;
	public static float GRID_H = 2*32f;
	
	public static float SCREEN_W = 12*3*32f;
	public static float SCREEN_H = 8*3*32f;
	
	public static Viewport mainViewport;
	
	static public TextureAtlas atlas;
	
	// Monster types
	public enum MonsterType {
		BASIC("monster1"),
		BASIC_HALO("monster1_halo"),
		ZIPPER("zippermonster"),
		SHELL("shellmonster.1"),
		SHOOT_STICK("shoot_stick");
	
		private final String textureName;
		MonsterType(String textureName) { this.textureName = textureName; }
	}

	static public Map<MonsterType, Sprite> monsters = new EnumMap<>(MonsterType.class);

	// Power up types
	public enum PowerUpType {
		HEAL("powerup_heal"),
		AMMO("powerup_ammo1"),
		SPICE("spice"),
		SHOOT("powerup_shoot1"),
		HEALTH("powerup_health1");
	
		private final String textureName;
		PowerUpType(String textureName) { this.textureName = textureName; }
	}

	static public Map<PowerUpType, Sprite> powerUps = new EnumMap<>(PowerUpType.class);

	// Bullet types
	public enum BulletType {
		PLAYER("bullet"),
		MONSTER("monsterbullet"),
		MONSTER2("monsterbullet2");

		private final String textureName;
		BulletType(String textureName) { this.textureName = textureName; }
	}

	static public Map<BulletType, Sprite> bullets = new EnumMap<>(BulletType.class);
	
	static public Sprite [] effects;
	
	static public Sprite [] cursors;
	
	static public Sprite [] health;
	
	static public Sprite [] props;

	static public TextureRegion [] walls;
	
	static public Sound shot;
	static public Sound explo1;
	
	public static ShapeRenderer shapeRenderer;
	
	static public TextureAtlas getAtlas()
	{
		return atlas;
	}
	
	static public void loadTextures()
	{
		atlas = new TextureAtlas(Gdx.files.local("data/fly.atlas"));
		
		for (MonsterType type : MonsterType.values()) {
			monsters.put(type, atlas.createSprite(type.textureName));
		}

		for (PowerUpType type : PowerUpType.values()) {
			powerUps.put(type, atlas.createSprite(type.textureName));
		}

		for(BulletType type : BulletType.values()) {
			bullets.put(type, atlas.createSprite(type.textureName));
		}
		
		health = new Sprite[2];
		health[0] = G.getAtlas().createSprite("health_on");
		health[1] = G.getAtlas().createSprite("health_off");
		
		
		
		effects = new Sprite[5];
		effects[0] = G.getAtlas().createSprite("smallpuff");
//		effects[1] = G.getAtlas().createSprite("exploring1");
		effects[1] = G.getAtlas().createSprite("smallpuff");
//		effects[2] = G.getAtlas().createSprite("whitedisc");
		effects[2] = G.getAtlas().createSprite("smallpuff");
		effects[3] = G.getAtlas().createSprite("star");
		effects[4] = G.getAtlas().createSprite("portal1");

		cursors = new Sprite[1];
		cursors[0] = G.getAtlas().createSprite("crosshair1");
		
		props = new Sprite[3];
		props[0] = G.getAtlas().createSprite("pipe_ew");
		props[1] = G.getAtlas().createSprite("pipe_bend_nw");
		props[2] = G.getAtlas().createSprite("tile64");
		
		wFont = new BitmapFont(Gdx.files.internal("data/font_big.fnt"),
				Gdx.files.internal("data/font_big.png"), false, false );
		
		// worldFont.getData().scale( 0.05f );
		
		shot = Gdx.audio.newSound( Gdx.files.local("data/shot.ogg"));
		explo1 = Gdx.audio.newSound( Gdx.files.local("data/explo1.ogg"));
		
		walls = new TextureRegion[2];
		walls[0] = G.getAtlas().findRegion("edgeA1");
		walls[1] = G.getAtlas().findRegion("edgeA10");
	}
	
	static public void disposeTextures()
	{
		atlas.dispose();
	}
	
	
	
}
