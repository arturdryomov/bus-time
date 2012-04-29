package app.android.bustime.local;


import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class TimeTest
{
	@Test
	public void testNormalCreatingFromNumbers() {
		Time firstTime = new Time(10, 40);
		assertEquals("10:40", firstTime.toString());

		Time secondTime = new Time(00, 00);
		assertEquals("00:00", secondTime.toString());

		Time thirdTime = new Time(23, 59);
		assertEquals("23:59", thirdTime.toString());
	}

	@Test(expected = TimeException.class)
	public void testCreatingFromNumbersWithNegativeHours() {
		new Time(-10, 40);
	}

	@Test(expected = TimeException.class)
	public void testCreatingFromNumbersWithNegativeMinutes() {
		new Time(10, -40);
	}

	@Test(expected = TimeException.class)
	public void testCreatingFromNumbersWithHugeHours() {
		new Time(25, 40);
	}

	@Test(expected = TimeException.class)
	public void testCreatingFromNumbersWithHugeMinutes() {
		new Time(10, 65);
	}

	@Test
	public void testNormalCreatingFromString() {
		Time firstTime = new Time("10:40");
		assertEquals("10:40", firstTime.toString());

		Time secondTime = new Time("00:00");
		assertEquals("00:00", secondTime.toString());

		Time thirdTime = new Time("23:59");
		assertEquals("23:59", thirdTime.toString());
	}
}
