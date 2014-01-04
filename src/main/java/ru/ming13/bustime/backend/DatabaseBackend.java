package ru.ming13.bustime.backend;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import ru.ming13.bustime.database.DatabaseSchema;

public class DatabaseBackend
{
	private static final String SERVER_URL = "http://bustime-backend-dsav.herokuapp.com";

	private final DatabaseBackendApi backendApi;

	public DatabaseBackend() {
		this.backendApi = buildBackendApi();
	}

	private DatabaseBackendApi buildBackendApi() {
		RestAdapter backendAdapter = new RestAdapter.Builder()
			.setServer(SERVER_URL)
			.build();

		return backendAdapter.create(DatabaseBackendApi.class);
	}

	public String getDatabaseVersion() {
		try {
			return backendApi.getDatabaseVersion(DatabaseSchema.Versions.CURRENT).getVersion();
		} catch (RetrofitError error) {
			return StringUtils.EMPTY;
		}
	}

	public InputStream getDatabaseContent() {
		try {
			return new GZIPInputStream(backendApi.getDatabaseFile(DatabaseSchema.Versions.CURRENT).getBody().in());
		} catch (IOException e) {
			return null;
		}
	}
}
