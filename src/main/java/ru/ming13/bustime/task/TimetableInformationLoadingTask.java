package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.TimetableInformationLoadedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public class TimetableInformationLoadingTask extends AsyncTask<Void, Void, BusEvent>
{
	private static final int DEFAULT_TIME_POSITION = 0;

	private final ContentResolver contentResolver;
	private final Uri timetableUri;

	public static void execute(Context context, Uri timetableUri) {
		new TimetableInformationLoadingTask(context, timetableUri).execute();
	}

	private TimetableInformationLoadingTask(Context context, Uri timetableUri) {
		this.contentResolver = context.getContentResolver();
		this.timetableUri = timetableUri;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		int currentTimetableType = getCurrentTimetableType();
		int closestTripPosition = getClosestTimePosition(currentTimetableType);

		return new TimetableInformationLoadedEvent(currentTimetableType, closestTripPosition);
	}

	private int getCurrentTimetableType() {
		if (!isTimetableWeekPartDependent()) {
			return BusTimeContract.Timetable.Type.FULL_WEEK;
		}

		if (Time.current().isWeekend()) {
			return BusTimeContract.Timetable.Type.WEEKEND;
		} else {
			return BusTimeContract.Timetable.Type.WORKDAYS;
		}
	}

	private boolean isTimetableWeekPartDependent() {
		return getFullWeekTripsCount() == 0;
	}

	private int getFullWeekTripsCount() {
		Cursor timetableCursor = loadFullWeekTimetable();

		try {
			return timetableCursor.getCount();
		} finally {
			timetableCursor.close();
		}
	}

	private Cursor loadFullWeekTimetable() {
		return contentResolver.query(getFullWeekTimetableUri(), null, null, null, null);
	}

	private Uri getFullWeekTimetableUri() {
		return getTimetableUri(BusTimeContract.Timetable.Type.FULL_WEEK);
	}

	private Uri getTimetableUri(int timetableType) {
		return BusTimeContract.Timetable.getTimetableUri(timetableUri, timetableType);
	}

	private int getClosestTimePosition(int timetableType) {
		Cursor timetableCursor = loadTimetable(timetableType);

		try {
			return getClosestTimePosition(timetableCursor);
		} finally {
			timetableCursor.close();
		}
	}

	private Cursor loadTimetable(int timetableType) {
		return contentResolver.query(getTimetableUri(timetableType), null, null, null, null);
	}

	private int getClosestTimePosition(Cursor timetableCursor) {
		String currentTime = Time.current().toDatabaseString();

		while (timetableCursor.moveToNext()) {
			if (isAfter(getTimeString(timetableCursor), currentTime)) {
				return timetableCursor.getPosition();
			}
		}

		return DEFAULT_TIME_POSITION;
	}

	private boolean isAfter(String time, String currentTime) {
		return time.compareTo(currentTime) >= 0;
	}

	private String getTimeString(Cursor timetableCursor) {
		return timetableCursor.getString(
			timetableCursor.getColumnIndex(BusTimeContract.Timetable.ARRIVAL_TIME));
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
