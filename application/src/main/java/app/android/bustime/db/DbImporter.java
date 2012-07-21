package app.android.bustime.db;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import org.apache.commons.io.IOUtils;


public class DbImporter
{
	private static final String DATABASE_NAME = "bustime.db";

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

		if (!databasesDirectory.exists()) {
			databasesDirectory.mkdirs();
		}
	}

	private void copyAssetsDatabaseToLocalDatabase() {
		InputStream assetsDatabaseStream = getAssetsDatabaseStream();
		OutputStream localDatabaseStream = getLocalDatabaseStream();

		copyData(assetsDatabaseStream, localDatabaseStream);
	}

	private InputStream getAssetsDatabaseStream() {
		AssetManager assetManager = context.getAssets();

		try {
			return assetManager.open(DATABASE_NAME);
		}
		catch (IOException e) {
			throw new DbImportException();
		}
	}

	private OutputStream getLocalDatabaseStream() {
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
}
