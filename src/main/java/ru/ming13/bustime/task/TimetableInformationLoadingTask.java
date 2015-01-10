package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.venmo.cursor.CursorList;

import java.util.List;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.TimetableInformationLoadedEvent;
import ru.ming13.bustime.cursor.TimetableCursor;
import ru.ming13.bustime.model.TimetableTime;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public class TimetableInformationLoadingTask extends AsyncTask<Void, Void, BusEvent>
{
	private static final class Defaults
	{
		private Defaults() {
		}

		public static final int TIME_POSITION = 0;
	}

	private final ContentResolver contentResolver;
	private final Uri timetableUri;

	public static void execute(@NonNull Context context, @NonNull Uri timetableUri) {
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
		if (isTimetableWeekPartDependent()) {
			return BusTimeContract.Timetable.Type.currentWeekPartDependent();
		} else {
			return BusTimeContract.Timetable.Type.FULL_WEEK;
		}
	}

	private boolean isTimetableWeekPartDependent() {
		return getFullWeekTripsCount() == 0;
	}

	private int getFullWeekTripsCount() {
		Cursor timetableCursor = getFullWeekTimetableCursor();

		try {
			return timetableCursor.getCount();
		} finally {
			timetableCursor.close();
		}
	}

	private Cursor getFullWeekTimetableCursor() {
		return contentResolver.query(getFullWeekTimetableUri(), null, null, null, null);
	}

	private Uri getFullWeekTimetableUri() {
		return getTimetableUri(BusTimeContract.Timetable.Type.FULL_WEEK);
	}

	private Uri getTimetableUri(int timetableType) {
		return BusTimeContract.Timetable.getTimetableUri(timetableUri, timetableType);
	}

	private int getClosestTimePosition(int timetableType) {
		return getClosestTimePosition(getTimetable(timetableType));
	}

	private int getClosestTimePosition(List<TimetableTime> timetable) {
		Time currentTime = Time.current();

		for (int timePosition = 0; timePosition < timetable.size(); timePosition++) {
			if (timetable.get(timePosition).getTime().isAfter(currentTime)) {
				return timePosition;
			}
		}

		return Defaults.TIME_POSITION;
	}

	private List<TimetableTime> getTimetable(int timetableType) {
		Cursor timetableCursor = getTimetableCursor(timetableType);

		try {
			return new CursorList<>(new TimetableCursor(timetableCursor));
		} finally {
			timetableCursor.close();
		}
	}

	private Cursor getTimetableCursor(int timetableType) {
		return contentResolver.query(getTimetableUri(timetableType), null, null, null, null);
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
