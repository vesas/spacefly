package com.vesas.spacefly.world.procedural.room;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.vesas.spacefly.game.G;
import com.vesas.spacefly.world.procedural.Decal;
import com.vesas.spacefly.world.procedural.Feature;
import com.vesas.spacefly.world.procedural.FloorPatina;
import com.vesas.spacefly.world.procedural.GenSeed;
import com.vesas.spacefly.world.procedural.generator.MetaPortal;
import com.vesas.spacefly.world.procedural.generator.RoomRole;
import com.vesas.spacefly.world.procedural.room.rectangleroom.Exit;
import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

public abstract class RoomFeature implements Feature
{
	protected float xpos;
	protected float ypos;
	
	protected float width;
	protected float height;

	// Which purpose this room serves. Drives the per-room ambient-light mood, read by the
	// live world from whichever room the player currently stands in.
	protected RoomRole role = RoomRole.STANDARD;

	public RoomRole getRole() { return role; }
	public void setRole( RoomRole role ) { this.role = role; }

	// --- Per-room floor wear: an organic grime overlay pooled toward the interior, fading to nothing
	// at the edges so doorways stay seamless. Assigned once at floor generation
	// (WorldGen.assignFloorPatina), drawn over the floor in drawWithVisibility. The surface *material*
	// is baked straight into the floor pixmap (see FloorPatina.bakeMaterial), not drawn here.
	private boolean hasWear = false;
	private int wearVariant;
	private float wearR = 1f, wearG = 1f, wearB = 1f, wearA = 0f;
	private boolean wearFlipX = false, wearFlipY = false;

	// Normalized distance of this room from the floor start (0 at entrance .. 1 at far exit). Drives
	// the floor-wide ambient mood drift (see ProceduralGameWorld.updateAmbient).
	private float depth01 = 0f;

	public float getDepth01() { return depth01; }
	public void setDepth01( float d ) { this.depth01 = d; }

	public void setPatina( FloorPatina.Pick pick )
	{
		this.hasWear = true;
		this.wearVariant = pick.wearVariant;
		this.wearR = pick.wearR; this.wearG = pick.wearG; this.wearB = pick.wearB; this.wearA = pick.wearA;
		this.wearFlipX = pick.wearFlipX; this.wearFlipY = pick.wearFlipY;
	}

	/** Draw the wear overlay over one floor quad. No-op if unassigned. */
	protected void drawFloorOverlays( Batch batch, float x, float y, float w, float h )
	{
		if( !hasWear || w <= 0f || h <= 0f )
			return;
		TextureRegion reg = FloorPatina.wearRegion( wearVariant );
		// Mirror the shared region for this room, then restore it (single-threaded draw).
		if( wearFlipX || wearFlipY )
			reg.flip( wearFlipX, wearFlipY );
		batch.setColor( wearR, wearG, wearB, wearA );
		batch.draw( reg, x, y, w, h );
		batch.setColor( 1f, 1f, 1f, 1f );
		if( wearFlipX || wearFlipY )
			reg.flip( wearFlipX, wearFlipY );
	}

	public float getXpos() { return xpos; }
	public float getYpos() { return ypos; }
	
	public float getWidth() { return width; } 
	public float getHeight() { return height; }  
	
	protected Array<Exit> exits = new Array<Exit>();

	private Array<Decal> decals = new Array<Decal>();

	public void addDecals( Array<Decal> d ) { decals.addAll( d ); }

	protected void drawDecals( Batch batch ) {
		for( int i = 0; i < decals.size; i++ )
			decals.get(i).draw( batch );
	}

	// World positions of vents that can serve as monster spawn origins.
	private Array<float[]> ventSpawns = new Array<float[]>();

	public void addVentSpawn( float x, float y ) { ventSpawns.add( new float[]{ x, y } ); }

	/** A random vent spawn point (x,y), or null if this room has no vents. */
	public float[] randomVentSpawn() {
		if( ventSpawns.size == 0 ) return null;
		return ventSpawns.get( GenSeed.random.nextInt( ventSpawns.size ) );
	}

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
