package ru.ming13.bustime.provider;

import android.app.SearchManager;
import android.content.UriMatcher;

final class BusTimePathsBuilder
{
	private static final class Segments
	{
		private Segments() {
		}

		public static final String ROUTES = "routes";
		public static final String STATIONS = "stations";
	}

	public String buildRoutesPath() {
		return Segments.ROUTES;
	}

	public String buildStationsPath() {
		return Segments.STATIONS;
	}

	public String buildRouteStationsPath(String routeNumber) {
		return String.format("%s/%s/%s", Segments.ROUTES, routeNumber, Segments.STATIONS);
	}

	public String buildStationRoutesPath(String stationNumber) {
		return String.format("%s/%s/%s", Segments.STATIONS, stationNumber, Segments.ROUTES);
	}

	public String buildRouteTimetablePath(String routeNumber, String stationNumber) {
		return String.format("%s/%s/%s/%s", Segments.ROUTES, routeNumber, Segments.STATIONS, stationNumber);
	}

	public String buildStationTimetablePath(String routeNumber, String stationNumber) {
		return String.format("%s/%s/%s/%s", Segments.STATIONS, stationNumber, Segments.ROUTES, routeNumber);
	}

	public String buildStationSearchPath(String searchQuery) {
		return String.format("%s/%s", SearchManager.SUGGEST_URI_PATH_QUERY, searchQuery);
	}
}
