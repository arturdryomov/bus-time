package ru.ming13.bustime.task;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.commons.lang3.StringUtils;

import ru.ming13.bustime.backend.DatabaseBackend;
import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.UpdatesAvailableEvent;
import ru.ming13.bustime.bus.UpdatesForcedEvent;
import ru.ming13.bustime.bus.UpdatesNotAvailableEvent;
import ru.ming13.bustime.util.Preferences;

public class DatabaseUpdateCheckingTask extends AsyncTask<Void, Void, BusEvent>
{
	private final Context context;

	public static void execute(Context context) {
		new DatabaseUpdateCheckingTask(context).execute();
	}

	private DatabaseUpdateCheckingTask(Context context) {
		this.context = context.getApplicationContext();
	}

	@Override
	protected BusEvent doInBackground(Void... paramteters) {
		String localDatabaseVersion = getLocalDatabaseVersion();
		String serverDatabaseVersion = getServerDatabaseVersion();

		if (StringUtils.isEmpty(serverDatabaseVersion)) {
			return new UpdatesNotAvailableEvent();
		}

		if (StringUtils.isEmpty(localDatabaseVersion)) {
			return new UpdatesForcedEvent();
		}

		if (!localDatabaseVersion.equals(serverDatabaseVersion)) {
			return new UpdatesAvailableEvent();
		}

		return new UpdatesNotAvailableEvent();
	}

	private String getLocalDatabaseVersion() {
		Preferences preferences = Preferences.getDatabaseStateInstance(context);
		return preferences.getString(Preferences.Keys.CONTENT_VERSION);
	}

	private String getServerDatabaseVersion() {
		return new DatabaseBackend().getDatabaseVersion();
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getBus().post(busEvent);
	}
}
