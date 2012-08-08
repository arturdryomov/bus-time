package app.android.bustime.ui.loader;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.db.DbImportException;
import app.android.bustime.db.DbImporter;


public class DatabaseUpdateCheckLoader extends AsyncTaskLoader<Bundle>
{
	public static final String RESULT_DATABASE_EVER_UPDATED_KEY = "database_ever_updated";
	public static final String RESULT_DATABASE_UPDATE_AVAILABLE_KEY = "database_update_available";

	private final DbImporter dbImporter;

	public DatabaseUpdateCheckLoader(Context context) {
		super(context);

		dbImporter = new DbImporter(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public Bundle loadInBackground() {
		Bundle result = new Bundle();

		try {
			boolean isDatabaseEverUpdated = dbImporter.isLocalDatabaseEverUpdated();
			result.putBoolean(RESULT_DATABASE_EVER_UPDATED_KEY, isDatabaseEverUpdated);

			if (!isDatabaseEverUpdated) {
				return result;
			}

			boolean isDatabaseUpdateAvailable = dbImporter.isLocalDatabaseUpdateAvailable();
			result.putBoolean(RESULT_DATABASE_UPDATE_AVAILABLE_KEY, isDatabaseUpdateAvailable);
		}
		catch (DbImportException e) {
			// Skip exceptions, this update check is optional
		}

		return result;
	}
}
