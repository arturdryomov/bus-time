package ru.ming13.bustime.util;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import ru.ming13.bustime.database.DatabaseSchema;

public final class Assets
{
	private final Context context;

	public static Assets of(@NonNull Context context) {
		return new Assets(context);
	}

	private Assets(Context context) {
		this.context = context.getApplicationContext();
	}

	public InputStream getDatabaseContents() {
		try {
			return context.getAssets().open(DatabaseSchema.DATABASE_NAME);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
