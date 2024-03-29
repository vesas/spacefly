package com.vesas.spacefly.game;

public class Util
{
	public static float DEGTORAD = (float) (Math.PI / 180.0f);
	public static float RADTODEG = (float) (180.0f / Math.PI);

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

	static public float floatLerp( float start, float end, float pos )
	{
		return (start + pos*(end - start));
	}
}
