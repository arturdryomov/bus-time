package ru.ming13.bustime.database;


import android.provider.BaseColumns;


public class DatabaseSchema
{
	private DatabaseSchema() {
	}

	public static final String DATABASE_NAME = "bustime.db";

	public static final class Versions
	{
		private Versions() {
		}

		// Split route number to number and description.
		// Split station name to name and direction.
		// Split times to hours and minutes.
		// Use 24+ hours for trip times.
		public static final int CURRENT = 2;

		public static final int INITIAL = 1;
	}

	public static final class Tables
	{
		private Tables() {
		}

		public static final String ROUTES = "Routes";
		public static final String TRIP_TYPES = "TripTypes";
		public static final String TRIPS = "Trips";
		public static final String STATIONS = "Stations";
		public static final String ROUTES_AND_STATIONS = "RoutesAndStations";
	}

	public static final class RoutesColumns implements BaseColumns
	{
		private RoutesColumns() {
		}

		public static final String NUMBER = "number";
		public static final String DESCRIPTION = "description";
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

		public static final String TYPE_ID = "type_id";
		public static final String ROUTE_ID = "route_id";
		public static final String HOUR = "hour";
		public static final String MINUTE = "minute";
	}

	public static final class StationsColumns implements BaseColumns
	{
		private StationsColumns() {
		}

		public static final String NAME = "name";
		public static final String DIRECTION = "direction";
		public static final String LONGITUDE = "longitude";
		public static final String LATITUDE = "latitude";
	}

	public static final class RoutesAndStationsColumns implements BaseColumns
	{
		private RoutesAndStationsColumns() {
		}

		public static final String ROUTE_ID = "route_id";
		public static final String STATION_ID = "station_id";
		public static final String SHIFT_HOUR = "shift_hour";
		public static final String SHIFT_MINUTE = "shift_minute";
	}
}
