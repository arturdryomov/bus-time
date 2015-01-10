package ru.ming13.bustime.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.google.android.gms.common.GooglePlayServicesUtil;

import ru.ming13.bustime.util.Fragments;

public class GooglePlayServicesErrorDialog extends DialogFragment
{
	public static final String TAG = "gps_error_dialog";

	@InjectExtra(Fragments.Arguments.ERROR_CODE)
	int errorCode;

	@InjectExtra(Fragments.Arguments.REQUEST_CODE)
	int requestCode;

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

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setUpInjections();

		return GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(), requestCode);
	}

	private void setUpInjections() {
		Dart.inject(this, getArguments());
	}
}
