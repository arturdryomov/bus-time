package ru.ming13.bustime.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.android.gms.common.GooglePlayServicesUtil;

import ru.ming13.bustime.util.Fragments;

public class GooglePlayServicesErrorDialog extends DialogFragment
{
	public static final String TAG = "GPS_ERROR_DIALOG";

	public static GooglePlayServicesErrorDialog newInstance(int errorCode, int requestCode) {
		GooglePlayServicesErrorDialog dialog = new GooglePlayServicesErrorDialog();

		dialog.setArguments(buildArguments(errorCode, requestCode));

		return dialog;
	}

	private static Bundle buildArguments(int errorCode, int requestCode) {
		Bundle arguments = new Bundle();

		arguments.putInt(Fragments.Arguments.ERROR_CODE, errorCode);
		arguments.putInt(Fragments.Arguments.REQUEST_CODE, requestCode);

		return arguments;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return GooglePlayServicesUtil.getErrorDialog(getErrorCode(), getActivity(), getRequestCode());
	}

	private int getErrorCode() {
		return getArguments().getInt(Fragments.Arguments.ERROR_CODE);
	}

	private int getRequestCode() {
		return getArguments().getInt(Fragments.Arguments.REQUEST_CODE);
	}
}