package ru.ming13.bustime.database;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import org.apache.commons.io.IOUtils;


final class DatabaseImporter
{
	private static final String DATABASE_NAME = "bustime.db";

	private final Context context;

	public static boolean isDatabaseImportRequired(Context context) {
		return new DatabaseImporter(context).isImportRequired();
	}

	private DatabaseImporter(Context context) {
		this.context = context;
	}

	private boolean isImportRequired() {
		return !isLocalDatabaseExist();
	}

	private boolean isLocalDatabaseExist() {
		return getLocalDatabaseFile().exists();
	}

	private File getLocalDatabaseFile() {
		return context.getDatabasePath(DATABASE_NAME).getAbsoluteFile();
	}

	public static void importDatabase(Context context) {
		new DatabaseImporter(context).importFromAssets();
	}

	private void importFromAssets() {
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
			throw new RuntimeException();
		}
	}

	private OutputStream buildLocalDatabaseStream() {
		try {
			return new FileOutputStream(getLocalDatabaseFile());
		}
		catch (FileNotFoundException e) {
			throw new RuntimeException();
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
			throw new RuntimeException();
		}
	}
}
