package ru.ming13.bustime.ui.fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class ProgressDialogFragment extends DialogFragment
{
	public static final String TAG = "progress_dialog";

	private static final boolean CANCELABLE = false;

	public static ProgressDialogFragment newInstance(String message) {
		ProgressDialogFragment progressDialog = new ProgressDialogFragment();

		progressDialog.setArguments(buildArguments(message));

		progressDialog.setCancelable(CANCELABLE);

		return progressDialog;
	}

	private static Bundle buildArguments(String message) {
		Bundle arguments = new Bundle();

		arguments.putString(FragmentArguments.MESSAGE, message);

		return arguments;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return buildProgressDialog();
	}

	private ProgressDialog buildProgressDialog() {
		ProgressDialog progressDialog = new ProgressDialog(getActivity());

		progressDialog.setMessage(getMessage());

		return progressDialog;
	}

	private String getMessage() {
		return getArguments().getString(FragmentArguments.MESSAGE);
	}
}
