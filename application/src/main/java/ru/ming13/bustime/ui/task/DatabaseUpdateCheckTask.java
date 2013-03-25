package ru.ming13.bustime.ui.task;


import android.content.Context;
import android.os.AsyncTask;
import ru.ming13.bustime.db.content.DbImportException;
import ru.ming13.bustime.db.content.DbImporter;
import ru.ming13.bustime.ui.bus.BusEvent;
import ru.ming13.bustime.ui.bus.BusProvider;
import ru.ming13.bustime.ui.bus.DatabaseUpdateAvailableEvent;
import ru.ming13.bustime.ui.bus.DatabaseUpdateCheckFailedEvent;
import ru.ming13.bustime.ui.bus.NoDatabaseUpdateAvailableEvent;
import ru.ming13.bustime.ui.bus.NoDatabaseUpdatesEverEvent;


public class DatabaseUpdateCheckTask extends AsyncTask<Void, Void, BusEvent>
{
	private final DbImporter dbImporter;

	public static void execute(Context context) {
		new DatabaseUpdateCheckTask(context).execute();
	}

	private DatabaseUpdateCheckTask(Context context) {
		this.dbImporter = new DbImporter(context);
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		try {
			return checkDatabaseUpdate();
		}
		catch (DbImportException e) {
			return new DatabaseUpdateCheckFailedEvent();
		}
	}

	private BusEvent checkDatabaseUpdate() {
		if (!dbImporter.isLocalDatabaseEverUpdated()) {
			return new NoDatabaseUpdatesEverEvent();
		}

		if (dbImporter.isLocalDatabaseUpdateAvailable()) {
			return new DatabaseUpdateAvailableEvent();
		}
		else {
			return new NoDatabaseUpdateAvailableEvent();
		}
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getInstance().post(busEvent);
	}
}
