package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.provider.BusTimeContract;

public class StopInformationQueryingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final long stopId;

	public static void execute(Context context, long stopId) {
		new StopInformationQueryingTask(context, stopId).execute();
	}

	private StopInformationQueryingTask(Context context, long stopId) {
		this.contentResolver = context.getContentResolver();
		this.stopId = stopId;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return buildStopInformationQueriedEvent();
	}

	private BusEvent buildStopInformationQueriedEvent() {
		Cursor stopsCursor = queryStops();

		try {
			return buildStopInformationQueriedEvent(stopsCursor);
		} finally {
			stopsCursor.close();
		}
	}

	private Cursor queryStops() {
		return contentResolver.query(getStopsUri(), null, null, null, null);
	}

	private BusEvent buildStopInformationQueriedEvent(Cursor stopsCursor) {
		stopsCursor.moveToPosition(getStopPosition(stopsCursor));

		long stopId = getStopId(stopsCursor);
		String stopName = getStopName(stopsCursor);
		String stopDirection = getStopDirection(stopsCursor);

		return new StopSelectedEvent(stopId, stopName, stopDirection);
	}

	private int getStopPosition(Cursor stopsCursor) {
		while (stopsCursor.moveToNext()) {
			if (getStopId(stopsCursor) == stopId) {
				return stopsCursor.getPosition();
			}
		}

		throw new RuntimeException();
	}

	private long getStopId(Cursor stopsCursor) {
		return stopsCursor.getLong(
			stopsCursor.getColumnIndex(BusTimeContract.Stops._ID));
	}

	private String getStopName(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.NAME));
	}

	private String getStopDirection(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.DIRECTION));
	}

	private Uri getStopsUri() {
		return BusTimeContract.Stops.buildStopsUri();
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
