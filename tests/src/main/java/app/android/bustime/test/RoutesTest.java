package app.android.bustime.test;


import java.util.List;

import app.android.bustime.local.Route;


public class RoutesTest extends DbTestCase
{
	public void testGetRoutesList() {
		fillDatabaseWithRoutes();

		List<Route> routesList = routes.getRoutesList();

		assertNotNull(routesList);
		assertEquals(ROUTES_COUNT, routesList.size());
	}

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
}
