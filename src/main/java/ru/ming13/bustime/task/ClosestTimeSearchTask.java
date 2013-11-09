package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.ClosestTimeFoundEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public class ClosestTimeSearchTask extends AsyncTask<Void, Void, BusEvent>
{
	private static final int DEFAULT_TIME_POSITION = 0;

	private final ContentResolver contentResolver;
	private final Uri timetableUri;

	public static void execute(Context context, Uri timetableUri) {
		new ClosestTimeSearchTask(context, timetableUri).execute();
	}

	private ClosestTimeSearchTask(Context context, Uri timetableUri) {
		this.contentResolver = context.getContentResolver();
		this.timetableUri = timetableUri;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return new ClosestTimeFoundEvent(getClosestTimePosition());
	}

	private int getClosestTimePosition() {
		Cursor timetableCursor = queryTimetable();

		try {
			return getClosestTimePosition(timetableCursor);
		} finally {
			// This is really strange.
			// If we wouldn’t reset the cursor there
			// the timetable would display a wrong time sometimes.
			timetableCursor.moveToFirst();
			timetableCursor.moveToPrevious();

			timetableCursor.close();
		}
	}

	private Cursor queryTimetable() {
		return contentResolver.query(timetableUri, null, null, null, null);
	}

	private int getClosestTimePosition(Cursor timetableCursor) {
		String currentTime = Time.current().toDatabaseString();

		while (timetableCursor.moveToNext()) {
			if (getTimeString(timetableCursor).compareTo(currentTime) > 0) {
				return timetableCursor.getPosition();
			}
		}

		return DEFAULT_TIME_POSITION;
	}

	private String getTimeString(Cursor timetableCursor) {
		return timetableCursor.getString(
			timetableCursor.getColumnIndex(BusTimeContract.Timetable.ARRIVAL_TIME));
	}

	@Override
	protected void onPostExecute(BusEvent closestTimeFoundEvent) {
		super.onPostExecute(closestTimeFoundEvent);

		BusProvider.getBus().post(closestTimeFoundEvent);
	}
}
