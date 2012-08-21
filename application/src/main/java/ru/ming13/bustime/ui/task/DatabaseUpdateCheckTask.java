package ru.ming13.bustime.ui.task;


import android.content.Context;
import android.os.AsyncTask;
import ru.ming13.bustime.db.content.DbImportException;
import ru.ming13.bustime.db.content.DbImporter;


public class DatabaseUpdateCheckTask extends AsyncTask<Void, Void, Void>
{
	public interface DatabaseUpdateCheckCallback
	{
		public void onNoUpdatesEver();

		public void onAvailableUpdate();

		public void onFailedUpdateCheck();
	}

	private static enum Result
	{
		NO_UPDATES_EVER, UPDATE_AVAILABLE, NO_UPDATE_AVAILABLE, FAIL
	}

	private Result result;

	private final Context context;

	private final DatabaseUpdateCheckCallback databaseUpdateCheckCallback;

	public static DatabaseUpdateCheckTask newInstance(Context context, DatabaseUpdateCheckCallback databaseUpdateCheckCallback) {
		return new DatabaseUpdateCheckTask(context, databaseUpdateCheckCallback);
	}

	private DatabaseUpdateCheckTask(Context context, DatabaseUpdateCheckCallback databaseUpdateCheckCallback) {
		this.context = context;

		this.databaseUpdateCheckCallback = databaseUpdateCheckCallback;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		try {
			result = checkDatabaseUpdate();
		}
		catch (DbImportException e) {
			result = Result.FAIL;
		}

		return null;
	}

	private Result checkDatabaseUpdate() {
		DbImporter dbImporter = new DbImporter(context);

		if (!dbImporter.isLocalDatabaseEverUpdated()) {
			return Result.NO_UPDATES_EVER;
		}

		if (dbImporter.isLocalDatabaseUpdateAvailable()) {
			return Result.UPDATE_AVAILABLE;
		}
		else {
			return Result.NO_UPDATE_AVAILABLE;
		}
	}

	@Override
	protected void onPostExecute(Void taskResult) {
		super.onPostExecute(taskResult);

		switch (result) {
			case NO_UPDATES_EVER:
				databaseUpdateCheckCallback.onNoUpdatesEver();
				break;

			case UPDATE_AVAILABLE:
				databaseUpdateCheckCallback.onAvailableUpdate();
				break;

			case FAIL:
				databaseUpdateCheckCallback.onFailedUpdateCheck();
				break;
		}
	}
}
