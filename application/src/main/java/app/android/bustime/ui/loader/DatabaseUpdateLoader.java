package app.android.bustime.ui.loader;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import app.android.bustime.R;
import app.android.bustime.db.content.DbImportException;
import app.android.bustime.db.content.DbImporter;


public class DatabaseUpdateLoader extends AsyncTaskLoader<String>
{
	private final DbImporter dbImporter;

	public DatabaseUpdateLoader(Context context) {
		super(context);

		dbImporter = new DbImporter(context);
	}

	@Override
	protected void onStartLoading() {
		super.onStartLoading();

		forceLoad();
	}

	@Override
	public String loadInBackground() {
		try {
			dbImporter.importFromServer();
		}
		catch (DbImportException e) {
			return getContext().getString(R.string.error_unspecified);
		}

		return new String();
	}
}
