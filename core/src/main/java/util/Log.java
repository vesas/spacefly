package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Log
{
	public static void debug( Exception e )
	{
		File file = null;

		FileOutputStream fos = null; 
		PrintStream ps = null;
		
		try
		{
			file = new File("./log.debug");

			fos = new FileOutputStream(file); 
			ps = new PrintStream( fos );

			e.printStackTrace(ps );
		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			if( ps != null )
				ps.close();		
			
			try
			{
				if( fos != null )
					fos.close();
				
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
}
