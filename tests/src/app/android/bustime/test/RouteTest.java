package app.android.bustime.test;

import java.util.List;

import app.android.bustime.local.Route;
import app.android.bustime.local.Time;


public class RouteTest extends DbTestCase
{
	private Route route;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		route = routes.createRoute(ROUTE_10_NAME);
		route.insertDepartureTime(new Time("00:10"));
		route.insertDepartureTime(new Time("01:20"));
		route.insertDepartureTime(new Time("02:30"));
	}

	public void testGetName() {
		assertEquals(ROUTE_10_NAME, route.getName());
	}

	public void testSetName() {
		route.setName(ROUTE_10A_NAME);

		assertEquals(ROUTE_10A_NAME, route.getName());
	}

	public void testGetDepartureTimetable() {
		List<Time> departureTimetable = route.getDepartureTimetable();

		assertNotNull(departureTimetable);
		assertEquals(3, departureTimetable.size());
	}

	public void testRemoveDepartureTime() {
		route.removeDepartureTime(new Time("00:10"));

		assertEquals(2, route.getDepartureTimetable().size());
	}
}
