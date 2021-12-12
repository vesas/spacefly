package com.vesas.spacefly.game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class G
{
	public ShapeRenderer renderer = new ShapeRenderer();

	public static BitmapFont font = new BitmapFont();
	public static BitmapFont worldFont = new BitmapFont();
	
	public static BitmapFont wFont;
	
	public static Random random = new Random();
	
	public static float GRID_W = 3*32f;
	public static float GRID_H = 2*32f;
	
	public static float SCREEN_W = 12*3*32f;
	public static float SCREEN_H = 8*3*32f;
	
	public static Viewport mainViewport;
	
	static public TextureAtlas atlas;
	
	static public Sprite [] monsters;
	static public Sprite [] bullets;
	
	static public Sprite [] effects;
	
	static public Sprite [] spice;
	
	static public Sprite [] cursors;
	
	static public Sprite [] health;
	
	static public Sprite [] props;
	
	static public Sound shot;
	static public Sound explo1;
	
	public static ShapeRenderer shapeRenderer;
	
	static public TextureAtlas getAtlas()
	{
		return atlas;
	}
	
	static public float floatLerp( float start, float end, float pos )
	{
		return (start + pos*(end - start));
	}
	
	static public void loadTextures()
	{
		atlas = new TextureAtlas(Gdx.files.local("data/fly.atlas"));
		
		monsters = new Sprite[4];
		
		monsters[0] = G.getAtlas().createSprite("monster1");
		monsters[1] = G.getAtlas().createSprite("zippermonster");
		monsters[2] = G.getAtlas().createSprite("shellmonster.1");
		monsters[3] = G.getAtlas().createSprite("monster1");
		
		spice = new Sprite[5];
		
		spice[0] = G.getAtlas().createSprite("powerup_heal");
		spice[1] = G.getAtlas().createSprite("powerup_ammo1");
		spice[2] = G.getAtlas().createSprite("spice");
		spice[3] = G.getAtlas().createSprite("powerup_shoot1");
		spice[4] = G.getAtlas().createSprite("powerup_health1");
		
		health = new Sprite[2];
		health[0] = G.getAtlas().createSprite("health_on");
		health[1] = G.getAtlas().createSprite("health_off");
		
		bullets = new Sprite[3];
		
		bullets[0] = G.getAtlas().createSprite("bullet");
		bullets[1] = G.getAtlas().createSprite("monsterbullet");
		bullets[2] = G.getAtlas().createSprite("monsterbullet2");
		
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
		
		props = new Sprite[2];
		props[0] = G.getAtlas().createSprite("pipe_ew");
		props[1] = G.getAtlas().createSprite("pipe_bend_nw");
		
		wFont = new BitmapFont(Gdx.files.internal("data/font_big.fnt"),
				Gdx.files.internal("data/font_big.png"), false, false );
		
		// worldFont.getData().scale( 0.05f );
		
		shot = Gdx.audio.newSound( Gdx.files.local("data/shot.ogg"));
		explo1 = Gdx.audio.newSound( Gdx.files.local("data/explo1.ogg"));
	}
	
	static public void disposeTextures()
	{
		atlas.dispose();
	}
	
	
	
}
