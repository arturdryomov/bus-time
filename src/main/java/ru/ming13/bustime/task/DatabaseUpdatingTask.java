package ru.ming13.bustime.task;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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

	public static void execute(@NonNull Context context) {
		new DatabaseUpdatingTask(context).execute();
	}

	private DatabaseUpdatingTask(Context context) {
		this.context = context.getApplicationContext();
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		try {
			changeLocalDatabaseContents();
			changeLocalDatabaseVersion();

			notifyLocalDatabaseContentsChange();

			return new DatabaseUpdateFinishedEvent();
		} catch (RuntimeException e) {
			return new DatabaseUpdateFinishedEvent();
		}
	}

	private void changeLocalDatabaseContents() {
		DatabaseOperator.with(context).replaceDatabaseContents(getServerDatabaseContents());
	}

	private InputStream getServerDatabaseContents() {
		return DatabaseBackend.with(context).getDatabaseContents();
	}

	private void changeLocalDatabaseVersion() {
		Preferences.of(context).setDatabaseVersion(getServerDatabaseVersion());
	}

	private String getServerDatabaseVersion() {
		return DatabaseBackend.with(context).getDatabaseVersion();
	}

	private void notifyLocalDatabaseContentsChange() {
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
