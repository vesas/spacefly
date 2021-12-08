package com.vesas.spacefly.world.procedural.room.rectangleroom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.game.Screen;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.lsystem.SimpleLSystem;
import com.vesas.spacefly.world.procedural.lsystem.SimpleWineSystem;
import com.vesas.spacefly.world.procedural.room.Room;

public class RectangleRoom extends Room
{
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	Texture tex;
	
	public void addBlocks( Array<FeatureBlock> blocks )
	{
		this.blocks.addAll( blocks );
	}

	public void draw(Screen screen)	
	{
		screen.worldBatch.draw( tex, this.xpos, this.ypos, this.width, this.height);
		
		final int size = blocks.size;
		
		for( int i = 0; i < size; i++ )
		{
			final FeatureBlock block = blocks.get( i );
			block.draw( screen );
		}
		
//		G.wFont.setScale(0.007f, 0.007f);
//		G.wFont.draw( screen.worldBatch, "w:" + this.width + " h:" + this.height, this.xpos  + 1, this.ypos  + 1.5f);
	}

	public void tick(Screen screen, float delta)
	{
	}
	
	private static Color col1 = new Color(0.35f, 0.35f, 0.36f, 1.0f);
	
	private static Color col2 = new Color(0.30f, 0.30f, 0.30f, 1.0f );
	private static Color col3 = new Color(0.20f, 0.20f, 0.20f, 1.0f );
	private static Color col4 = new Color(0.23f, 0.23f, 0.23f, 0.3f );

	@Override
	public void init()
	{
		// this width is in "units". (eg 5-13)
		float ratio = this.width / this.height;
		int mapwidth = (int) (this.width*64f);
		int mapheight = (int) (this.height*64f);
				
		Pixmap pixmap = new Pixmap(mapwidth, mapheight, Pixmap.Format.RGBA8888 );
//		pixmap.setFilter(Filter.BiLinear);
		pixmap.setColor( col1 );
		pixmap.fill();
		
		int halfW = (int) (pixmap.getWidth() * 0.5f);
		int halfH = (int) (pixmap.getHeight() * 0.5f);
		
//		pixmap.setColor( col2 );
//		pixmap.fillRectangle(0, 0,  halfW, halfH);
		
//		pixmap.setColor( col3 );
//		pixmap.fillRectangle(halfW, halfH,  pixmap.getWidth()-halfW, pixmap.getHeight()-halfH);
		
//		pixmap.drawPixel(0,0,  Color.rgba8888(col2.r, col2.g, col2.b, 1.0f));
//		pixmap.drawPixel(1,1, Color.rgba8888(col3.r, col3.g, col3.b, 1.0f));
		
//		pixmap.setBlending(Blending.SourceOver);
		pixmap.setColor( col4 );
		for( int i = 0 ; i < 4; i++ )
		{
			float xx = pixmap.getWidth() * GenSeed.random.nextFloat();
			float yy = pixmap.getHeight() * GenSeed.random.nextFloat();
			pixmap.drawCircle((int)xx, (int)yy, (int)(GenSeed.random.nextFloat() * 19.5f + 1.5f));
		}
		
		if(GenSeed.random.nextBoolean() )
			drawWineProps( pixmap );
		if(GenSeed.random.nextBoolean() )
			drawWineProps( pixmap );
		
		if(GenSeed.random.nextBoolean() )
			drawTreeProps( pixmap );
		
		tex = new Texture(pixmap);
		
	}
	
	private static Color winePropCol = new Color(0.211f, 0.241f, 0.231f, 1.0f );
	private static Color propCol = new Color(0.171f, 0.221f, 0.195f, 1.0f );
	
	private void drawWineProps( Pixmap pm )
	{
		pm.setColor( winePropCol );
		
		int exitdir = GenSeed.random.nextInt(4);
//		exitdir = 0;
		ExitDir ex1 = ExitDir.values()[exitdir];
		
		Exit exit1 = this.getExit( ex1 );
		
		// cant draw since we have an exit there
		if( exit1 != null )
		{
			return;
		}

		Vector2 dir = new Vector2();
		Vector2 start = new Vector2();
		
		// pixmap has y coord from top down
		if( ex1 == ExitDir.S )
		{
			dir.x = 0;
			dir.y = -pm.getHeight() * 0.2f;
			if( dir.y < -160 )
				dir.y = -160;
			
			start.x = pm.getWidth() * 0.2f + (float) (GenSeed.random.nextFloat() * pm.getWidth() * 0.6f );
			start.y = pm.getHeight();
		}
		if( ex1 == ExitDir.N )
		{
			dir.x = 0;
			dir.y = pm.getHeight() * 0.2f;
			if( dir.y > 160 )
				dir.y = 160;
			
			start.x = (float) pm.getWidth() * 0.2f + (GenSeed.random.nextFloat() * pm.getWidth() * 0.6f );
			start.y = 0;
			
		}
		
		if( ex1 == ExitDir.W )
		{
			
			dir.x = pm.getWidth() * 0.2f;
			if( dir.x > 160 )
				dir.x = 160;
			dir.y = 0.0f;

			start.x = 0.0f;
			start.y = (float) pm.getHeight() * 0.2f + (GenSeed.random.nextFloat() * pm.getHeight() * 0.6f );
			
		}
		
		if( ex1 == ExitDir.E )
		{
			
			dir.x = -pm.getWidth() * 0.2f;
			if( dir.x < -160 )
				dir.x = -160;
			dir.y = 0.0f;
			
			start.x = pm.getWidth();
			start.y = (float) pm.getHeight() * 0.2f + (GenSeed.random.nextFloat() * pm.getHeight() * 0.6f );
		}
		
		SimpleWineSystem system = SimpleWineSystem.getInstance();
		
		system.setStartPos( start.x , start.y );
		system.setStartDir( dir.x , dir.y );
		system.setDirRandomness( 145f );
		system.setMaxLevel( 2 + GenSeed.random.nextInt(6) );
		system.setWidthScale( 0.25f );
		
		system.draw( pm );
		
	}
	
	private void drawTreeProps( Pixmap pm )
	{
		pm.setColor( propCol );
		
		int exitdir = GenSeed.random.nextInt(4);
//		exitdir = 0;
		ExitDir ex1 = ExitDir.values()[exitdir];
		
		Exit exit1 = this.getExit( ex1 );
		
		// cant draw since we have an exit there
		if( exit1 != null )
		{
//			return;
		}
		
		Vector2 dir = new Vector2();
		Vector2 start = new Vector2();
		
		// pixmap has y coord from top down
		if( ex1 == ExitDir.S )
		{
			dir.x = 0;
			dir.y = -pm.getHeight() * 0.4f;
			if( dir.y < -260 )
				dir.y = -260;
			
			start.x = pm.getWidth() * 0.2f + (float) (GenSeed.random.nextFloat() * pm.getWidth() * 0.6f );
			start.y = pm.getHeight();
		}
		if( ex1 == ExitDir.N )
		{
			dir.x = 0;
			dir.y = pm.getHeight() * 0.4f;
			if( dir.y > 260 )
				dir.y = 260;
			
			start.x = (float) pm.getWidth() * 0.2f + (GenSeed.random.nextFloat() * pm.getWidth() * 0.6f );
			start.y = 0;
			
		}
		
		if( ex1 == ExitDir.W )
		{
			
			dir.x = pm.getWidth() * 0.4f;
			if( dir.x > 260 )
				dir.x = 260;
			dir.y = 0.0f;

			start.x = 0.0f;
			start.y = (float) pm.getHeight() * 0.2f + (GenSeed.random.nextFloat() * pm.getHeight() * 0.6f );
			
		}
		
		if( ex1 == ExitDir.E )
		{
			
			dir.x = -pm.getWidth() * 0.4f;
			if( dir.x < -260 )
				dir.x = -260;
			dir.y = 0.0f;
			
			start.x = pm.getWidth();
			start.y = (float) pm.getHeight() * 0.2f + (GenSeed.random.nextFloat() * pm.getHeight() * 0.6f );
		}
		
		SimpleLSystem system = SimpleLSystem.getInstance();
		
		system.setStartPos( start.x , start.y );
		system.setStartDir( dir.x , dir.y );
		system.setDirRandomness( 45f );
		system.setMaxLevel( 1 + GenSeed.random.nextInt(6) );
		system.setWidthScale( 0.35f );
		
		system.draw( pm );
		
	}

}


