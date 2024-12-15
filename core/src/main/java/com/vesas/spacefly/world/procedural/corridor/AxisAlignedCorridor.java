package com.vesas.spacefly.world.procedural.corridor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.screen.GameScreen;
import com.vesas.spacefly.util.DebugHelper;
import com.vesas.spacefly.util.FrameTime;
import com.vesas.spacefly.world.procedural.Feature;
import com.vesas.spacefly.world.procedural.FeatureBlock;

public class AxisAlignedCorridor implements Feature
{
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();

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
		
		// draw floor texture
		screen.worldBatch.draw( tex, this.xpos, this.ypos, this.width, this.height);

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
	
	private static Color col1 = new Color(0.24f, 0.25f, 0.33f, 1.0f);
	
	public void init()
	{
		int width = 1;
		int height = 1;
		
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888 );
		pixmap.setFilter(Filter.BiLinear);
		pixmap.setColor( col1 );
		pixmap.fill();
		
		tex = new Texture(pixmap);
		pixmap.dispose();
	}
	
}
