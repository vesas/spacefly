package com.vesas.spacefly.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

public class Log
{
	static BufferedWriter writer = null;

	public static void debug( String msg ) {

		try
		{
			if(writer == null) {
				writer = new BufferedWriter(new FileWriter("./log.debug", false));
			}
			writer.append(msg + "\n");
			writer.flush();
		}
		catch(IOException e) {
		}
	}

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
