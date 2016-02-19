package ru.ming13.bustime.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import info.metadude.android.typedpreferences.IntPreference;
import info.metadude.android.typedpreferences.StringPreference;

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
		public static final String STRING = Strings.EMPTY;
	}

	private final SharedPreferences preferences;

	public static Preferences of(@NonNull Context context) {
		return new Preferences(context);
	}

	private Preferences(Context context) {
		this.preferences = context.getSharedPreferences(LOCATION, Context.MODE_PRIVATE);
	}

    public StringPreference getDatabaseVersionPreference() {
        return new StringPreference(preferences, Keys.DATABASE_VERSION, Defaults.STRING);
    }

    public IntPreference getHomeTabPositionPreference() {
        return new IntPreference(preferences, Keys.HOME_TAB_POSITION, Defaults.INT);
    }
}
