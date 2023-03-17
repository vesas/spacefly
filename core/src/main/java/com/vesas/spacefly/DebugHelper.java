package com.vesas.spacefly;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.vesas.spacefly.box2d.Box2DWorld;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.game.Screen;

public class DebugHelper
{
	static private String GC_STAT_1 = "";
	static private String GC_STAT_2 = "";
	
	static private String FPS = "";
	static private int counter = 30;
	
	static public boolean BOX2D_DEBUG = false;
	static public boolean GC_DEBUG = false;
	static public boolean VISIB_DEBUG = false;
	static public boolean PLAYER_DEBUG = false;

	public static long totalGarbageCollections = 0;
	public static long garbageCollectionTime = 0;

	static public void calcGCStats() 
	{
		// if( !DebugHelper.GC_DEBUG )
			// return;

		totalGarbageCollections = 0;
		garbageCollectionTime = 0;

		for(GarbageCollectorMXBean gc :
			ManagementFactory.getGarbageCollectorMXBeans()) {

			long count = gc.getCollectionCount();

			if(count >= 0) {
				totalGarbageCollections += count;
			}

			long time = gc.getCollectionTime();

			if(time >= 0) {
				garbageCollectionTime += time;
			}
		}

		GC_STAT_1 = "Total Garbage Collection Time (ms): " + garbageCollectionTime;
		GC_STAT_2 = "Total Garbage Collections: " + totalGarbageCollections;

	}
	
	static public void render( Screen screen )
	{
		if( DebugHelper.BOX2D_DEBUG )
			Box2DWorld.renderDebug( screen.camera.combined );
		
		if( counter-- <= 0 )
		{
			FPS = "FPS: " + Gdx.graphics.getFramesPerSecond();
			
			DebugHelper.calcGCStats();

			counter = 60;
		}
		
		G.wFont.getData().setScale(0.45f, 0.45f);
		G.wFont.draw( screen.screenBatch, FPS, 20 ,Gdx.graphics.getHeight() - 40 );
	}
	

	static public void printGCStats( SpriteBatch screenBatch ) 
	{
		if( !DebugHelper.GC_DEBUG )
		{
			return;
		}
	    
		G.font.draw( screenBatch, GC_STAT_1, 10,270);
		G.font.draw( screenBatch, GC_STAT_2, 10,250);
	}
}
