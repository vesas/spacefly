package com.vesas.spacefly.world.procedural.generator;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaRectangleRoom implements MetaFeature
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
			return Exits.values()[GenSeed.random.nextInt( Exits.values().length )];
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

	public MetaRectangleRoom( )
	{ 
		
	}
	
	public MetaRectangleRoom( float w, float h )
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
	
	@Override
	public void closePortal( MetaPortal port )
	{
		portals.remove( port.getExit() );
	}
	
	public void setSize( float w, float h )
	{
		rect.width = w;
		rect.height = h;
	}
	
	public void addPortal( ExitDir exitDir, MetaPortal portal )
	{
		portal.target  = null;
		portal.setExit(exitDir);
		
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
	
	@Override
	public boolean overlaps(Rectangle rect)
	{
		return this.rect.contains(rect) || this.rect.overlaps( rect );
	}

	@Override
	public Rectangle getBounds()
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

	@Override
	public Array<MetaPortal> getPortalArray(MetaPortal exclude) 
	{
		Array<MetaPortal> ret = new Array<MetaPortal>();

		Array<MetaPortal> values = portals.values().toArray();

		for(MetaPortal port : values )
		{
			if(!port.equals(exclude))
			{
				ret.add(port);
			}
		}
		return ret;
	}

	@Override
	public String toString()
	{
		String ret = "";

		ret += "MetaRectangleRoom(x:";
		ret += rect.x;
		ret += ",y:";
		ret += rect.y;
		ret += ",w:";
		ret += rect.width;
		ret += ",h:";
		ret += rect.width;
		ret += ")";
		return ret;
	}
}
