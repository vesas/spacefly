package com.vesas.spacefly.world.procedural.lsystem;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.world.procedural.GenSeed;

public class SimpleWineSystem
{
	static class StackElement
	{
		public Vector2 start = new Vector2();
		public Vector2 prevLevelTip = new Vector2();
		public Vector2 dir = new Vector2();
		public int level = 0;
	}
	
	
	private Vector2 startDir = new Vector2(); 
	private Vector2 startPos = new Vector2();
	
	private int maxLevel = 2;
	private float scaling = 0.50f;
	private float widthScale = 0.33f;
	
	private float dirRandomness = 0.0f;
	
	private Array<StackElement> stack = new Array<StackElement>();
	
	private static SimpleWineSystem INSTANCE = new SimpleWineSystem();
	
	public static SimpleWineSystem getInstance()
	{
		INSTANCE.init();
		return INSTANCE;
	}
	
	private void init()
	{
		stack.clear();
	}
	
	public void setMaxLevel( int maxLev )
	{
		this.maxLevel = maxLev;
	}
	
	public void setStartDir( float x, float y )
	{
		startDir.x = x;
		startDir.y = y;
	}
	
	public void setStartPos( float x, float y )
	{
		startPos.x = x;
		startPos.y = y;
	}
	
	public void setScaling( float scale )
	{
		this.scaling = scale;	
	}
	
	public void setWidthScale( float scale )
	{
		this.widthScale = scale;	
	}
	
	public void setDirRandomness( float r )
	{
		dirRandomness = r;
	}
	
	
	static private Vector2 tmp = new Vector2();
	
	private void renderElement( Pixmap pm, StackElement elem )
	{
		tmp.x = elem.dir.x;
		tmp.y = elem.dir.y;
		
		// negative is clockwise
		tmp.rotate90( -1 );
		tmp.scl( 0.5f );
		tmp.scl( widthScale );
		float tempLen = tmp.len() * 0.1f;
		
		if( elem.level > 0 )
		{
			fillTriangle( pm ,(int)(elem.prevLevelTip.x), (int)(elem.prevLevelTip.y), 
					(int)(elem.prevLevelTip.x + (elem.start.x - elem.prevLevelTip.x) * tempLen), 
					(int)(elem.prevLevelTip.y + (elem.start.y - elem.prevLevelTip.y) * tempLen), 
						(int)(elem.start.x + elem.dir.x), (int)(elem.start.y + elem.dir.y) );	
		}
		else
		{
			fillTriangle( pm , (int)(elem.start.x - tmp.x), (int)(elem.start.y - tmp.y), 
							(int)(elem.start.x + tmp.x), (int)(elem.start.y + tmp.y),
						(int)(elem.start.x + elem.dir.x), (int)(elem.start.y + elem.dir.y) );	
		}
		
		
		if( elem.level >= maxLevel )
		{
			return;
		}
		
		StackElement newElem1 = new StackElement();
		
		newElem1.prevLevelTip.x = elem.start.x + elem.dir.x;
		newElem1.prevLevelTip.y = elem.start.y + elem.dir.y;
		
		newElem1.start.x = elem.start.x + elem.dir.x * 0.89f;
		newElem1.start.y = elem.start.y + elem.dir.y * 0.89f;
		
		tmp.x = elem.dir.x * scaling;
		tmp.y = elem.dir.y * scaling;
		tmp.rotateDeg( ( ( GenSeed.random.nextFloat() - 0.5f) * dirRandomness ) );
		newElem1.dir.x = tmp.x;
		newElem1.dir.y = tmp.y;
		newElem1.level = elem.level + 1;
		
		stack.add( newElem1 );
	}
	
	private void fillTriangle( Pixmap pm, int x1, int y1, int x2, int y2, int x3, int y3 )
	{
		int width = pm.getWidth();
		int height = pm.getHeight();
		
		if( x1 > width || x2 > width || x3 > width )
			return;
		
		if( y1 > height || y2 > height || y3 > height )
			return;
		
		if( x1 < 0 || x2 < 0 || x3 < 0 )
			return;
		
		if( y1 < 0 || y2 < 0 || y3 < 0 )
			return;
		
		pm.fillTriangle( x1,y1, x2,y2,x3,y3);
	}
	
	public void draw( Pixmap pm )
	{
		StackElement elem = new StackElement();
		elem.start.x = startPos.x;
		elem.start.y = startPos.y;
		elem.dir.x = startDir.x;
		elem.dir.y = startDir.y;
		
		tmp.rotateDeg( (GenSeed.random.nextFloat() - 0.5f) * dirRandomness );
		
		elem.level = 0;
		
		stack.add( elem );
		
		while( stack.size > 0 )
		{
			renderElement( pm, stack.pop() );
		}
	}
}
