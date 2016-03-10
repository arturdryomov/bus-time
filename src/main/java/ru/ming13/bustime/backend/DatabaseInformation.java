package ru.ming13.bustime.backend;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

final class DatabaseInformation
{
	@SerializedName("version")
	private String version;

	@NonNull
	public String getVersion() {
		return version;
	}
}
