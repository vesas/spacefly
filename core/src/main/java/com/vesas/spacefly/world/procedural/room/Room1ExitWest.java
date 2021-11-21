package com.vesas.spacefly.world.procedural.room;

import com.vesas.spacefly.game.Screen;

public class Room1ExitWest extends Room
{
	private Block5Right a1;
	private Block5Right a2;
	
	private Block5Right b1;
	private Block5Right b2;
	
	
	private Block2Up e1;
	private Block2Up e2;
	
	private Block5Up e3;
	
	private int xsize;
	private int ysize;
	
	public void init()
	{
		
	}
	
	public void init( float xpos, float ypos, float rotate)
	{
		a1 = new Block5Right();
		a1.init( xpos + 0.5f, ypos + 0, 0);
		a2 = new Block5Right();
		a2.init( xpos + 3, ypos + 0, 0);
		
		b1 = new Block5Right();
		b1.init( xpos + 0.5f, ypos + 3.0f, 0);
		b2 = new Block5Right();
		b2.init( xpos + 3f, ypos + 3.0f, 0);
		
		e1 = new Block2Up();
		e1.init( xpos, ypos, 0);
		e2 = new Block2Up();
		e2.init( xpos, ypos + 2.5f, 0);
		e3 = new Block5Up();
		e3.init( xpos + 5f , ypos + 0.5f, 0);
	}
	
	public void setSize( int xsize, int ysize )
	{
		this.xsize = xsize;
		this.ysize = ysize;
	}
	
	public void draw(Screen screen)
	{
		a1.draw( screen );
		a2.draw( screen );
		b1.draw( screen );
		b2.draw( screen );
		
		e1.draw( screen );
		e2.draw( screen );
		e3.draw( screen );
	}

	public void tick(Screen screen, float delta)
	{
		a1.tick( screen, delta);
		a2.tick( screen, delta);
		b1.tick( screen, delta);
		b2.tick( screen, delta);
		
		e1.tick( screen, delta);
		e2.tick( screen, delta);
		e3.tick( screen, delta);
	}

}


