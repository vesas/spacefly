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
	
	/**
	 * Returns the difference between two angles in degrees
	 * @param target Target angle in degrees
	 * @param source Starting angle in degrees
	 * @return Difference between the two angles in degrees
	 */
	static public float angleDiff( float target, float source )
	{
		float angleDiff = target - source;
		
		while ( angleDiff < -180 ) angleDiff += 360;
		while ( angleDiff >  180 ) angleDiff -= 360;

		return angleDiff;
	}

	/**
	 * Linearly interpolates between two values
	 * @param start Starting value
	 * @param end Ending value
	 * @param pos Interpolation position (0 to 1)
	 * @return Interpolated value
	 */
	static public float floatLerp( float start, float end, float pos )
	{
		return (start + pos*(end - start));
	}
}
