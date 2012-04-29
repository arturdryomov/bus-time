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
		station.insertShiftTimeForRoute(route, new Time("00:20"));
		station.insertShiftTimeForRoute(route, new Time("00:30"));
	}

	public void testGetName() {
		assertEquals(STATION_KALININA_NAME, station.getName());
	}

	public void testSetName() {
		station.setName(STATION_KOPTEVO_NAME);

		assertEquals(STATION_KOPTEVO_NAME, station.getName());
	}

	public void testGetTimetableForRoute() {
		List<Time> timetableForRoute = station.getTimetableForRoute(route);

		assertNotNull(timetableForRoute);
		assertEquals(3, timetableForRoute.size());
	}

	public void testRemoveShiftTimeForRoute() {
		station.removeShiftTimeForRoute(route, new Time("00:10"));

		assertEquals(2, station.getTimetableForRoute(route).size());
	}
}
