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
		corr.setSize( length, width );
		
		float xpos, ypos, xsize, ysize;
		
		ExitDir startExitDir = startPortal.exit;
		
		if( startExitDir.equals( ExitDir.N ))
		{
			xpos = startPortal.centerX - width * 0.5f;
			ypos = startPortal.centerY - length;
			
			xsize = width;
			ysize = length;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX;
			endPortal.centerY = startPortal.centerY - length;
		}
		if( startExitDir.equals( ExitDir.S ))
		{
			xpos = startPortal.centerX - width * 0.5f;
			ypos = startPortal.centerY;
			
			xsize = width;
			ysize = length;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX;
			endPortal.centerY = startPortal.centerY + length;
		}
		
		if( startExitDir.equals( ExitDir.E ))
		{
			xpos = startPortal.centerX - length;
			ypos = startPortal.centerY - width * 0.5f;
			
			xsize = length;
			ysize = width;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX - length;
			endPortal.centerY = startPortal.centerY;
		}
		
		if( startExitDir.equals( ExitDir.W ))
		{
			xpos = startPortal.centerX;
			ypos = startPortal.centerY - width * 0.5f;
			
			xsize = length;
			ysize = width;
			
			corr.setSize(xpos, ypos, xsize, ysize);
			
			endPortal.centerX = startPortal.centerX + length;
			endPortal.centerY = startPortal.centerY;
		}
		
		corr.addStartPortal(startExitDir, startPortal);
		corr.addEndPortal(startExitDir.getOpposite(), endPortal);
		
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
		MetaPortal newStartPortal = new MetaPortal();
		newStartPortal.exit = portal.exit.getOpposite();
		
		newStartPortal.centerX = portal.centerX;
		newStartPortal.centerY = portal.centerY;
		newStartPortal.width = portal.width;
		
		this.startPortal = newStartPortal;
		
		MetaPortal newEndPortal = new MetaPortal();
		newEndPortal.exit = portal.exit;
		
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
