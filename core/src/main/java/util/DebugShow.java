package util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import com.vesas.spacefly.GameScreen;
import com.vesas.spacefly.game.G;

public class DebugShow
{
	static public boolean debug = false; 
	
	static private Deque<String> strings = new ArrayDeque<String>();
	int pos = 0;

	public static void draw(GameScreen screen)
	{
		if( !debug )
			return;
		
		Iterator<String> it = strings.iterator();
		int i = 0;
		while( it.hasNext() )
		{
			String txt = it.next();	
			G.font.draw(screen.screenBatch , txt, 15, 15 + i * 20);
			i++;
		}
		
	}
	
	public static void add( String txt )
	{
		strings.addLast(txt);
		
		if( strings.size() > 45 )
			strings.removeFirst();
	}
}
