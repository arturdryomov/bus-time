package ru.ming13.bustime.backend;

import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import ru.ming13.bustime.R;
import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.util.Strings;

public final class DatabaseBackend
{
	private final DatabaseBackendApi backendApi;

	@NonNull
	public static DatabaseBackend with(@NonNull Context context) {
		return new DatabaseBackend(context.getApplicationContext());
	}

	private DatabaseBackend(Context context) {
		this.backendApi = createBackendApi(context);
	}

	private DatabaseBackendApi createBackendApi(Context context) {
		RestAdapter backendAdapter = new RestAdapter.Builder()
			.setEndpoint(context.getString(R.string.url_backend))
			.build();

		return backendAdapter.create(DatabaseBackendApi.class);
	}

	@NonNull
	public String getDatabaseVersion() {
		try {
			return backendApi.getDatabaseInformation(DatabaseSchema.Versions.CURRENT).getVersion();
		} catch (RetrofitError error) {
			return Strings.EMPTY;
		}
	}

	@NonNull
	public InputStream getDatabaseContents() {
		try {
			return backendApi.getDatabaseContents(DatabaseSchema.Versions.CURRENT).getBody().in();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
