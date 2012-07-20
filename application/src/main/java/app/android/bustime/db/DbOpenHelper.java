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
		// TODO: Change this to downloading file instead of copying from assets
		importDatabaseFromAssets(db);
	}

	private void importDatabaseFromAssets(SQLiteDatabase localDatabase) {
		removeFile(getDatabaseFile(localDatabase));
		copyData(getSourceDatabaseStream(), getDatabaseStream(localDatabase));
	}

	private void removeFile(File file) {
		file.delete();
	}

	private InputStream getSourceDatabaseStream() {
		AssetManager assetManager = context.getAssets();

		try {
			return assetManager.open(DATABASE_NAME);
		}
		catch (IOException e) {
			throw new DbException();
		}
	}

	private OutputStream getDatabaseStream(SQLiteDatabase db) {
		try {
			return new FileOutputStream(getDatabaseFile(db));
		}
		catch (FileNotFoundException e) {
			throw new DbException();
		}
	}

	private File getDatabaseFile(SQLiteDatabase db) {
		return new File(db.getPath());
	}

	private void copyData(InputStream inputStream, OutputStream outputStream) {
		try {
			IOUtils.copy(inputStream, outputStream);
		}
		catch (IOException e) {
			throw new DbException();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDatabaseVersion, int newDatabaseVersion) {
	}
}
