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
import ru.ming13.bustime.bus.StopLoadedEvent;
import ru.ming13.bustime.cursor.StopsCursor;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;

public class StopLoadingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final ContentResolver contentResolver;
	private final long stopId;

	public static void execute(@NonNull Context context, long stopId) {
		new StopLoadingTask(context, stopId).execute();
	}

	private StopLoadingTask(Context context, long stopId) {
		this.contentResolver = context.getContentResolver();
		this.stopId = stopId;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		return new StopLoadedEvent(getStop());
	}

	private Stop getStop() {
		for (Stop stop : getStops()) {
			if (stop.getId() == stopId) {
				return stop;
			}
		}

		throw new RuntimeException();
	}

	private List<Stop> getStops() {
		Cursor stopsCursor = contentResolver.query(getStopsUri(), null, null, null, null);

		try {
			return new CursorList<>(new StopsCursor(stopsCursor));
		} finally {
			stopsCursor.close();
		}
	}

	private Uri getStopsUri() {
		return BusTimeContract.Stops.getStopsUri();
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
