package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;

import java.io.InputStream;

import ru.ming13.bustime.backend.DatabaseBackend;
import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.DatabaseUpdateFinishedEvent;
import ru.ming13.bustime.database.DatabaseOperator;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Preferences;

public class DatabaseUpdatingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final Context context;

	public static void execute(Context context) {
		new DatabaseUpdatingTask(context).execute();
	}

	private DatabaseUpdatingTask(Context context) {
		this.context = context.getApplicationContext();
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		try {
			updateDatabaseContents();
			updateDatabaseVersion();

			notifyDatabaseContentsChange();
		} catch (RuntimeException e) {
			return new DatabaseUpdateFinishedEvent();
		}

		return new DatabaseUpdateFinishedEvent();
	}

	private void updateDatabaseContents() {
		InputStream serverDatabaseContents = DatabaseBackend.with(context).getDatabaseContents();
		DatabaseOperator.with(context).replaceDatabaseContents(serverDatabaseContents);
	}

	private void updateDatabaseVersion() {
		Preferences preferences = Preferences.getDatabaseStateInstance(context);
		preferences.set(Preferences.Keys.CONTENTS_VERSION, DatabaseBackend.with(context).getDatabaseVersion());
	}

	private void notifyDatabaseContentsChange() {
		ContentResolver contentResolver = context.getContentResolver();

		contentResolver.notifyChange(BusTimeContract.Routes.getRoutesUri(), null);
		contentResolver.notifyChange(BusTimeContract.Stops.getStopsUri(), null);
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
