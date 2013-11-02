package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StationSelectedEvent;
import ru.ming13.bustime.provider.BusTimeContract;

public class StationInformationQueryingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final long stationId;

	public static void execute(Context context, long stationId) {
		new StationInformationQueryingTask(context, stationId).execute();
	}

	private StationInformationQueryingTask(Context context, long stationId) {
		this.contentResolver = context.getContentResolver();
		this.stationId = stationId;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return buildStationInformationQueriedEvent();
	}

	private BusEvent buildStationInformationQueriedEvent() {
		Cursor stationsCursor = queryStations();

		try {
			return buildStationInformationQueriedEvent(stationsCursor);
		} finally {
			stationsCursor.close();
		}
	}

	private Cursor queryStations() {
		String selection = String.format("%s = ?", BusTimeContract.Stations._ID);
		String[] selectionArguments = {String.valueOf(stationId)};

		return contentResolver.query(getStationsUri(), null, selection, selectionArguments, null);
	}

	private BusEvent buildStationInformationQueriedEvent(Cursor stationsCursor) {
		stationsCursor.moveToPosition(getStationPosition(stationsCursor));

		long stationId = getStationId(stationsCursor);
		String stationName = getStationName(stationsCursor);
		String stationDirection = getStationDirection(stationsCursor);

		return new StationSelectedEvent(stationId, stationName, stationDirection);
	}

	private int getStationPosition(Cursor stationsCursor) {
		while (stationsCursor.moveToNext()) {
			if (getStationId(stationsCursor) == stationId) {
				return stationsCursor.getPosition();
			}
		}

		throw new RuntimeException();
	}

	private long getStationId(Cursor stationsCursor) {
		return stationsCursor.getLong(
			stationsCursor.getColumnIndex(BusTimeContract.Stations._ID));
	}

	private String getStationName(Cursor stationsCursor) {
		return stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.NAME));
	}

	private String getStationDirection(Cursor stationsCursor) {
		return stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.DIRECTION));
	}

	private Uri getStationsUri() {
		return BusTimeContract.Stations.buildStationsUri();
	}

	@Override
	protected void onPostExecute(BusEvent stationInformationQueriedEvent) {
		super.onPostExecute(stationInformationQueriedEvent);

		BusProvider.getBus().post(stationInformationQueriedEvent);
	}
}
