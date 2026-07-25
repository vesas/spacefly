package com.vesas.spacefly.world.procedural;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.AnimateEntity;
import com.vesas.spacefly.world.procedural.FloorTheme;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.LevelExit;
import com.vesas.spacefly.game.Player;
import com.vesas.spacefly.game.Weapon;
import com.vesas.spacefly.game.WeaponDrop;
import com.vesas.spacefly.monster.Monster;
import com.vesas.spacefly.monster.ZipperCloudManager;
import com.vesas.spacefly.monster.ZipperMonster;
import com.vesas.spacefly.particles.ExplosionInterface;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.util.DebugHelper;
import com.vesas.spacefly.util.FrameTime;
import com.vesas.spacefly.visibility.Edge;
import com.vesas.spacefly.visibility.EndPoint;
import com.vesas.spacefly.visibility.Visibility;
import com.vesas.spacefly.visibility.VisibilityPoly;
import com.vesas.spacefly.world.AbstractGameWorld;
import com.vesas.spacefly.world.procedural.generator.RoomRole;
import com.vesas.spacefly.world.procedural.room.RoomFeature;

public class ProceduralGameWorld extends AbstractGameWorld
{
	public static long worldSeed = 256;
	// Current floor number, threaded into generation the same way worldSeed is. 1 = baseline.
	public static int floorNumber = 1;

	private Array<Feature> feats;

	private LevelExit levelExit;
	private boolean exitReached = false;

	private Visibility visib;

	private ShaderProgram visibShader;

	private FrameBuffer fbo;

	private FloorTheme theme;

	// Live ambient light, lerped toward the mood of whichever room the player stands in. This is
	// how room roles read: a temporal light shift with no baked floor seam at the doorway.
	private float curAmbR, curAmbG, curAmbB;

	// Depth of the room the player last stood in (0 at entrance .. 1 at far exit). Held across
	// corridors so the floor-wide mood drift never snaps back when between rooms.
	private float lastDepth01 = 0f;

	@Override
	public void init()
	{
		
		theme = FloorTheme.values()[(int)(Math.abs(worldSeed) % FloorTheme.values().length)];

		// Start the live ambient at the theme base so there is no flash on floor entry.
		curAmbR = theme.ambR;
		curAmbG = theme.ambG;
		curAmbB = theme.ambB;

		visib = new Visibility();
		visib.startLoad();

		WorldGen gen = new WorldGen(this, visib, theme);
		gen.setFirstRoomCenter(Player.INSTANCE.getWorldCenter());
		feats = gen.generate(worldSeed, floorNumber);
		levelExit = new LevelExit(gen.getExitPosition().x, gen.getExitPosition().y);

		initVaults(gen.getVaults());
		
		visib.finishLoad();

		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		fbo = new FrameBuffer(Format.RGBA8888, (int)width, (int)height, false);
		
		final String vertexShaderString = Gdx.files.local("data/vertexShader.glsl").readString();
		final String visibPixelShaderString = Gdx.files.local("data/visibShader.glsl").readString();
		visibShader = new ShaderProgram(vertexShaderString, visibPixelShaderString);

		visibShader.bind();
		visibShader.setUniformf("ambientColor", 0.15f, 0.15f, 0.15f, 0.25f);
		
		Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888 );
		pixmap.setColor(1.0f, 1.0f,1.0f,1.0f);
		pixmap.fill();
		
		textureSolid = new Texture(pixmap);
		
	}
	
	@Override
	public void draw(GameScreen screen)
	{

		Vector2 playerCenter = Player.INSTANCE.getWorldCenter();
		
		float screenHeight = screen.viewport.getScreenHeight();
		float screenWidth = screen.viewport.getScreenWidth();
		
		visibShader.bind();
		visibShader.setUniformf("ambientColor", curAmbR, curAmbG, curAmbB, 0.519f);
		visibShader.setUniformi("u_lightmap", 1);
		visibShader.setUniformf("resolution", screenWidth, screenHeight);
		
		// defaultShader.setUniformf("viewcenc", screenWidth*0.5f, screenHeight*0.5f);
		
		
		// screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		// Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		// Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_DST_ALPHA);
		// Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);

		screen.worldBatch.end();
		
		//draw the light to the FBO
		fbo.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		visib.setLightLocation( playerCenter.x, playerCenter.y);
		visib.sweep();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_COLOR, GL20.GL_ONE_MINUS_SRC_ALPHA );

		drawVisibility( screen, fbo, playerCenter );

		Gdx.gl.glDisable(GL20.GL_BLEND);
		// screen.worldBatch.draw(light, playerCenter.x - lightSize*0.5f , playerCenter.y - lightSize*0.5f, lightSize, lightSize);
		
		fbo.end();
		
		fbo.getColorBufferTexture().bind(1);
		textureSolid.bind(0);
		
		screen.worldBatch.begin();
		
//		fbo.getColorBufferTexture().bind(1); //this is important! bind the FBO to the 2nd texture unit

		screen.worldBatch.setShader(null);
		
		long startNano = System.nanoTime();
		 
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			feat.draw(screen.worldBatch);
		}
		long endNano = System.nanoTime();
		FrameTime.features = endNano - startNano;

		screen.worldBatch.setShader(visibShader);

		startNano = System.nanoTime();
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			feat.drawWithVisibility(screen);
		}
		endNano = System.nanoTime();
		FrameTime.featvisib = endNano - startNano;
	
		// drawVisibility( screen, fbo, playerCenter );
		
		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			Monster m = monsters.get(i);
			m.draw(screen);
		}
		
		for( int i = 0, size = resources.size; i < size ; i++ )
		{
			AnimateEntity p = resources.get( i );
			p.draw( screen );
		}

		for( int i = 0, size = systems.size; i < size ; i++ )
		{
			ExplosionInterface explo = systems.get( i );
			explo.draw( screen );
		}

		// Damage numbers draw without the visibility shader so they stay bright and readable
		// even in fogged / dim areas, and with normal alpha blending (explosions leave the
		// batch in additive mode, which would erase the dark shadow and blow out the text).
		screen.worldBatch.setShader(null);
		screen.worldBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			monsters.get( i ).drawHealthBar( screen.worldBatch );
		}
		drawFloaters( screen );

		screen.worldBatch.end();

		levelExit.draw(screen);

		if( DebugHelper.VISIB_DEBUG ) {
			renderVisibilityDebug2(screen);
		}
		
	}
	
	private void renderVisibilityDebug2(GameScreen screen) 
	{
		
		G.shapeRenderer.begin(ShapeType.Line);
		
		G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.299f);
		
		List<Edge> edges = visib.edges;
		final int size = edges.size();
		for( int i = 0; i < size; i++ )
		{
			final Edge edge = edges.get( i );
			
			if( edge.isBoundary() ) 
			{
				G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 0.599f);
			}
			else 
			{
				float val = edge.procRank;
				val = val * 0.5f;
				if(val > 1.0f) {
					val = 1.0f;
				}

				G.shapeRenderer.setColor(0.0f + val, 0.299f, 0.299f, 0.499f);
			}

			final EndPoint e0 = edge.getEndPoint1();
			final float ax = e0.point.x;
			final float ay = e0.point.y;
			
			final EndPoint e1 = edge.getEndPoint2();
			final float bx = e1.point.x;
			final float by = e1.point.y;
						
			G.shapeRenderer.line(ax, ay, bx, by);

		}

		G.shapeRenderer.end();

		/*
		screen.worldBatch.begin();
		G.font.draw(screen.worldBatch, "S", 123 - 16, 123 + 15);
		screen.worldBatch.end();
		*/
	}
	
	
	public void drawMiniMap( GameScreen screen )
	{
		G.shapeRenderer.begin(ShapeType.Filled);
		G.shapeRenderer.setColor(0.6f, 0.6f, 0.6f, 0.5f);
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );

			feat.drawMiniMap();
			
			

			// public void polygon (float[] vertices) {
		}
		
		G.shapeRenderer.setColor(1.0f, 0.0f, 0.0f, 1.0f);
		for( int i = 0, size = monsters.size; i < size; i++ )
		{
			Monster m = monsters.get(i);
			Vector2 pos = m.getBody().getWorldCenter();
			
			if( m instanceof ZipperMonster )
			{
				G.shapeRenderer.setColor(0.999f, 0.5f, 0.3f, 1.0f);
				G.shapeRenderer.circle( pos.x, pos.y, 0.5f, 5);
			}
			else
			{
				G.shapeRenderer.setColor(0.999f, 0.0f, 0.0f, 1.0f);
				G.shapeRenderer.circle( pos.x, pos.y, 1.0f, 5);
			}
				
		}

		levelExit.drawMiniMap();

		G.shapeRenderer.end();
	}

	private Texture textureSolid;
	
	private void drawVisibility( GameScreen screen, FrameBuffer fb, Vector2 playerCenter )
	{
		VisibilityPoly visiPoly = visib.getVisibPoly();
		
		// these are the triangle endpoints
		final Array<Vector2> points = visiPoly.getTriEndPoints();
		
		G.shapeRenderer.begin(ShapeType.Filled);

//		Matrix4 mat = new Matrix4();
//		mat.setToTranslation(0.0f, 1.5f, 0.0f);
//		G.shapeRenderer.setTransformMatrix(mat);
		G.shapeRenderer.setColor(0.999f, 0.999f, 0.999f, 1.0f);

		for( int i = 0; i < points.size; i = i + 2 )
		{
			final Vector2 p1 = points.get( i );
			final Vector2 p2 = points.get( i + 1 );
			G.shapeRenderer.triangle(playerCenter.x, playerCenter.y, p1.x, p1.y, p2.x, p2.y);
		}
		
		G.shapeRenderer.end();
	}

	@Override
	public void tick(GameScreen screen, float delta)
	{
		for( int i = 0; i < feats.size; i++ )
		{
			Feature feat = feats.get( i );
			feat.tick(screen, delta);
		}

		for( int i = 0, size = resources.size; i < size ; i++ )
		{
			AnimateEntity p = resources.get( i );
			p.tick(screen, delta );
		}

		for( int i = 0, size = systems.size; i < size ; i++ )
		{
			ExplosionInterface p = systems.get( i );
			p.tick(delta);
		}

		floaterTick( delta );

		super.monsterTick(screen, delta );

		tickVaults();

		updateAmbient(delta);

		levelExit.tick(delta);
		if (!exitReached && levelExit.isPlayerInRange(Player.INSTANCE.getWorldCenter())) {
			exitReached = true;
		}

		cleanup();
	}

	// --- Vault gated loot ---------------------------------------------------------------
	// A vault's reward is not on the floor when you enter; it appears once the room's
	// guardians are cleared. No HUD, no announcement — the player simply finds the drop.
	// All state is instance-level, rebuilt per floor, so there is nothing static to reset.

	private static class VaultState
	{
		final Rectangle bounds;
		final Vector2 loot;
		boolean armed;    // player has stepped inside
		boolean revealed; // loot has been dropped

		VaultState( Rectangle bounds, Vector2 loot )
		{
			this.bounds = bounds;
			this.loot = loot;
		}
	}

	private final Array<VaultState> vaults = new Array<VaultState>();

	private void initVaults( Array<WorldGen.VaultInfo> infos )
	{
		vaults.clear();
		for( int i = 0; i < infos.size; i++ )
		{
			WorldGen.VaultInfo vi = infos.get( i );
			vaults.add( new VaultState( vi.bounds, vi.loot ) );
		}
	}

	private void tickVaults()
	{
		if( vaults.size == 0 )
			return;

		Vector2 pc = Player.INSTANCE.getWorldCenter();

		for( int i = 0; i < vaults.size; i++ )
		{
			VaultState v = vaults.get( i );
			if( v.revealed )
				continue;

			// Arm only once the player has actually entered, so a vault the player never
			// engages does not dump its loot from across the map.
			if( !v.armed )
			{
				if( v.bounds.contains( pc.x, pc.y ) )
					v.armed = true;
				else
					continue;
			}

			// Reveal once no guardian remains inside the vault.
			if( !anyMonsterInside( v.bounds ) )
			{
				// Vault reward is gated behind a fight, so bias it toward the better tiers.
				addResource( new WeaponDrop( v.loot.x, v.loot.y, Weapon.randomDrop( GenSeed.random, floorNumber + 3 ) ) );
				v.revealed = true;
			}
		}
	}

	private boolean anyMonsterInside( Rectangle bounds )
	{
		for( int i = 0; i < monsters.size; i++ )
		{
			Vector2 c = monsters.get( i ).getBody().getWorldCenter();
			if( bounds.contains( c.x, c.y ) )
				return true;
		}
		return false;
	}

	// --- Per-room ambient mood -----------------------------------------------------------
	// The role of the room the player currently occupies picks a target ambient colour; the
	// live ambient eases toward it. Because ambient is a single global uniform, the corridor
	// and room are always lit the same at any instant, so the mood fades in as you cross the
	// threshold — no hard colour border at the doorway.

	private RoomFeature roomAtPlayer()
	{
		Vector2 pc = Player.INSTANCE.getWorldCenter();
		for( int i = 0; i < feats.size; i++ )
		{
			Feature f = feats.get( i );
			if( f instanceof RoomFeature )
			{
				float x = f.getXpos(), y = f.getYpos();
				if( pc.x >= x && pc.x <= x + f.getWidth() && pc.y >= y && pc.y <= y + f.getHeight() )
					return (RoomFeature) f;
			}
		}
		return null; // corridors and thresholds stay neutral
	}

	private void updateAmbient( float delta )
	{
		RoomFeature room = roomAtPlayer();
		RoomRole role = room != null ? room.getRole() : RoomRole.STANDARD;
		if( room != null )
			lastDepth01 = room.getDepth01();

		// Base ambient drifts from the theme's entrance mood toward its "deep" mood by how far into
		// the floor the player is — a smooth, non-obvious transition across the whole level. Kept
		// moderate: a whole-floor light shift reads as atmosphere long before it reads as "coloured".
		float d = lastDepth01;
		float tr = theme.ambR + (theme.deepAmbR - theme.ambR) * d;
		float tg = theme.ambG + (theme.deepAmbG - theme.ambG) * d;
		float tb = theme.ambB + (theme.deepAmbB - theme.ambB) * d;

		// Then nudge by the current room's role.
		switch( role )
		{
			case VAULT:     tr *= 0.80f; tg *= 0.87f; tb *= 1.05f; break; // cool, dim, secure
			case SANCTUARY: tr *= 1.14f; tg *= 1.07f; tb *= 0.95f; break; // warm, calm, brighter
			case BOSS:      tr *= 1.08f; tg *= 0.80f; tb *= 0.78f; break; // dim, tense, red
			case ARENA:     tr *= 1.06f; tg *= 1.06f; tb *= 1.10f; break; // open, a touch brighter
			case STANDARD:
			default:        break;
		}
		tr = clamp01( tr ); tg = clamp01( tg ); tb = clamp01( tb );

		// Ease over roughly half a second so crossing a threshold is a smooth fade, not a snap.
		float k = Math.min( 1f, delta * 3.5f );
		curAmbR += (tr - curAmbR) * k;
		curAmbG += (tg - curAmbG) * k;
		curAmbB += (tb - curAmbB) * k;
	}

	private static float clamp01( float v )
	{
		return v < 0f ? 0f : (v > 1f ? 1f : v);
	}

	@Override
	public boolean isExitReached() {
		return exitReached;
	}

	@Override
	public void destroyWorld() {
		monsters.clear();
		monstersToBeRemoved.clear();
		resources.clear();
		resourcesToBeRemoved.clear();
		systems.clear();
		removeSystems.clear();
		floaters.clear();
		removeFloaters.clear();
		feats.clear();
		vaults.clear();
		levelExit = null;
		exitReached = false;
		if (fbo != null) {
			fbo.dispose();
			fbo = null;
		}
		if (visibShader != null) {
			visibShader.dispose();
			visibShader = null;
		}
		if (textureSolid != null) {
			textureSolid.dispose();
			textureSolid = null;
		}
		FloorPatina.dispose();
		ZipperCloudManager.reset();
	}

}
