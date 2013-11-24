package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.TimetableTypeQueriedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public class TimetableTypeQueryingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final Uri timetableUri;

	public static void execute(Context context, Uri timetableUri) {
		new TimetableTypeQueryingTask(context, timetableUri).execute();
	}

	private TimetableTypeQueryingTask(Context context, Uri timetableUri) {
		this.contentResolver = context.getContentResolver();
		this.timetableUri = timetableUri;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return new TimetableTypeQueriedEvent(getTimetableTypeId());
	}

	private int getTimetableTypeId() {
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
		Cursor timetableCursor = queryTimetable();

		try {
			return timetableCursor.getCount();
		} finally {
			timetableCursor.close();
		}
	}

	private Cursor queryTimetable() {
		return contentResolver.query(getFullWeekTimetableUri(), null, null, null, null);
	}

	private Uri getFullWeekTimetableUri() {
		return BusTimeContract.Timetable.buildTimetableUri(
			timetableUri, BusTimeContract.Timetable.Type.FULL_WEEK);
	}

	@Override
	protected void onPostExecute(BusEvent timetableInformationQueriedEvent) {
		super.onPostExecute(timetableInformationQueriedEvent);

		BusProvider.getBus().post(timetableInformationQueriedEvent);
	}
}
