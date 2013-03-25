package ru.ming13.bustime.ui.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.apache.commons.lang3.StringUtils;


public final class Preferences
{
	public static final class Keys
	{
		private Keys() {
		}

		public static final String DATABASE_ETAG = "database_etag";
		public static final String UPDATE_AVAILABLE = "update_available";
		public static final String SELECTED_TAB_INDEX = "selected_tab_index";
	}

	private Preferences() {
	}

	public static void set(Context context, String key, String value) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(context);

		preferencesEditor.putString(key, value);

		preferencesEditor.commit();
	}

	private static SharedPreferences.Editor getPreferencesEditor(Context context) {
		return getSharedPreferences(context).edit();
	}

	public static void set(Context context, String key, int value) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(context);

		preferencesEditor.putInt(key, value);

		preferencesEditor.commit();
	}

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	public static String getString(Context context, String key) {
		return getSharedPreferences(context).getString(key, StringUtils.EMPTY);
	}

	public static int getInt(Context context, String key) {
		return getSharedPreferences(context).getInt(key, 0);
	}

	public static void remove(Context context, String key) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(context);

		preferencesEditor.remove(key);

		preferencesEditor.commit();
	}
}
