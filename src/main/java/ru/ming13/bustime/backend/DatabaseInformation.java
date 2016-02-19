package ru.ming13.bustime.backend;

import com.google.gson.annotations.SerializedName;

final class DatabaseInformation
{
	@SerializedName("version")
	private String version;

	public String getVersion() {
		return version;
	}
}
