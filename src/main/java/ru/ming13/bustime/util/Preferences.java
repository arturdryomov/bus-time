package ru.ming13.bustime.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;

public final class Preferences
{
	private static final String LOCATION = "state";

	private static final class Keys
	{
		private Keys() {
		}

		public static final String DATABASE_VERSION = "database_version";
		public static final String HOME_TAB_POSITION = "home_tab_position";
	}

	private static final class Defaults
	{
		private Defaults() {
		}

		public static final int INT = 0;
		public static final String STRING = StringUtils.EMPTY;
	}

	private final SharedPreferences preferences;

	public static Preferences with(@NonNull Context context) {
		return new Preferences(context);
	}

	private Preferences(Context context) {
		preferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
	}

	public String getDatabaseVersion() {
		return getString(Keys.DATABASE_VERSION);
	}

	private String getString(String key) {
		return preferences.getString(key, Defaults.STRING);
	}

	public void setDatabaseVersion(String databaseVersion) {
		set(Keys.DATABASE_VERSION, databaseVersion);
	}

	private void set(String key, String value) {
		preferences.edit().putString(key, value).apply();
	}

	public int getHomeTabPosition() {
		return getInt(Keys.HOME_TAB_POSITION);
	}

	private int getInt(String key) {
		return preferences.getInt(key, Defaults.INT);
	}

	public void setHomeTabPosition(int homeTabPosition) {
		set(Keys.HOME_TAB_POSITION, homeTabPosition);
	}

	private void set(String key, int value) {
		preferences.edit().putInt(key, value).apply();
	}
}
