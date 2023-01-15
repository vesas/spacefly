package com.vesas.spacefly.world.procedural.corridor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.world.procedural.Feature;
import com.vesas.spacefly.world.procedural.FeatureBlock;

public class AxisAlignedCorridor implements Feature
{
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	protected float xpos;
	protected float ypos;
	
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
	public void drawWithVisibility(Screen screen) {
		
		// draw floor texture
		screen.worldBatch.draw( tex, this.xpos, this.ypos, this.width, this.height);
	}
	
	public void draw(Screen screen)
	{
		// draw all wall blocks
		final int size = blocks.size;
		for( int i = 0; i < size; i++ )
		{
			final FeatureBlock block = blocks.get( i );
			block.draw( screen );
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

	public void tick(Screen screen, float delta)
	{
	}
	
	public void setLength( int len )
	{
		this.len = len;
	}
	
	private static Color col1 = new Color(0.35f, 0.35f, 0.36f, 1.0f);
	public void init()
	{
		int width = 1;
		int height = 1;
		
		Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888 );
		pixmap.setFilter(Filter.BiLinear);
		pixmap.setColor( col1 );
		pixmap.fill();
		
		tex = new Texture(pixmap);
	}
	
}
