package ru.ming13.bustime.database;


import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public final class DatabaseOperator
{
	private Context context;

	public static DatabaseOperator with(Context context) {
		return new DatabaseOperator(context);
	}

	private DatabaseOperator(Context context) {
		this.context = context;
	}

	public boolean isDatabaseAvailable() {
		return getDatabaseFile().exists();
	}

	private File getDatabaseFile() {
		return context.getDatabasePath(DatabaseSchema.DATABASE_NAME).getAbsoluteFile();
	}

	public void setDatabaseContent(InputStream databaseContent) {
		try {
			File tempDatabaseFile = getTempFile(databaseContent);

			FileUtils.copyFile(tempDatabaseFile, getDatabaseFile());

			tempDatabaseFile.delete();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private File getTempFile(InputStream fileContent) {
		try {
			File tempFile = File.createTempFile("bustime", null, context.getCacheDir());

			FileUtils.copyInputStreamToFile(fileContent, tempFile);

			return tempFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (NullPointerException e) {
			throw new RuntimeException(e);
		}
	}
}
