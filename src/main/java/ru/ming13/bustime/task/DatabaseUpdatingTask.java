package ru.ming13.bustime.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.InputStream;

import ru.ming13.bustime.backend.DatabaseBackend;
import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.UpdatesFinishedEvent;
import ru.ming13.bustime.database.DatabaseOperator;
import ru.ming13.bustime.util.Preferences;

public class DatabaseUpdatingTask extends AsyncTask<Void, Void, BusEvent>
{
	private Context context;

	public static void execute(Context context) {
		new DatabaseUpdatingTask(context).execute();
	}

	private DatabaseUpdatingTask(Context context) {
		this.context = context;
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		try {
			setServerDatabaseContents();
			setLocalDatabaseVersion();
		} catch (RuntimeException e) {
			return new UpdatesFinishedEvent();
		}

		return new UpdatesFinishedEvent();
	}

	private void setServerDatabaseContents() {
		InputStream serverDatabaseContents = DatabaseBackend.getInstance().getDatabaseContents();

		if (serverDatabaseContents == null) {
			return;
		}

		DatabaseOperator.with(context).replaceDatabaseContents(serverDatabaseContents);
	}

	private void setLocalDatabaseVersion() {
		Preferences preferences = Preferences.getDatabaseStateInstance(context);
		preferences.set(Preferences.Keys.CONTENTS_VERSION, getServerDatabaseVersion());
	}

	private String getServerDatabaseVersion() {
		return DatabaseBackend.getInstance().getDatabaseVersion();
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
