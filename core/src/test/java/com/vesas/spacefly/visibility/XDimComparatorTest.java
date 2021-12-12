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

		assertEquals(-1, res);
	}

	@Test
	public void test2()
	{
		XDimComparator comp = new XDimComparator();
		
		EndPoint a = new EndPoint(3,3); 
		EndPoint b = new EndPoint(1,1);
		
		int res = comp.compare(a, b);

		assertEquals(1, res);
	}

	@Test
	public void test3()
	{
		XDimComparator comp = new XDimComparator();
		
		EndPoint a = new EndPoint(5,3); 
		EndPoint b = new EndPoint(5,1);
		
		int res = comp.compare(a, b);

		assertEquals(1, res);
	}

}
