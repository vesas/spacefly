package com.vesas.spacefly.world.procedural.generator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public class MetaOctaRoom implements MetaFeature {

    private Rectangle rect = new Rectangle();

    private ObjectMap<ExitDir, MetaPortal> portals = new ObjectMap<ExitDir, MetaPortal>();

    private int id;

	public MetaOctaRoom() 
	{ 
		this.id = IDGenerator.getNextId();
	}

    public void setSize( float posx, float posy, float w, float h )
	{
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
        return rect;
    }

    @Override
    public Array<MetaPortal> getPortalArray(MetaPortal exclude) {
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
    public void closePortal(MetaPortal portal) {
        portals.remove( portal.getExit() );
        
    }

    public ObjectMap<ExitDir, MetaPortal> getPortals()
	{
		return portals;
	}
	
	public void setSize( float w, float h )
	{
		rect.width = w;
		rect.height = h;
	}
	
	public void addPortal( ExitDir exitDir, MetaPortal portal )
	{
        portal.setExit(exitDir);
		portals.put( exitDir, portal );	
	}
	
	public MetaPortal getPortal( ExitDir exitDir )
	{
        return portals.get( exitDir );
	}
    
}
