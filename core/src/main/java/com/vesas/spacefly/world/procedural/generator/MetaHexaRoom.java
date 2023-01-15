package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaHexaRoom implements MetaFeature {

    private Rectangle rect = new Rectangle();

    private int id;
    private float radius;

	public MetaHexaRoom() 
	{ 
		this.id = IDGenerator.getNextId();
	}

    public void setSize( float radius, float posx, float posy, float w, float h )
	{
        this.radius = radius;

		rect.x = posx;
		rect.y = posy;
		
		rect.width = w;
		rect.height = h;
	}
	
    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean overlaps(Rectangle rect) {
        return rect.contains(this.rect) || this.rect.contains(rect) || this.rect.overlaps( rect );
    }

    @Override
    public Rectangle getBounds() {
        // TODO Auto-generated method stub
        return rect;
    }

    @Override
    public Array<MetaPortal> getPortalArray(MetaPortal exclude) {
        // TODO Auto-generated method stub

        return new Array<MetaPortal>();
    }

    @Override
    public void closePortal(MetaPortal portal) {
        // TODO Auto-generated method stub
        
    }

    public ObjectMap<ExitDir, MetaPortal> getPortals()
	{
		return null;
	}
	
	public void setSize( float w, float h )
	{
		rect.width = w;
		rect.height = h;
	}
	
	public void addPortal( ExitDir exitDir, MetaPortal portal )
	{
        return;
	}
	
	public MetaPortal getPortal( ExitDir exitDir )
	{
        return null;
	}
    
}
