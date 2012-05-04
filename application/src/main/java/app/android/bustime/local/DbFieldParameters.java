package app.android.bustime.local;

public class DbFieldParameters
{
	public static final String ID = "integer primary key autoincrement not null unique";

	public static final String ROUTE_NAME = "text not null unique";

	public static final String TRIPS_ROUTE_ID = "integer not null references Routes(_id)";
	public static final String TRIPS_DEPARTURE_TIME = "text not null";

	public static final String STATION_NAME = "text not null unique";
	public static final String STATION_LONGITUDE = "text not null";
	public static final String STATION_LATITUDE = "text not null";

	public static final String ROUTES_AND_STATIONS_ROUTE_ID = "integer not null references Routes(_id)";
	public static final String ROUTES_AND_STATIONS_STATION_ID = "integer not null references Stations(_id)";
	public static final String ROUTES_AND_STATIONS_TIME_SHIFT = "text not null";
}
