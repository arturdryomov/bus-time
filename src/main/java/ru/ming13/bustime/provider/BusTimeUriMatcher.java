package ru.ming13.bustime.provider;

import android.content.UriMatcher;

final class BusTimeUriMatcher
{
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
			pathsBuilder.buildStationsPath(), Codes.STATIONS);

		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildRouteStationsPath(Masks.NUMBER), Codes.ROUTE_STATIONS);
		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildStationRoutesPath(Masks.NUMBER), Codes.STATION_ROUTES);

		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildRouteTimetablePath(Masks.NUMBER, Masks.NUMBER), Codes.ROUTE_TIMETABLE);
		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildStationTimetablePath(Masks.NUMBER, Masks.NUMBER), Codes.STATION_TIMETABLE);

		uriMatcher.addURI(BusTimeContract.AUTHORITY,
			pathsBuilder.buildStationSearchPath(Masks.SYMBOLS), Codes.STATIONS_SEARCH);

		return uriMatcher;
	}
}
