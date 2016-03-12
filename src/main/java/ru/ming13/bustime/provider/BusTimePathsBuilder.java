package ru.ming13.bustime.provider;

import android.app.SearchManager;
import android.support.annotation.NonNull;

final class BusTimePathsBuilder
{
	private static final class Segments
	{
		private Segments() {
		}

		public static final String ROUTES = "routes";
		public static final String STOPS = "stops";
	}

	@NonNull
	public String buildRoutesPath() {
		return Segments.ROUTES;
	}

	@NonNull
	public String buildStopsPath() {
		return Segments.STOPS;
	}

	@NonNull
	public String buildRouteStopsPath(String routeNumber) {
		return String.format("%s/%s/%s", Segments.ROUTES, routeNumber, Segments.STOPS);
	}

	@NonNull
	public String buildStopRoutesPath(String stopNumber) {
		return String.format("%s/%s/%s", Segments.STOPS, stopNumber, Segments.ROUTES);
	}

	@NonNull
	public String buildTimetablePath(String routeNumber, String stopNumber) {
		return String.format("%s/%s/%s/%s", Segments.ROUTES, routeNumber, Segments.STOPS, stopNumber);
	}

	@NonNull
	public String buildStopsSearchPath(String searchQuery) {
		return String.format("%s/%s", SearchManager.SUGGEST_URI_PATH_QUERY, searchQuery);
	}
}
