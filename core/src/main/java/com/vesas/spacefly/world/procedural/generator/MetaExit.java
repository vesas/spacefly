package com.vesas.spacefly.world.procedural.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.vesas.spacefly.game.G;
import com.vesas.spacefly.world.procedural.generator.MetaRoom.Exits;

public class MetaExit
{
	// center of the exit
	private int x,y;
	
	private List<MetaRoom> rooms = new ArrayList<MetaRoom>();
	private List<Exits> dirs = new ArrayList<Exits>();

	public MetaExit()
	{
		
	}
	
	public void addRoom(  MetaRoom mr )
	{
		Random rand = G.random;
		
		/*
		if( rooms.size() == 0 )
		{
			Exits exit = Exits.getRandom();
			
			int exitX = mr.getPosx() + 2 + rand.nextInt( mr.getWidth() - 4 );
			int exitY = mr.getPosy() + 2 + rand.nextInt( mr.getHeight() - 4 );
			
			if( exit == Exits.EXIT_EAST )
			{
				x = mr.getPosx() - 1;
				y = exitY;	
			}
			else if( exit == Exits.EXIT_WEST )
			{
				x = mr.getPosx() + mr.getWidth() + 1;
				y = exitY;	
			}
			else if( exit == Exits.EXIT_NORTH )
			{
				x = exitX;
				y = mr.getPosy() + mr.getHeight() + 1;	
			}
			else if( exit == Exits.EXIT_SOUTH )
			{
				x = exitX;
				y = mr.getPosy() - 1;	
			}
			
			dirs.add( exit );
		}
		else if( rooms.size() == 1 )
		{	
			int exitX = mr.getPosx() + 2 + rand.nextInt( mr.getWidth() - 4 );
			int exitY = mr.getPosy() + 2 + rand.nextInt( mr.getHeight() - 4 );
			
			Exits exit = dirs.get( 0 );
			
			Exits newExitDir = exit.getOpposite();
			
			dirs.add( newExitDir );
			
			if( newExitDir == Exits.EXIT_EAST )
			{
				int roomPosx = this.x + 1;
				int roomPosy = this.y - exitY;
				
				mr.setPosx(roomPosx);
				mr.setPosy(roomPosy);
			}
			else if( newExitDir == Exits.EXIT_WEST )
			{
				int roomPosx = this.x - 1 - mr.getWidth();
				int roomPosy = this.y - exitY;
				
				mr.setPosx(roomPosx);
				mr.setPosy(roomPosy);
			}
			if( newExitDir == Exits.EXIT_SOUTH )
			{
				int roomPosx = this.x - exitX;
				int roomPosy = this.y - 1 - mr.getHeight();
				
				mr.setPosx(roomPosx);
				mr.setPosy(roomPosy);
			}
			if( newExitDir == Exits.EXIT_NORTH )
			{
				int roomPosx = this.x - exitX;
				int roomPosy = this.y + 1;
				
				mr.setPosx(roomPosx);
				mr.setPosy(roomPosy);
			}
			
		}
		
		rooms.add( mr );
		*/
	}
	
	public void setPos( int x, int y )
	{
		this.x = x;
		this.y = y;
	}
	
	public void stroke( Region region )
	{
		Exits exitDir1 = dirs.get( 0 );
		/*
		if( exitDir1 == Exits.EXIT_EAST || exitDir1 == Exits.EXIT_WEST )
		{
			region.drawHorizontalLine( this.x - 1, this.x + 1, this.y -2 );
			region.drawHorizontalLine( this.x - 1, this.x + 1, this.y + 2 );
			
			region.clearVerticalLine( this.y - 1, this.y + 1, this.x - 1);
			region.clearVerticalLine( this.y - 1, this.y + 1, this.x + 1);
		}
		
		if( exitDir1 == Exits.EXIT_SOUTH || exitDir1 == Exits.EXIT_NORTH )
		{
			region.drawVerticalLine( this.y - 1, this.y + 1, this.x -2 );
			region.drawVerticalLine( this.y - 1, this.y + 1, this.x + 2 );
			
			region.clearHorizontalLine( this.x - 1, this.x + 1, this.y - 1);
			region.clearHorizontalLine( this.x - 1, this.x + 1, this.y + 1);
		}
		*/
		
		MetaRoom another = rooms.get( 1 );
		
		another.stroke( region );
	}
}
