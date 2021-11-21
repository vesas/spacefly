package com.vesas.spacefly.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class XDimComparatorTest
{

	@Test
	public void test()
	{
		
		XDimComparator comp = new XDimComparator();
		
		EndPoint a = new EndPoint(1,2); 
		EndPoint b = new EndPoint(2,3);
		
		int res = comp.compare(a, b);

		// assertEquals(5, res);
		
	}

}
