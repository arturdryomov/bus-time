package ru.ming13.bustime.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import ru.ming13.bustime.BuildConfig;
import ru.ming13.bustime.R;

public final class Android
{
	public static final int TARGET_VERSION = Build.VERSION_CODES.LOLLIPOP;

	private Android() {
	}

	public static String getApplicationId() {
		return BuildConfig.APPLICATION_ID;
	}

	public static boolean isHoneycombOrLater() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean isIceCreamSandwichOrLater() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean isKitKatOrLater() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	public static boolean isPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	public static boolean isTablet(Context context) {
		return context.getResources().getBoolean(R.bool.tablet);
	}
}
