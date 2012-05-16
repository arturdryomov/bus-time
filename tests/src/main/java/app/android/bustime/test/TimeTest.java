package app.android.bustime.test;


import android.test.AndroidTestCase;
import app.android.bustime.local.Time;
import app.android.bustime.local.TimeException;


public class TimeTest extends AndroidTestCase
{
	public void testNormalCreatingFromNumbers() {
		Time firstTime = new Time(10, 40);
		assertEquals("10:40", firstTime.toString());

		Time secondTime = new Time(00, 00);
		assertEquals("00:00", secondTime.toString());

		Time thirdTime = new Time(23, 59);
		assertEquals("23:59", thirdTime.toString());
	}

	public void testCreatingFromNumbersWithNegativeHours() {
		try {
			new Time(-10, 40);

			fail();
		}
		catch (TimeException e) {
		}
	}

	public void testCreatingFromNumbersWithHugeHours() {
		try {
			new Time(25, 40);

			fail();
		}
		catch (TimeException e) {
		}
	}

	public void testCreatingFromNumbersWithHugeMinutes() {
		try {
			new Time(10, 65);

			fail();
		}
		catch (TimeException e) {
		}
	}

	public void testNormalCreatingFromString() {
		Time firstTime = new Time("10:40");
		assertEquals("10:40", firstTime.toString());

		Time secondTime = new Time("00:00");
		assertEquals("00:00", secondTime.toString());

		Time thirdTime = new Time("23:59");
		assertEquals("23:59", thirdTime.toString());
	}

	public void testGetHours() {
		Time time = new Time("10:40");

		assertEquals(10, time.getHours());
	}

	public void testGetMinutes() {
		Time time = new Time("10:40");

		assertEquals(40, time.getMinutes());
	}

	public void testSum() {
		Time firstTime = new Time("10:40");
		Time secondTime = new Time("00:30");

		assertEquals("11:10", firstTime.sum(secondTime).toString());
	}

	public void testIsAfter() {
		Time firstTime = new Time("10:40");
		Time secondTime = new Time("10:30");

		assertTrue(firstTime.isAfter(secondTime));
	}

	public void testEquals() {
		Time firstTime = new Time("10:40");
		Time secondTime = new Time("10:40");

		assertTrue(firstTime.equals(secondTime));
	}
}
