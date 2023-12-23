package com.vesas.spacefly.monster;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.game.G;

public class ZipperCloud
{

	private Array<ZipperMonster> zippers = new Array<ZipperMonster>();
	
	private Vector2 center = new Vector2();
	
	public void addMonster( ZipperMonster mon )
	{
		zippers.add( mon );
	}
	
	public void remove( ZipperMonster mon )
	{
		zippers.removeValue( mon , true );
		
		if( zippers.size == 0 )
			ZipperCloudManager.removeCloud( this );
	}
	
	public Vector2 getCenter()
	{
		center.x = 0.0f;
		center.y = 0.0f;
		
		for( int i = 0 ;i < zippers.size; i++ )
		{
			ZipperMonster mon = zippers.get(i);
			
			Vector2 monCenter = mon.getBody().getWorldCenter();
			
			center.x += monCenter.x;
			center.y += monCenter.y;
		}
		
		center.x = center.x / zippers.size;
		center.y = center.y / zippers.size;
		
		return center;
	}
	
	public void draw(GameScreen screen)
	{
		Vector2 cent = getCenter();
		
		screen.worldBatch.end();

		G.shapeRenderer.begin(ShapeType.Filled);
		G.shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.1f);
		G.shapeRenderer.circle( cent.x, cent.y, 0.5f);
		G.shapeRenderer.end();
		screen.worldBatch.begin();
	}
}
