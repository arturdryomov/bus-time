package app.android.bustime.test;


import java.util.List;

import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;

public class StationTest extends DbTestCase
{
	private Station station;
	private Route route;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		station = stations.createStation(STATION_KALININA_NAME);
		route = routes.createRoute(ROUTE_10_NAME);

		station.insertShiftTimeForRoute(route, new Time("00:10"));
		route.insertDepartureTime(new Time("10:00"));
		route.insertDepartureTime(new Time("12:00"));
	}

	public void testGetName() {
		assertEquals(STATION_KALININA_NAME, station.getName());
	}

	public void testSetName() {
		station.setName(STATION_KOPTEVO_NAME);

		assertEquals(STATION_KOPTEVO_NAME, station.getName());
	}

	public void testGetShiftTimeForRoute() {
		Time shiftTimeForRoute = station.getShiftTimeForRoute(route);

		assertNotNull(shiftTimeForRoute);
		assertEquals("00:10", station.getShiftTimeForRoute(route).toString());
	}

	public void testGetTimetable() {
		List<Time> timetable = station.getTimetableForRoute(route);

		assertEquals("10:10", timetable.get(0).toString());
		assertEquals("12:10", timetable.get(1).toString());
	}

	public void testRemoveShiftTimeForRoute() {
		station.removeShiftTimeForRoute(route, new Time("00:10"));

		assertEquals("00:00", station.getShiftTimeForRoute(route).toString());
	}
}
