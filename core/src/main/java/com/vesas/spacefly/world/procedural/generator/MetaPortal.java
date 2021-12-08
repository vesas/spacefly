package com.vesas.spacefly.world.procedural.generator;

import com.vesas.spacefly.world.procedural.room.rectangleroom.ExitDir;

// feature exit
public class MetaPortal
{
	public static int RECTANGLE_ROOM = 0;
	public static int CORRIDOR = 1;
	
	public int START_TYPE = 0;
	
	private int id;
	
	private ExitDir exit;

	// Source so that we can remove this portal if it doesn't fit to the world
	private MetaFeature source;

	private MetaFeature target;
	
	public float centerX;
	public float centerY;
	
	public float width;

	public MetaPortal() 
	{  
		this.id = IDGenerator.getNextId();
	}

	public MetaFeature getSource() {
		return source;
	}

	public void setSource(MetaFeature source) {
		this.source = source;
	}

	public int getId() {
		return id;
	}

	public ExitDir getExit() {
		return exit;
	}

	public void setExit(ExitDir exit) {
		this.exit = exit;
	}

	public MetaFeature getTarget() {
		return target;
	}

	public void setTarget(MetaFeature target) {
		this.target = target;
	}

	public float getCenterX() {
		return centerX;
	}

	public void setCenterX(float centerX) {
		this.centerX = centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public void setCenterY(float centerY) {
		this.centerY = centerY;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		buf.append("MetaPortal(id:");
		buf.append(id);
		buf.append(",exit:");
		buf.append(this.exit);
		buf.append(",sourceID:");
		if(source != null)
			buf.append(source.getId());
		else
			buf.append("null");
		buf.append(")");

		buf.append(",targetID:");
		if(target != null)
			buf.append(target.getId());
		else
			buf.append("null");
		buf.append(")");

		return buf.toString();
	}
}
