package ru.ming13.bustime.ui.task;


import android.content.Context;
import android.os.AsyncTask;
import ru.ming13.bustime.db.content.DbImportConnectionException;
import ru.ming13.bustime.db.content.DbImportException;
import ru.ming13.bustime.db.content.DbImporter;
import ru.ming13.bustime.ui.bus.BusEvent;
import ru.ming13.bustime.ui.bus.BusProvider;
import ru.ming13.bustime.ui.bus.DatabaseUpdateFailedEvent;
import ru.ming13.bustime.ui.bus.DatabaseUpdateSucceedEvent;


public class DatabaseUpdateTask extends AsyncTask<Void, Void, BusEvent>
{
	private final DbImporter dbImporter;

	public static void execute(Context context) {
		new DatabaseUpdateTask(context).execute();
	}

	private DatabaseUpdateTask(Context context) {
		this.dbImporter = new DbImporter(context);
	}

	@Override
	protected BusEvent doInBackground(Void... parameters) {
		try {
			dbImporter.importFromServer();

			return new DatabaseUpdateSucceedEvent();
		}
		catch (DbImportConnectionException e) {
			return new DatabaseUpdateFailedEvent(true);
		}
		catch (DbImportException e) {
			return new DatabaseUpdateFailedEvent(false);
		}
	}

	@Override
	protected void onPostExecute(BusEvent busEvent) {
		super.onPostExecute(busEvent);

		BusProvider.getInstance().post(busEvent);
	}
}
