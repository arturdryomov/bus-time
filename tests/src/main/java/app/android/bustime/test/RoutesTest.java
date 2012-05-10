package app.android.bustime.test;


import java.util.List;

import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;


public class RoutesTest extends DbTestCase
{
	public void testRouteCreation() {
		Route route = routes.createRoute(ROUTE_10_NAME);

		assertEquals(ROUTE_10_NAME, route.getName());
		assertEquals(1, routes.getRoutesList().size());
	}

	public void testRouteDeleting() {
		Route route = routes.createRoute(ROUTE_10_NAME);

		routes.deleteRoute(route);

		assertEquals(0, routes.getRoutesList().size());
	}

	public void testGetRoutesList() {
		fillDatabaseWithRoutes();

		List<Route> routesList = routes.getRoutesList();

		assertNotNull(routesList);
		assertEquals(ROUTES_COUNT, routesList.size());
	}

	public void testGetRoutesListByStation() {
		fillDatabaseWithRoutes();

		Route route = routes.getRoutesList().get(0);

		Station station = stations.createStation(STATION_MOLODEJNAYA_NAME,
			STATION_MOLODEJNAYA_LATITUDE, STATION_MOLODEJNAYA_LONGITUDE);
		station.insertShiftTimeForRoute(route, new Time(0, 10));

		assertEquals(1, routes.getRoutesList(station).size());
	}
}
