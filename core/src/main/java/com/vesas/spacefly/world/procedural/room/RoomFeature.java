package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.world.procedural.Feature;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.room.rectangleroom.Exit;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public abstract class RoomFeature implements Feature
{
	protected float xpos;
	protected float ypos;
	
	protected float width;
	protected float height;
	
	public float getXpos() { return xpos; }		
	public float getYpos() { return ypos; }		
	
	public float getWidth() { return width; } 
	public float getHeight() { return height; }  
	
	protected Array<Exit> exits = new Array<Exit>();
	
	protected boolean exitsContain( ExitDir e )
	{
		for( int i = 0; i < exits.size; i++ )
		{
			if( exits.get(i).exitDir.equals( e ) )
				return true;
		}
		
		return false;
	}

	@Override
    public void drawMiniMap() {
		float xpos = getXpos();
		float ypos = getYpos();
		float width = getWidth();
		float height = getHeight();
		
		G.shapeRenderer.rect(xpos, ypos, width, height);
    }
	
	public void addConnectors( ObjectMap<ExitDir, MetaPortal> portals )
	{
		Values<MetaPortal> values = portals.values();
		
		while( values.hasNext() )
		{
			MetaPortal port = values.next();
			
			Exit exit = new Exit();
			
			exit.exitDir = port.getExit();
			exit.exitWidth = (int) port.getWidth();
			
			this.addExit( exit );
		}
		
	}
	
	protected void addExit( Exit exit )
	{
		exits.add( exit );
	}

	public FeatureConnector getConnectorFromDir( ExitDir dir )
	{
		for( int i = 0; i < exits.size; i++ )
		{
			if( exits.get(i).exitDir.equals( dir ) )
				return exits.get(i).connector;
		}
		
		return null;
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
	
	public Exit getExit( ExitDir e )
	{
		for( int i = 0; i < exits.size; i++ )
		{
			if( exits.get(i).exitDir.equals( e ) )
			{
				Exit exit = exits.get(i);
			
				return exit;
			}
		}
		
		return null;
	}
	
	public float getExitStartX( ExitDir e )
	{
		for( int i = 0; i < exits.size; i++ )
		{
			if( exits.get(i).exitDir.equals( e ) )
			{
				Exit exit = exits.get(i);
				
				return exit.exitX;
			}
				
		}
		
		return 0.0f;
	}
	
	public float getExitStartY( ExitDir e )
	{
		for( int i = 0; i < exits.size; i++ )
		{
			if( exits.get(i).exitDir.equals( e ) )
			{
				Exit exit = exits.get(i);
				
				return exit.exitY;
			}
				
		}
		
		return 0.0f;
	}
}
