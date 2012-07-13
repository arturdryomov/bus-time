package app.android.bustime.db;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;


public class Station implements Parcelable
{
	private final SQLiteDatabase database;

	private long id;
	private String name;
	private double latitude;
	private double longitude;

	Station(ContentValues databaseValues) {
		database = DbProvider.getInstance().getDatabase();

		setStationValues(databaseValues);
	}

	private void setStationValues(ContentValues databaseValues) {
		Long idAsLong = databaseValues.getAsLong(DbFieldNames.ID);
		if (idAsLong == null) {
			throw new DbException();
		}
		id = idAsLong.longValue();

		String nameAsString = databaseValues.getAsString(DbFieldNames.NAME);
		if (nameAsString == null) {
			throw new DbException();
		}
		name = nameAsString;

		Double latitudeAsDouble = databaseValues.getAsDouble(DbFieldNames.LATITUDE);
		if (latitudeAsDouble == null) {
			throw new DbException();
		}
		latitude = latitudeAsDouble.doubleValue();

		Double longitudeAsDouble = databaseValues.getAsDouble(DbFieldNames.LONGITUDE);
		if (longitudeAsDouble == null) {
			throw new DbException();
		}
		longitude = longitudeAsDouble.doubleValue();
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Time getShiftTimeForRoute(Route route) {
		Cursor databaseCursor = database.rawQuery(buildRouteShiftTimeSelectionQuery(route), null);

		Time shiftTime;

		if (databaseCursor.getCount() == 0) {
			throw new NotExistsException();
		}
		else {
			databaseCursor.moveToFirst();
			shiftTime = new Time(extractTimeFromCursor(databaseCursor));
		}

		databaseCursor.close();

		return shiftTime;
	}

	private String buildRouteShiftTimeSelectionQuery(Route route) {
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("select ");

		queryBuilder.append(String.format("%s ", DbFieldNames.TIME_SHIFT));

		queryBuilder.append(String.format("from %s ", DbTableNames.ROUTES_AND_STATIONS));
		queryBuilder.append(
			String.format("where %s = %d and %s = %d", DbFieldNames.STATION_ID, id, DbFieldNames.ROUTE_ID,
				route.getId()));

		return queryBuilder.toString();
	}

	private String extractTimeFromCursor(Cursor databaseCursor) {
		return databaseCursor.getString(databaseCursor.getColumnIndexOrThrow(DbFieldNames.TIME_SHIFT));
	}

	public List<Time> getTimetableForRoute(Route route) {
		List<Time> timetable = new ArrayList<Time>();

		Time shiftTimeForRoute = getShiftTimeForRoute(route);

		for (Time departureTime : route.getDepartureTimetable()) {
			Time routeTime = departureTime.sum(shiftTimeForRoute);
			timetable.add(routeTime);
		}

		return timetable;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeLong(id);
		parcel.writeString(name);
	}

	public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>()
	{
		@Override
		public Station createFromParcel(Parcel parcel) {
			return new Station(parcel);
		}

		@Override
		public Station[] newArray(int size) {
			return new Station[size];
		}
	};

	private Station(Parcel parcel) {
		database = DbProvider.getInstance().getDatabase();

		readStationDataFromParcel(parcel);
	}

	private void readStationDataFromParcel(Parcel parcel) {
		id = parcel.readLong();
		name = parcel.readString();
	}
}
