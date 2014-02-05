package ru.ming13.bustime.backend;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import ru.ming13.bustime.database.DatabaseSchema;

public class DatabaseBackend
{
	private static final String API_URL = "http://server.local:5000";

	private final DatabaseBackendApi backendApi;

	public static DatabaseBackend create() {
		return new DatabaseBackend();
	}

	private DatabaseBackend() {
		this.backendApi = buildBackendApi();
	}

	private DatabaseBackendApi buildBackendApi() {
		RestAdapter backendAdapter = new RestAdapter.Builder()
			.setEndpoint(API_URL)
			.build();

		return backendAdapter.create(DatabaseBackendApi.class);
	}

	public String getDatabaseVersion() {
		try {
			return backendApi.getDatabaseInformation(DatabaseSchema.Versions.CURRENT).getVersion();
		} catch (RetrofitError error) {
			return StringUtils.EMPTY;
		}
	}

	public InputStream getDatabaseContents() {
		try {
			return backendApi.getDatabaseContents(DatabaseSchema.Versions.CURRENT).getBody().in();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
