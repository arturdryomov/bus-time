package app.android.bustime.test;


import android.test.AndroidTestCase;
import app.android.bustime.db.time.Time;


public class TimeTest extends AndroidTestCase
{
	public void testParse() {
		Time time = Time.parse("10:00");
		assertEquals("10:00", time.toDatabaseString());

		time = Time.parse("11:20");
		assertEquals("11:20", time.toDatabaseString());

		time =  Time.parse("23:59");
		assertEquals("23:59", time.toDatabaseString());
	}

	public void testSum() {
		Time firstTime = Time.parse("10:00");
		Time secondTime = Time.parse("00:20");
		assertEquals("10:20", firstTime.sum(secondTime).toDatabaseString());

		firstTime = Time.parse("12:53");
		secondTime = Time.parse("00:29");
		assertEquals("13:22", firstTime.sum(secondTime).toDatabaseString());
	}

	public void testIsAfter() {
		Time timeBefore = Time.parse("10:00");
		Time timeAfter = Time.parse("23:00");

		assertTrue(timeAfter.isAfter(timeBefore));
		assertFalse(timeBefore.isAfter(timeAfter));
	}
}
