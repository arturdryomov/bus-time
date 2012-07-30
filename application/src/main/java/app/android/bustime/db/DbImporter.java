package app.android.bustime.db;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import app.android.bustime.R;
import app.android.bustime.ui.Preferences;
import org.apache.commons.io.IOUtils;


public class DbImporter
{
	private static final String DATABASE_NAME = "bustime.db";

	private static final String HTTP_ETAG_HEADER_FIELD = "etag";
	private static final String HTTP_HEAD_REQUEST_TYPE = "HEAD";

	private final Context context;

	public DbImporter(Context context) {
		this.context = context;
	}

	public boolean isLocalDatabaseExist() {
		return getLocalDatabaseFile().exists();
	}

	private File getLocalDatabaseFile() {
		return context.getDatabasePath(DATABASE_NAME).getAbsoluteFile();
	}

	public void importFromAssets() {
		if (!isDatabasesDirectoryExist()) {
			createDatabasesDirectory();
		}

		copyAssetsDatabaseToLocalDatabase();
	}

	private boolean isDatabasesDirectoryExist() {
		return getLocalDatabaseFile().getParentFile().exists();
	}

	private void createDatabasesDirectory() {
		File databasesDirectory = getLocalDatabaseFile().getParentFile();
		databasesDirectory.mkdirs();
	}

	private void copyAssetsDatabaseToLocalDatabase() {
		InputStream assetsDatabaseStream = buildAssetsDatabaseStream();
		OutputStream localDatabaseStream = buildLocalDatabaseStream();

		copyData(assetsDatabaseStream, localDatabaseStream);
	}

	private InputStream buildAssetsDatabaseStream() {
		AssetManager assetManager = context.getAssets();

		try {
			return assetManager.open(DATABASE_NAME);
		}
		catch (IOException e) {
			throw new DbImportException();
		}
	}

	private OutputStream buildLocalDatabaseStream() {
		try {
			return new FileOutputStream(getLocalDatabaseFile());
		}
		catch (FileNotFoundException e) {
			throw new DbImportException();
		}
	}

	private void copyData(InputStream inputStream, OutputStream outputStream) {
		try {
			IOUtils.copy(inputStream, outputStream);

			outputStream.flush();
			outputStream.close();

			inputStream.close();
		}
		catch (IOException e) {
			throw new DbImportException();
		}
	}

	public void importFromServer() {
		copyServerDatabaseToLocalDatabase();

		updateLocalDatabaseEtag();
		removeLocalDatabaseUpdateAvailableStored();

		DbProvider.getInstance().refreshDatabase(context);
	}

	private void copyServerDatabaseToLocalDatabase() {
		InputStream serverDatabaseStream = buildServerDatabaseStream();
		OutputStream localDatabaseStream = buildLocalDatabaseStream();

		copyData(serverDatabaseStream, localDatabaseStream);
	}

	private InputStream buildServerDatabaseStream() {
		try {
			return new GZIPInputStream(buildServerDatabaseUrl().openStream());
		}
		catch (IOException e) {
			throw new DbImportException();
		}
	}

	private URL buildServerDatabaseUrl() {
		try {
			return new URL(context.getString(R.string.url_server_database_file));
		}
		catch (MalformedURLException e) {
			throw new DbImportException();
		}
	}

	private void updateLocalDatabaseEtag() {
		String serverDatabaseEtag = getServerDatabaseEtag();

		Preferences.set(context, Preferences.PREFERENCE_DATABASE_ETAG, serverDatabaseEtag);
	}

	private String getServerDatabaseEtag() {
		URLConnection urlConnection = buildServerDatabaseConnection();

		String serverDatabaseEtag = urlConnection.getHeaderField(HTTP_ETAG_HEADER_FIELD);

		if (serverDatabaseEtag == null) {
			throw new DbImportException();
		}

		return serverDatabaseEtag;
	}

	private URLConnection buildServerDatabaseConnection() {
		try {
			HttpURLConnection httpURLConnection = (HttpURLConnection) buildServerDatabaseUrl().openConnection();
			httpURLConnection.setRequestMethod(HTTP_HEAD_REQUEST_TYPE);

			return httpURLConnection;
		}
		catch (IOException e) {
			throw new DbImportException();
		}
	}

	private void removeLocalDatabaseUpdateAvailableStored() {
		Preferences.remove(context, Preferences.PREFERENCE_UPDATE_AVAILABLE);
	}

	public boolean isLocalDatabaseEverUpdated() {
		return !TextUtils.isEmpty(getLocalDatabaseEtag());
	}

	private String getLocalDatabaseEtag() {
		return Preferences.getString(context, Preferences.PREFERENCE_DATABASE_ETAG);
	}

	public boolean isLocalDatabaseUpdateAvailable() {
		if (isLocalDatabaseUpdateAvailableStored()) {
			return true;
		}

		if (!isServerDatabaseEtagEqualsLocalDatabaseEtag()) {
			storeLocalDatabaseUpdateAvailable();

			return true;
		}

		return false;
	}

	private boolean isLocalDatabaseUpdateAvailableStored() {
		return !TextUtils.isEmpty(
			Preferences.getString(context, Preferences.PREFERENCE_UPDATE_AVAILABLE));
	}

	private boolean isServerDatabaseEtagEqualsLocalDatabaseEtag() {
		String serverDatabaseEtag = getServerDatabaseEtag();
		String localDatabaseEtag = getLocalDatabaseEtag();

		return serverDatabaseEtag.equals(localDatabaseEtag);
	}

	private void storeLocalDatabaseUpdateAvailable() {
		Preferences.set(context, Preferences.PREFERENCE_UPDATE_AVAILABLE,
			Preferences.PREFERENCE_UPDATE_AVAILABLE);
	}
}
