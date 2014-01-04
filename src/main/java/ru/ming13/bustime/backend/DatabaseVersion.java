package ru.ming13.bustime.backend;

import com.google.gson.annotations.SerializedName;

class DatabaseVersion
{
	@SerializedName("version")
	private String version;

	@SerializedName("file_url")
	private String fileUrl;

	public String getVersion() {
		return version;
	}

	public String getFileUrl() {
		return fileUrl;
	}
}
