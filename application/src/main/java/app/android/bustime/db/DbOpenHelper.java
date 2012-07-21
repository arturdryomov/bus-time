package app.android.bustime.db;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import org.apache.commons.io.IOUtils;


class DbOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "bustime.db";

	private final Context context;

	public DbOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
	}

	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (!isLocalDatabaseExist()) {
			importDatabaseFromAssets();
		}

		return super.getWritableDatabase();
	}

	private boolean isLocalDatabaseExist() {
		return getLocalDatabaseFile().exists();
	}

	private File getLocalDatabaseFile() {
		return context.getDatabasePath(DATABASE_NAME).getAbsoluteFile();
	}

	private void importDatabaseFromAssets() {
		createDatabasesDirectory();

		InputStream assetsDatabaseStream = getAssetsDatabaseStream();
		OutputStream localDatabaseStream = getLocalDatabaseStream();

		copyData(assetsDatabaseStream, localDatabaseStream);
	}

	private void createDatabasesDirectory() {
		File databasesDirectory = getLocalDatabaseFile().getParentFile();

		if (!databasesDirectory.exists()) {
			databasesDirectory.mkdirs();
		}
	}

	private InputStream getAssetsDatabaseStream() {
		AssetManager assetManager = context.getAssets();

		try {
			return assetManager.open(DATABASE_NAME);
		}
		catch (IOException e) {
			throw new DbException();
		}
	}

	private OutputStream getLocalDatabaseStream() {
		try {
			return new FileOutputStream(getLocalDatabaseFile());
		}
		catch (FileNotFoundException e) {
			throw new DbException();
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
			throw new DbException();
		}
	}
}
