package com.vesas.spacefly.monster;

import com.badlogic.gdx.utils.Array;

public class ZipperCloudManager
{	
	static private Array<ZipperCloud> clouds = new Array<ZipperCloud>();
	
	static public void add( ZipperCloud cloud )
	{
		clouds.add( cloud );
	}
	
	static public void removeCloud( ZipperCloud cloud )
	{
		clouds.removeValue( cloud, true);
	}
}
