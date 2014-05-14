package ru.ming13.bustime.util;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

import ru.ming13.bustime.R;

final class Android
{
	private Android() {
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
