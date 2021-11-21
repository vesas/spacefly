package com.vesas.spacefly.game;

public class Util
{
	/**
	 * returns the absolute closest difference between two angles in degrees
	 * @param a1 angle in degrees
	 * @param a2 angle in degrees
	 * @return
	 */
	static public float absAngleDiff( float a1, float a2 )
	{
		return Math.abs( angleDiff(a1,a2) );
	}
	
	static public float angleDiff( float target, float source )
	{
		float angleDiff = target - source;
		
		while ( angleDiff < -180 ) angleDiff += 360;
		while ( angleDiff >  180 ) angleDiff -= 360;
		
		return angleDiff;
	}
}
