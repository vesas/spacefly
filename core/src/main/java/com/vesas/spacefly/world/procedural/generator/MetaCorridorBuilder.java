package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaCorridorBuilder
{
	private MetaPortal startPortal;
	private MetaPortal endPortal;
	private float width;
	private float length;
	
	private Array<MetaPortal> portals = new Array<MetaPortal>();
	
	public static MetaCorridorBuilder INSTANCE = new MetaCorridorBuilder();
	
	private MetaCorridorBuilder() { }
	
	public MetaCorridor build()
	{
		MetaCorridor corr = new MetaCorridor();
		
		width = startPortal.width;
		corr.setLengthWidth( length, width );
		
		float xpos, ypos, xsize, ysize;
		
		ExitDir startExitDir = startPortal.getExit();
		
		if( startExitDir.equals( ExitDir.N ))
		{
			// portal center, minus half portal, minus wall size (0.5f)
			xpos = startPortal.centerX - width * 0.5f - 0.5f;
			ypos = startPortal.centerY;
			
			xsize = width + 1.0f; // width + 2 * wall size
			ysize = length;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX;
			endPortal.centerY = startPortal.centerY + length;
		}
		if( startExitDir.equals( ExitDir.S ))
		{
			xpos = startPortal.centerX - width * 0.5f - 0.5f;
			ypos = startPortal.centerY - length;
			
			xsize = width + 1.0f; // 2 * wall size
			ysize = length;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX;
			endPortal.centerY = startPortal.centerY - length;
		}
		
		if( startExitDir.equals( ExitDir.E ))
		{
			xpos = startPortal.centerX;
			ypos = startPortal.centerY - width * 0.5f - 0.5f;
			
			xsize = length;
			ysize = width + 1.0f;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX + length;
			endPortal.centerY = startPortal.centerY;
		}
		
		if( startExitDir.equals( ExitDir.W ))
		{
			xpos = startPortal.centerX - length;
			ypos = startPortal.centerY - width * 0.5f - 0.5f;
			
			xsize = length;
			ysize = width + 1.0f;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX - length;
			endPortal.centerY = startPortal.centerY;
		}
		
		endPortal.setSource(corr);
		corr.addStartPortal(startExitDir, startPortal);
		corr.addEndPortal(startExitDir, endPortal);
		
		init();
		return corr;
	}
	
	public void init()
	{
		startPortal = null;
		endPortal = null;
		width = 0.0f;
		length = 0.0f;
		
		portals.clear();
	}
	
	public MetaCorridorBuilder createFromPortal( MetaPortal portal )
	{
		this.startPortal = portal;
		
		MetaPortal newEndPortal = new MetaPortal();
		newEndPortal.START_TYPE = MetaPortal.CORRIDOR;
		newEndPortal.setExit(portal.getExit());
		
		newEndPortal.width = portal.width;
		
		this.endPortal = newEndPortal;
			
		return this;
	}
	
	public MetaCorridorBuilder setLength( float length )
	{
		this.length = length;
		return this;
	}
	
}
