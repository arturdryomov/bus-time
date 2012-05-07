package app.android.bustime.local;

public class DbFieldParameters
{
	public static final String ID = "integer primary key autoincrement not null unique";
	public static final String NAME = "text not null unique";
	public static final String TIME = "text not null";

	public static final String STATION_COORDINATE = "text not null";

	public static final String FOREIGN_ROUTE_ID;
	public static final String FOREIGN_STATION_ID;

	static {
		final String foreignKeyMask = "integer not null references %s(%s)";

		FOREIGN_ROUTE_ID = String.format(foreignKeyMask, DbTableNames.ROUTES, DbFieldNames.ID);
		FOREIGN_STATION_ID = String.format(foreignKeyMask, DbTableNames.STATIONS, DbFieldNames.ID);
	}
}
