package ru.ming13.bustime.db.sqlite;


import android.provider.BaseColumns;


public class DbSchema
{
	private DbSchema() {
	}

	public static final class Tables
	{
		private Tables() {
		}

		public static final String ROUTES = "Routes";
		public static final String TRIPS = "Trips";
		public static final String TRIP_TYPES = "TripTypes";
		public static final String STATIONS = "Stations";
		public static final String ROUTES_AND_STATIONS = "RoutesAndStations";
	}

	public static final class RoutesColumns implements BaseColumns
	{
		private RoutesColumns() {
		}

		public static final String NAME = "name";
	}

	public static final class TripTypesColumns implements BaseColumns
	{
		private TripTypesColumns() {
		}

		public static final String NAME = "name";
	}

	public static final class TripTypesColumnsValues
	{
		private TripTypesColumnsValues() {
		}

		public static final int FULL_WEEK_ID = 0;
		public static final int WORKDAY_ID = 1;
		public static final int WEEKEND_ID = 2;
	}

	public static final class TripsColumns implements BaseColumns
	{
		private TripsColumns() {
		}

		public static final String TRIP_TYPE_ID = "trip_type_id";
		public static final String ROUTE_ID = "route_id";
		public static final String DEPARTURE_TIME = "departure_time";
	}

	public static final class StationsColumns implements BaseColumns
	{
		private StationsColumns() {
		}

		public static final String NAME = "name";
		public static final String LONGITUDE = "longitude";
		public static final String LATITUDE = "latitude";
	}

	public static final class RoutesAndStationsColumns implements BaseColumns
	{
		private RoutesAndStationsColumns() {
		}

		public static final String ROUTE_ID = "route_id";
		public static final String STATION_ID = "station_id";
		public static final String TIME_SHIFT = "time_shift";
	}
}
