package util;

public class PolyUtils
{

	public static float perpDot( float ax, float ay, float bx, float by )
	{
		return -ay * bx + ax * by;

	}
	
	/**
	 * Two vectors, 1 and 2, is 2 on the right(clockwise) of 1?
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	static public boolean isClockwise( float x1, float y1, float x2, float y2 )
	{
		return x2 * -y1 + y2 * x1 <= 0.0f;
	}
	
	/**
	 * * Two vectors, 1 and 2, is 2 on the left(counterclockwise) of 1?
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	static public boolean isCounterClockwise( float x1, float y1, float x2, float y2 )
	{
		return x2 * -y1 + y2 * x1 > 0.0f;
	}

	static public boolean isCW(float[] v)
	{
		float bax = v[2] - v[0];
		float bay = v[3] - v[1];

		float cbx = v[4] - v[2];
		float cby = v[5] - v[3];

		float d = perpDot(bax, bay, cbx, cby);

		if (d < 0)
		{
			return true;
		} else
		{
			return false;
		}

	}

	static public float[] swapYs(float[] v, float mapY)
	{

		float[] ret = new float[v.length];

		for (int i = 0; i < v.length; i++)
		{
			ret[i] = v[i];

			if (!(i % 2 == 0))
				ret[i] = -ret[i];
		}

		return ret;
	}

	static public float[] swapVertices(float[] v)
	{
		float[] ret = new float[v.length];

		for (int i = 0; i < v.length; i = i + 2)
		{
			ret[v.length - i - 2] = v[i];
			ret[v.length - i - 1] = v[i + 1];
		}

		return ret;
	}
	
}
