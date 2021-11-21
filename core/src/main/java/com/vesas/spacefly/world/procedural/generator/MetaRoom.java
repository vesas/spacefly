package com.vesas.spacefly.world.procedural.generator;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.world.procedural.room.ExitDir;
import com.vesas.spacefly.world.procedural.room.Room;

public class MetaRoom implements MetaFeature
{
	private Rectangle rect = new Rectangle();
	
	private ObjectMap<ExitDir, MetaPortal> portals = new ObjectMap<ExitDir, MetaPortal>();
	
	public enum Exits
	{
		EXIT_NORTH,
		EXIT_EAST,
		EXIT_WEST,
		EXIT_SOUTH;
		
		static public Exits getRandom()
		{
			return Exits.values()[G.random.nextInt( Exits.values().length )];
		}
		
		public Exits getOpposite()
		{
			if( this == EXIT_NORTH )
				return EXIT_SOUTH;
			
			if( this == EXIT_SOUTH)
				return EXIT_NORTH;
			
			if( this == EXIT_WEST )
				return EXIT_EAST;
			
			if( this == EXIT_EAST )
				return EXIT_WEST;
			
			return EXIT_NORTH;
		}
		
	}
	
	public Room real;
	
	
	public Rectangle getBounds()
	{
		return rect;
	}
	
	public MetaRoom( )
	{ 
		
	}
	
	public MetaRoom( float w, float h )
	{ 
		rect.width = w;
		rect.height = h;
	}
	
	public void setSize( float posx, float posy, float w, float h )
	{
		rect.x = posx;
		rect.y = posy;
		
		rect.width = w;
		rect.height = h;
	}
	
	public ObjectMap<ExitDir, MetaPortal> getPortals()
	{
		return portals;
	}
	
	public void shutPortal( MetaPortal port )
	{
		portals.remove( port.exit );
	}
	
	public void setSize( float w, float h )
	{
		rect.width = w;
		rect.height = h;
	}
	
	public void addPortal( ExitDir exitDir, MetaPortal portal )
	{
		portal.target  = null;
		portal.exit = exitDir;
		
		portals.put( exitDir, portal );	
	}
	
	public MetaPortal getPortal( ExitDir exitDir )
	{
		return portals.get( exitDir );
	}
	
	
	public void positionAsSecondRoomToCorridor( ExitDir exit, MetaCorridor corr )
	{
		
//		MetaRoom room1 = corr.getRoom1();
		Rectangle corrBounds = corr.getBounds();
		
//		corr.addSecondRoom( this );
		
		if( exit.equals( ExitDir.N ) )
		{
			rect.x = (corrBounds.x + corrBounds.width) * 0.5f - this.rect.width * 0.5f;
			rect.y = corrBounds.y - this.rect.height;
		}
		
		if( exit.equals( ExitDir.S ) )
		{
			rect.x = (corrBounds.x + corrBounds.width) * 0.5f - this.rect.width * 0.5f;
			rect.y = corrBounds.y + corrBounds.height;
		}
		if( exit.equals( ExitDir.W ) )
		{
			rect.x = corrBounds.x - this.rect.width;
			rect.y = (corrBounds.y + corrBounds.height) * 0.5f - this.rect.height * 0.5f;
		}
		
		if( exit.equals( ExitDir.E ) )
		{
			rect.x = corrBounds.x + corrBounds.width;
			rect.y = (corrBounds.y + corrBounds.height) * 0.5f - this.rect.height * 0.5f;
		}
		
	}
	
	public void addExit( MetaPortal e, ExitDir exitDir )
	{
//		exits.add( e );
		
	}
	
	public int[] gen()
	{
		return null;
	}
	
	public void stroke( Region region )
	{
		/*
		if( region.safeGetMap(posx, posy) != 0 )
			return;
		
		region.drawHorizontalLine( posx, posx + this.width, posy );
		region.drawHorizontalLine( posx, posx + this.width, posy + this.height );
		
		region.drawVerticalLine( posy, posy + this.height, posx );
		region.drawVerticalLine( posy, posy + this.height, posx + this.width );
	
	*/
	}
	
	private boolean canFindPosition( Region region )
	{
		for( int i = 0 ; i < 10 ; i++ )
		{
			
		}
		
		return false;
	}
	
	@Override
	public boolean overlaps(Rectangle rect)
	{
		return this.rect.contains(rect) || this.rect.overlaps( rect );
	}

	@Override
	public Rectangle getRect()
	{
		return rect;
	}
	

	public float getPosx() 
	{
		return rect.x;
	}
	
	public float getPosy() 
	{
		return rect.y;
	}
}
