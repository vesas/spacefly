package com.vesas.spacefly.world.procedural.corridor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.util.DebugHelper;
import com.vesas.spacefly.util.FrameTime;
import com.vesas.spacefly.world.procedural.Decal;
import com.vesas.spacefly.world.procedural.Feature;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.FloorPatina;
import com.vesas.spacefly.world.procedural.FloorTheme;
import com.vesas.spacefly.world.procedural.PipeSegment;
import com.vesas.spacefly.world.procedural.ProceduralGameWorld;

public class AxisAlignedCorridor implements Feature
{
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	private Array<PipeSegment> pipeSegments = new Array<PipeSegment>();
	private Array<Decal> decals = new Array<Decal>();

	public enum Dir {
		WE, SN; // west-east or south-north
	}

	public Dir dir = Dir.WE;
	
	// these are floor coordinates
	protected float xpos;
	protected float ypos;
	
	// these are floor width and height
	protected float width;
	protected float height;

	private float len;
	
	public float getXpos() { return xpos; }		
	public float getYpos() { return ypos; }		
	
	public float getWidth() { return width; } 
	public float getHeight() { return height; }  
	
	Texture tex;
	
	public void addBlocks( Array<FeatureBlock> blocks )
	{
		this.blocks.addAll( blocks );
	}

	public void addPipeSegments(Array<PipeSegment> pipes) {
		this.pipeSegments.addAll(pipes);
	}

	public void addDecals(Array<Decal> d) {
		this.decals.addAll(d);
	}

	@Override
    public void drawMiniMap() {
		float xpos = getXpos();
		float ypos = getYpos();
		float width = getWidth();
		float height = getHeight();
		
		G.shapeRenderer.rect(xpos, ypos, width, height);
    }

	private static final float WALL_WIDTH = 0.5f;

	@Override
	public void drawWithVisibility(GameScreen screen) {
		screen.worldBatch.draw( tex, this.xpos, this.ypos, this.width, this.height);
		for (int i = 0; i < pipeSegments.size; i++) {
			pipeSegments.get(i).draw(screen.worldBatch);
		}
		for (int i = 0; i < decals.size; i++) {
			decals.get(i).draw(screen.worldBatch);
		}
	}
	
	public void draw(SpriteBatch batch)
	{
		long startNano = System.nanoTime();
		// draw all wall blocks
		final int size = blocks.size;
		
		for( int i = 0; i < size; i++ )
		{
			final FeatureBlock block = blocks.get( i );
			block.draw( batch );
		}
		long endNano = System.nanoTime();
		FrameTime.corridorfeatures += (endNano - startNano);

		if(DebugHelper.PROC_GEN_DEBUG1) {
			G.shapeRenderer.begin(ShapeType.Line);
			G.shapeRenderer.setColor(0.4f, 0.4f, 0.9f, 0.1f);
			
			// botton leftt to right
			G.shapeRenderer.line(this.xpos, this.ypos, this.xpos + this.width, this.ypos);
			// top left to right
			G.shapeRenderer.line(this.xpos, this.ypos + this.height, this.xpos + this.width, this.ypos + height);

			// left bottom to top
			G.shapeRenderer.line(this.xpos, this.ypos, this.xpos, this.ypos + this.height);

			// right bottom to top
			G.shapeRenderer.line(this.xpos + this.width, this.ypos, this.xpos + this.width, this.ypos + this.height);

			G.shapeRenderer.end();
		}
	}
	
	public void setPosition( float x, float y )
	{
		this.xpos = x;
		this.ypos = y;
	}
	
	public void setDimensions( float width, float height )
	{
		this.width = width;
		this.height = height;
	}

	public void tick(GameScreen screen, float delta)
	{
	}
	
	public void setLength( int len )
	{
		this.len = len;
	}
	
	private FloorTheme theme = FloorTheme.STATION;

	public void setTheme(FloorTheme theme) { this.theme = theme; }

	public void init()
	{
		int pw = Math.max( 1, (int) (this.width * 64f) );
		int ph = Math.max( 1, (int) (this.height * 64f) );

		Pixmap pixmap = new Pixmap( pw, ph, Pixmap.Format.RGBA8888 );
		pixmap.setBlending( Pixmap.Blending.None );
		pixmap.setColor( theme.floorColor );
		pixmap.fill();

		// Bake the surface material as continuous world-space noise (no stretch, whatever the
		// corridor's aspect), fading to the neutral floor colour at the two ends where it meets rooms
		// so the junctions stay seamless. Only the length axis fades — a corridor is too thin to fade
		// its sides without washing the material out.
		float cx = xpos + width * 0.5f, cy = ypos + height * 0.5f;
		long seed = FloorPatina.featureSeed( ProceduralGameWorld.worldSeed, ProceduralGameWorld.floorNumber, cx, cy );
		boolean we = (dir == Dir.WE);
		FloorPatina.bakeMaterial( pixmap, 64f, xpos, ypos, width, height,
				FloorPatina.pickMaterial( seed, theme ), we, !we, 0.6f );

		tex = new Texture( pixmap );
		tex.setFilter( Texture.TextureFilter.Linear, Texture.TextureFilter.Linear );
		pixmap.dispose();
	}
	
}
