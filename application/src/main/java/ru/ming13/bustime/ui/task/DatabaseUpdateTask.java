package ru.ming13.bustime.ui.task;


import android.content.Context;
import android.os.AsyncTask;
import ru.ming13.bustime.db.content.DbImportException;
import ru.ming13.bustime.db.content.DbImporter;


public class DatabaseUpdateTask extends AsyncTask<Void, Void, Void>
{
	public interface DatabaseUpdateCallback
	{
		public void onSuccessUpdate();

		public void onFailedUpdate();
	}

	private static enum Result
	{
		SUCCESS, FAIL
	}

	private Result result;

	private final Context context;

	private final DatabaseUpdateCallback databaseUpdateCallback;

	public static DatabaseUpdateTask newInstance(Context context, DatabaseUpdateCallback databaseUpdateCallback) {
		return new DatabaseUpdateTask(context, databaseUpdateCallback);
	}

	private DatabaseUpdateTask(Context context, DatabaseUpdateCallback databaseUpdateCallback) {
		this.context = context;

		this.databaseUpdateCallback = databaseUpdateCallback;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		DbImporter dbImporter = new DbImporter(context);

		try {
			dbImporter.importFromServer();

			result = Result.SUCCESS;
		}
		catch (DbImportException e) {
			result = Result.FAIL;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void taskResult) {
		super.onPostExecute(taskResult);

		switch (result) {
			case SUCCESS:
				databaseUpdateCallback.onSuccessUpdate();
				break;

			case FAIL:
				databaseUpdateCallback.onFailedUpdate();
				break;
		}
	}
}
