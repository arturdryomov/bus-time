package ru.ming13.bustime.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import ru.ming13.bustime.R;

public final class LocationGuardian implements View.OnClickListener
{
	private final Fragment fragment;
	private final Runnable action;

	private final String permissionName = Manifest.permission.ACCESS_FINE_LOCATION;
	private final int permissionCode = 42;

	@NonNull
	public static LocationGuardian of(@NonNull Fragment fragment, @NonNull Runnable action) {
		return new LocationGuardian(fragment, action);
	}

	private LocationGuardian(Fragment fragment, Runnable action) {
		this.fragment = fragment;
		this.action = action;
	}

	public void execute() {
		if (isPermissionAvailable()) {
			executeAction();
			return;
		}

		if (isPermissionExplanationNeeded()) {
			showPermissionExplanation();
		} else {
			setUpPermission();
		}
	}

	private boolean isPermissionAvailable() {
		return ContextCompat.checkSelfPermission(fragment.getContext(), permissionName) == PackageManager.PERMISSION_GRANTED;
	}

	private void executeAction() {
		action.run();
	}

	private boolean isPermissionExplanationNeeded() {
		return fragment.shouldShowRequestPermissionRationale(permissionName);
	}

	private void showPermissionExplanation() {
		Snackbars.showFullscreen(fragment.getActivity(), R.string.message_permission_location, R.string.button_ok, this);
	}

	@Override
	public void onClick(View snackbar) {
		setUpPermission();
	}

	private void setUpPermission() {
		fragment.requestPermissions(new String[] {permissionName}, permissionCode);
	}

	public void onRequestPermissionResult(int permissionCode, int[] permissionGrants) {
		if (isPermissionAvailable(permissionCode, permissionGrants)) {
			executeAction();
		}
	}

	private boolean isPermissionAvailable(int permissionCode, int[] permissionGrants) {
		if (permissionCode != this.permissionCode) {
			return false;
		}

		for (int permissionGrant : permissionGrants) {
			if (permissionGrant == PackageManager.PERMISSION_GRANTED) {
				return true;
			}
		}

		return false;
	}
}
