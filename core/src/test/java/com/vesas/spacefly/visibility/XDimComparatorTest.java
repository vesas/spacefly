package com.vesas.spacefly.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class XDimComparatorTest
{

	@Test
	public void returnsNegativeWhenFirstXLessThanSecond() {
		final XDimComparator comp = new XDimComparator();

		final EndPoint a = new EndPoint(1,2);
		final EndPoint b = new EndPoint(2,3);

		final int res = comp.compare(a, b);

		assertEquals(-1, res);
	}

	@Test
	public void returnsPositiveWhenFirstXGreaterThanSecond() {
		final XDimComparator comp = new XDimComparator();

		final EndPoint a = new EndPoint(3,3);
		final EndPoint b = new EndPoint(1,1);

		final int res = comp.compare(a, b);

		assertEquals(1, res);
	}

	@Test
	public void returnsPositiveWhenXsEqualButFirstYGreater() {
		final XDimComparator comp = new XDimComparator();

		final EndPoint a = new EndPoint(5,3);
		final EndPoint b = new EndPoint(5,1);

		final int res = comp.compare(a, b);

		assertEquals(1, res);
	}

	@Test
	public void returnsNegativeWhenXsEqualButFirstYLessThanSecond() {
		final XDimComparator comp = new XDimComparator();

		final EndPoint a = new EndPoint(5,1);
		final EndPoint b = new EndPoint(5,3);

		final int res = comp.compare(a, b);

		assertEquals(-1, res);
	}

}
