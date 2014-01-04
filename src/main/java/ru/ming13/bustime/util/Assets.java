package ru.ming13.bustime.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

import ru.ming13.bustime.database.DatabaseSchema;

public final class Assets
{
	private Assets() {
	}

	public static InputStream getDatabaseContent(Context context) {
		try {
			return context.getAssets().open(DatabaseSchema.DATABASE_NAME);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
