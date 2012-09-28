package ru.ming13.bustime.ui.task;


import android.content.Context;
import android.os.AsyncTask;
import ru.ming13.bustime.db.content.DbImportConnectionException;
import ru.ming13.bustime.db.content.DbImportException;
import ru.ming13.bustime.db.content.DbImporter;


public class DatabaseUpdateTask extends AsyncTask<Void, Void, Void>
{
	public interface DatabaseUpdateCallback
	{
		public void onSuccessUpdate();

		public void onNetworkFail();

		public void onFailedUpdate();
	}

	private static enum Result
	{
		SUCCESS, NETWORK_FAIL, FAIL
	}

	private Result result;

	private Context context;

	private DatabaseUpdateCallback databaseUpdateCallback;

	public static DatabaseUpdateTask newInstance(Context context, DatabaseUpdateCallback databaseUpdateCallback) {
		return new DatabaseUpdateTask(context, databaseUpdateCallback);
	}

	private DatabaseUpdateTask(Context context, DatabaseUpdateCallback databaseUpdateCallback) {
		this.context = context;

		this.databaseUpdateCallback = databaseUpdateCallback;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public void setDatabaseUpdateCallback(DatabaseUpdateCallback databaseUpdateCallback) {
		this.databaseUpdateCallback = databaseUpdateCallback;
	}

	@Override
	protected Void doInBackground(Void... parameters) {
		DbImporter dbImporter = new DbImporter(context);

		try {
			dbImporter.importFromServer();

			result = Result.SUCCESS;
		}
		catch (DbImportConnectionException e) {
			result = Result.NETWORK_FAIL;
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

			case NETWORK_FAIL:
				databaseUpdateCallback.onNetworkFail();
				break;

			case FAIL:
				databaseUpdateCallback.onFailedUpdate();
				break;
		}
	}
}
