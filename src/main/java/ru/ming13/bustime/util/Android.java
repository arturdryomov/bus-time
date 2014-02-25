package ru.ming13.bustime.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.ViewConfiguration;

import ru.ming13.bustime.R;

final class Android
{
	private Android() {
	}

	public static boolean isIceCreamSandwichOrLater() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

	public static boolean isKitKatOrLater() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static boolean hasHardwareMenuKey(Context context) {
		return ViewConfiguration.get(context).hasPermanentMenuKey();
	}

	public static boolean isPortrait(Context context) {
		return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	public static boolean isTablet(Context context) {
		return context.getResources().getBoolean(R.bool.tablet);
	}
}
