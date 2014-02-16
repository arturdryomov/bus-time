package ru.ming13.bustime.provider;

import android.app.SearchManager;

final class BusTimePathsBuilder
{
	private static final class Segments
	{
		private Segments() {
		}

		public static final String ROUTES = "routes";
		public static final String STOPS = "stops";
	}

	public String buildRoutesPath() {
		return Segments.ROUTES;
	}

	public String buildStopsPath() {
		return Segments.STOPS;
	}

	public String buildRouteStopsPath(String routeNumber) {
		return String.format("%s/%s/%s", Segments.ROUTES, routeNumber, Segments.STOPS);
	}

	public String buildStopRoutesPath(String stopNumber) {
		return String.format("%s/%s/%s", Segments.STOPS, stopNumber, Segments.ROUTES);
	}

	public String buildTimetablePath(String routeNumber, String stopNumber) {
		return String.format("%s/%s/%s/%s", Segments.ROUTES, routeNumber, Segments.STOPS, stopNumber);
	}

	public String buildStopsSearchPath(String searchQuery) {
		return String.format("%s/%s", SearchManager.SUGGEST_URI_PATH_QUERY, searchQuery);
	}
}
