package ru.ming13.bustime.provider;

import android.content.UriMatcher;

final class BusTimeUriMatcher
{
	public static final class Codes
	{
		private Codes() {
		}

		public static final int ROUTES = 1;
		public static final int STOPS = 2;

		public static final int ROUTE_STOPS = 3;
		public static final int STOP_ROUTES = 4;

		public static final int ROUTE_TIMETABLE = 5;
		public static final int STOP_TIMETABLE = 6;

		public static final int STOPS_SEARCH = 7;
	}

	private static final class Masks
	{
		private Masks() {
		}

		public static final String NUMBER = "#";
		public static final String SYMBOLS = "*";
	}

	private final BusTimePathsBuilder pathsBuilder;

	public static UriMatcher buildMatcher() {
		return new BusTimeUriMatcher().buildUriMatcher();
	}

	private BusTimeUriMatcher() {
		pathsBuilder = new BusTimePathsBuilder();
	}

	private UriMatcher buildUriMatcher() {
		UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildRoutesPath(), Codes.ROUTES);
		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildStopsPath(), Codes.STOPS);

		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildRouteStopsPath(Masks.NUMBER), Codes.ROUTE_STOPS);
		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildStopRoutesPath(Masks.NUMBER), Codes.STOP_ROUTES);

		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildRouteTimetablePath(Masks.NUMBER, Masks.NUMBER), Codes.ROUTE_TIMETABLE);
		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildStopTimetablePath(Masks.NUMBER, Masks.NUMBER), Codes.STOP_TIMETABLE);

		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildStopsSearchPath(Masks.SYMBOLS), Codes.STOPS_SEARCH);

		return uriMatcher;
	}
}
