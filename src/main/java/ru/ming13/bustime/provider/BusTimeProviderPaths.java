package ru.ming13.bustime.provider;

import android.app.SearchManager;
import android.content.UriMatcher;

final class BusTimeProviderPaths
{
	private BusTimeProviderPaths() {
	}

	public static final String ROUTES;
	public static final String STATIONS;

	public static final String STATION_ROUTES;
	public static final String ROUTE_STATIONS;

	public static final String STATION_TIMETABLE;
	public static final String ROUTE_TIMETABLE;

	public static final String STATIONS_SEARCH;

	static {
		ROUTES = Segments.ROUTES;
		STATIONS = Segments.STATIONS;

		ROUTE_STATIONS = appendUriPath(appendUriPath(ROUTES, Segments.NUMBER), Segments.STATIONS);
		STATION_ROUTES = appendUriPath(appendUriPath(STATIONS, Segments.NUMBER), Segments.ROUTES);

		ROUTE_TIMETABLE = appendUriPath(ROUTE_STATIONS, Segments.NUMBER);
		STATION_TIMETABLE = appendUriPath(STATION_ROUTES, Segments.NUMBER);

		STATIONS_SEARCH = appendUriPath(SearchManager.SUGGEST_URI_PATH_QUERY, Segments.SYMBOL);
	}

	public static final class Segments
	{
		private Segments() {
		}

		public static final String NUMBER = "#";
		public static final String SYMBOL = "*";

		public static final String ROUTES = "routes";
		public static final String STATIONS = "stations";
	}

	private static String appendUriPath(String uri, String uriPath) {
		return String.format("%s/%s", uri, uriPath);
	}

	public static UriMatcher buildUriMatcher() {
		UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(BusTimeContract.AUTHORITY, ROUTES, Codes.ROUTES);
		uriMatcher.addURI(BusTimeContract.AUTHORITY, STATIONS, Codes.STATIONS);

		uriMatcher.addURI(BusTimeContract.AUTHORITY, ROUTE_STATIONS, Codes.ROUTE_STATIONS);
		uriMatcher.addURI(BusTimeContract.AUTHORITY, STATION_ROUTES, Codes.STATION_ROUTES);

		uriMatcher.addURI(BusTimeContract.AUTHORITY, ROUTE_TIMETABLE, Codes.ROUTE_TIMETABLE);
		uriMatcher.addURI(BusTimeContract.AUTHORITY, STATION_TIMETABLE, Codes.STATION_TIMETABLE);

		uriMatcher.addURI(BusTimeContract.AUTHORITY, STATIONS_SEARCH, Codes.STATIONS_SEARCH);

		return uriMatcher;
	}

	public static final class Codes
	{
		private Codes() {
		}

		public static final int ROUTES = 1;
		public static final int STATIONS = 2;

		public static final int ROUTE_STATIONS = 3;
		public static final int STATION_ROUTES = 4;

		public static final int ROUTE_TIMETABLE = 5;
		public static final int STATION_TIMETABLE = 6;

		public static final int STATIONS_SEARCH = 7;
	}
}
