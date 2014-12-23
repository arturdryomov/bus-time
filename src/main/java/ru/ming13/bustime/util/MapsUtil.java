package ru.ming13.bustime.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ConfigurationInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import ru.ming13.bustime.fragment.GooglePlayServicesErrorDialog;

public final class MapsUtil
{
	private final FragmentActivity activity;

	public static MapsUtil with(@NonNull FragmentActivity activity) {
		return new MapsUtil(activity);
	}

	private MapsUtil(FragmentActivity activity) {
		this.activity = activity;
	}

	public boolean areMapsHardwareAvailable() {
		ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

		int currentGlEsVersion = configurationInfo.reqGlEsVersion;
		int requiredGlEsVersion = 0x00020000;

		return currentGlEsVersion >= requiredGlEsVersion;
	}

	public boolean areMapsSoftwareAvailable() {
		return getErrorCode() == ConnectionResult.SUCCESS;
	}

	private int getErrorCode() {
		return GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
	}

	public void showErrorDialog() {
		showErrorDialog(getErrorCode());
	}

	public void showErrorDialog(ConnectionResult connectionResult) {
		showErrorDialog(connectionResult.getErrorCode());
	}

	private void showErrorDialog(int errorCode) {
		DialogFragment errorDialog = GooglePlayServicesErrorDialog.newInstance(errorCode, 0);
		errorDialog.show(activity.getSupportFragmentManager(), GooglePlayServicesErrorDialog.TAG);
	}

	public boolean isResolvable(ConnectionResult connectionResult) {
		return connectionResult.hasResolution();
	}

	public void resolve(ConnectionResult connectionResult) {
		try {
			connectionResult.startResolutionForResult(activity, 0);
		} catch (IntentSender.SendIntentException e) {
			throw new RuntimeException(e);
		}
	}
}
