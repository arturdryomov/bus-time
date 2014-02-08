package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopLoadedEvent;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;

public class StopLoadingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final long stopId;

	public static void execute(Context context, long stopId) {
		new StopLoadingTask(context, stopId).execute();
	}

	private StopLoadingTask(Context context, long stopId) {
		this.contentResolver = context.getContentResolver();
		this.stopId = stopId;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return new StopLoadedEvent(loadStop());
	}

	private Stop loadStop() {
		Cursor stopsCursor = loadStops();

		try {
			return getStop(stopsCursor);
		} finally {
			stopsCursor.close();
		}
	}

	private Cursor loadStops() {
		return contentResolver.query(getStopsUri(), null, null, null, null);
	}

	private Uri getStopsUri() {
		return BusTimeContract.Stops.getStopsUri();
	}

	private Stop getStop(Cursor stopsCursor) {
		stopsCursor.moveToPosition(getStopPosition(stopsCursor));

		String stopName = getStopName(stopsCursor);
		String stopDirection = getStopDirection(stopsCursor);

		return new Stop(stopId, stopName, stopDirection);
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

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
