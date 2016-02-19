package ru.ming13.bustime.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ConfigurationInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import ru.ming13.bustime.fragment.GooglePlayServicesErrorDialog;

public final class Maps
{
	private final FragmentActivity activity;

	public static Maps at(@NonNull FragmentActivity activity) {
		return new Maps(activity);
	}

	private Maps(FragmentActivity activity) {
		this.activity = activity;
	}

	public boolean areHardwareAvailable() {
		ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

		int currentGlEsVersion = configurationInfo.reqGlEsVersion;
		int requiredGlEsVersion = 0x00020000;

		return currentGlEsVersion >= requiredGlEsVersion;
	}

	public boolean areSoftwareAvailable() {
		return getErrorCode() == ConnectionResult.SUCCESS;
	}

	private int getErrorCode() {
		return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
	}

	public void showErrorDialog() {
		showErrorDialog(getErrorCode());
	}

	public void showErrorDialog(@NonNull ConnectionResult connectionResult) {
		showErrorDialog(connectionResult.getErrorCode());
	}

	private void showErrorDialog(int errorCode) {
		DialogFragment errorDialog = GooglePlayServicesErrorDialog.newInstance(errorCode, 0);
		errorDialog.show(activity.getSupportFragmentManager(), GooglePlayServicesErrorDialog.TAG);
	}

	public boolean isResolvable(@NonNull ConnectionResult connectionResult) {
		return connectionResult.hasResolution();
	}

	public void resolve(@NonNull ConnectionResult connectionResult) {
		try {
			connectionResult.startResolutionForResult(activity, 0);
		} catch (IntentSender.SendIntentException e) {
			throw new RuntimeException(e);
		}
	}
}
