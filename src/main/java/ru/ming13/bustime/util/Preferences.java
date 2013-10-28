package ru.ming13.bustime.util;

import android.content.Context;
import android.content.SharedPreferences;

public final class Preferences
{
	private static final class Locations
	{
		private Locations() {
		}

		public static final String APPLICATION_STATE = "application_state";
	}

	public static final class Keys
	{
		private Keys() {
		}

		public static final String SELECTED_TAB_POSITION = "selected_tab_position";
	}

	private static final class Defaults
	{
		private Defaults() {
		}

		public static final int INT = 0;
	}

	private final SharedPreferences preferences;

	public static Preferences getApplicationStateInstance(Context context) {
		return new Preferences(context, Locations.APPLICATION_STATE);
	}

	private Preferences(Context context, String location) {
		preferences = context.getSharedPreferences(location, Context.MODE_PRIVATE);
	}

	public int getInt(String key) {
		return preferences.getInt(key, Defaults.INT);
	}

	public void set(String key, int value) {
		preferences.edit().putInt(key, value).commit();
	}
}
