package ru.ming13.bustime.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ConfigurationInfo;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import ru.ming13.bustime.fragment.GooglePlayServicesErrorDialog;

public final class MapsUtil
{
	private static final int ERROR_DIALOG_REQUEST_CODE = 0;

	private final Context context;

	public static MapsUtil with(Context context) {
		return new MapsUtil(context);
	}

	private MapsUtil(Context context) {
		this.context = context;
	}

	public boolean areMapsHardwareAvailable() {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

		int currentGlEsVersion = configurationInfo.reqGlEsVersion;
		int requiredGlEsVersion = 0x00020000;

		return currentGlEsVersion >= requiredGlEsVersion;
	}

	public boolean areMapsSoftwareAvailable() {
		return getErrorCode() == ConnectionResult.SUCCESS;
	}

	private int getErrorCode() {
		return GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
	}

	public void showErrorDialog(FragmentManager fragmentManager) {
		showErrorDialog(fragmentManager, getErrorCode());
	}

	private void showErrorDialog(FragmentManager fragmentManager, int errorCode) {
		DialogFragment dialog = GooglePlayServicesErrorDialog.newInstance(errorCode, ERROR_DIALOG_REQUEST_CODE);
		dialog.show(fragmentManager, GooglePlayServicesErrorDialog.TAG);
	}

	public boolean isResolvable(ConnectionResult connectionResult) {
		return connectionResult.hasResolution();
	}

	public void resolveError(ConnectionResult connectionResult) {
		try {
			Activity activity = (Activity) context;
			connectionResult.startResolutionForResult(activity, ERROR_DIALOG_REQUEST_CODE);
		} catch (IntentSender.SendIntentException e) {
			throw new RuntimeException(e);
		}
	}

	public void showErrorDialog(FragmentManager fragmentManager, ConnectionResult connectionResult) {
		showErrorDialog(fragmentManager, connectionResult.getErrorCode());
	}
}
