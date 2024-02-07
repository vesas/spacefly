package com.vesas.spacefly.world.procedural.room.octaroom;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.world.procedural.FeatureBlock;
import com.vesas.spacefly.world.procedural.room.RoomFeature;

/*
 * Octagonal room
 */
public class OctaRoom extends RoomFeature
{
	// wall width in WORLD units
	public static float WALL_WIDTH = 0.5f;
	
	private Array<FeatureBlock> blocks = new Array<FeatureBlock>();
	
	Texture tex;
	
	public void addBlocks( Array<FeatureBlock> blocks ) {
		this.blocks.addAll( blocks );
	}

	public void draw(SpriteBatch batch)	
	{
		long startNano = System.nanoTime();

		final int size = blocks.size;
		
		for( int i = 0; i < size; i++ )
		{
			final FeatureBlock block = blocks.get( i );
			block.draw( batch );
		}
	}

	public void tick(GameScreen screen, float delta)
	{
	}
	
	private static Color col1 = new Color(0.24f, 0.25f, 0.33f, 1.0f);

	private static float PIXELS_PER_UNIT = 64f;

	@Override
	public void init()
	{
		float sidelen = (float)(this.getWidth() / (1 + Math.sqrt(2)));
		float apersqrttwo = (float)(sidelen / Math.sqrt(2));

		// this width is in "units". (eg 5-13)
		float ratio = this.width / this.height;

		int mapwidth = (int) (this.width*PIXELS_PER_UNIT);
		int mapheight = (int) (this.height*PIXELS_PER_UNIT);

		Pixmap pixmap = new Pixmap(mapwidth, mapheight, Pixmap.Format.RGBA8888 );

		pixmap.setColor( Color.CLEAR );
		pixmap.fill();
		pixmap.setColor( col1 );

		// first, stripe from bottom to top, portal width
		pixmap.fillRectangle((int)((apersqrttwo)*PIXELS_PER_UNIT), (int)((0)*PIXELS_PER_UNIT), (int)((sidelen)*PIXELS_PER_UNIT),(int)((this.height)*PIXELS_PER_UNIT));

		// left side rectangle
		pixmap.fillRectangle((int)((0)*PIXELS_PER_UNIT), (int)((apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo)*PIXELS_PER_UNIT),(int)((sidelen)*PIXELS_PER_UNIT));

		// right side rectangle
		pixmap.fillRectangle((int)((this.width - apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo  + 0.01f)*PIXELS_PER_UNIT),(int)((sidelen)*PIXELS_PER_UNIT));

		// top left triangle (y-axis points down)
		pixmap.fillTriangle((int)((0)*PIXELS_PER_UNIT), (int)((apersqrttwo)*PIXELS_PER_UNIT),
							(int)((apersqrttwo)*PIXELS_PER_UNIT), (int)((0)*PIXELS_PER_UNIT),
							(int)((apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo)*PIXELS_PER_UNIT));

		// bottom left triangle (y-axis points down)
		pixmap.fillTriangle((int)((0)*PIXELS_PER_UNIT), (int)((apersqrttwo+sidelen)*PIXELS_PER_UNIT),
							(int)((apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo+sidelen+apersqrttwo)*PIXELS_PER_UNIT),
							(int)((apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo+sidelen)*PIXELS_PER_UNIT));
		
		
		// top right triangle (y-axis points down)
		pixmap.fillTriangle((int)((apersqrttwo + sidelen)*PIXELS_PER_UNIT), (int)((0)*PIXELS_PER_UNIT),
							(int)((apersqrttwo + sidelen)*PIXELS_PER_UNIT), (int)((apersqrttwo)*PIXELS_PER_UNIT),
							(int)((apersqrttwo + sidelen+ apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo)*PIXELS_PER_UNIT));

		// bottom right triangle (y-axis points down)
		pixmap.fillTriangle((int)((apersqrttwo + sidelen)*PIXELS_PER_UNIT), (int)((apersqrttwo+sidelen)*PIXELS_PER_UNIT),
							(int)((apersqrttwo + sidelen)*PIXELS_PER_UNIT), (int)((apersqrttwo+sidelen+apersqrttwo)*PIXELS_PER_UNIT),
							(int)((apersqrttwo + sidelen + apersqrttwo)*PIXELS_PER_UNIT), (int)((apersqrttwo+sidelen)*PIXELS_PER_UNIT));

		tex = new Texture(pixmap);
		pixmap.dispose();
	}

	@Override
	public void drawWithVisibility(GameScreen screen) {
		screen.worldBatch.draw( tex, this.xpos, this.ypos, this.width, this.height);
	}
	

}


