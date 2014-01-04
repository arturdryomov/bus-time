package ru.ming13.bustime.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang3.StringUtils;

public final class Preferences
{
	private static final class Locations
	{
		private Locations() {
		}

		public static final String APPLICATION_STATE = "application_state";
		public static final String DATABASE_STATE = "database_state";
	}

	public static final class Keys
	{
		private Keys() {
		}

		public static final String CONTENT_VERSION = "content_version";
		public static final String SELECTED_TAB_POSITION = "selected_tab_position";
	}

	private static final class Defaults
	{
		private Defaults() {
		}

		public static final int INT = 0;
		public static final String STRING = StringUtils.EMPTY;
	}

	private final SharedPreferences preferences;

	public static Preferences getApplicationStateInstance(Context context) {
		return new Preferences(context, Locations.APPLICATION_STATE);
	}

	public static Preferences getDatabaseStateInstance(Context context) {
		return new Preferences(context, Locations.DATABASE_STATE);
	}

	private Preferences(Context context, String location) {
		preferences = context.getSharedPreferences(location, Context.MODE_PRIVATE);
	}

	public int getInt(String key) {
		return preferences.getInt(key, Defaults.INT);
	}

	public String getString(String key) {
		return preferences.getString(key, Defaults.STRING);
	}

	public void set(String key, int value) {
		preferences.edit().putInt(key, value).commit();
	}

	public void set(String key, String value) {
		preferences.edit().putString(key, value).commit();
	}
}
