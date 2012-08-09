package ru.ming13.bustime.ui.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public final class Preferences
{
	public static final String PREFERENCE_DATABASE_ETAG = "database_etag";
	public static final String PREFERENCE_UPDATE_AVAILABLE = "update_available";

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

	private static SharedPreferences getSharedPreferences(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	public static String getString(Context context, String key) {
		return getSharedPreferences(context).getString(key, new String());
	}

	public static void remove(Context context, String key) {
		SharedPreferences.Editor preferencesEditor = getPreferencesEditor(context);

		preferencesEditor.remove(key);

		preferencesEditor.commit();
	}
}
