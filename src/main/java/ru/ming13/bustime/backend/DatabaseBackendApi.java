package ru.ming13.bustime.backend;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

interface DatabaseBackendApi
{
	@GET("/db-updates/version/{schema}")
	DatabaseVersion getDatabaseVersion(@Path("schema") int schemaVersion);

	@GET("/db-updates/file/{schema}")
	Response getDatabaseFile(@Path("schema") int schemaVersion);
}
