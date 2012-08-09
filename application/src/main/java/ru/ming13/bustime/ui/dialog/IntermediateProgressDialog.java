package ru.ming13.bustime.ui.dialog;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class IntermediateProgressDialog extends DialogFragment
{
	public static final String TAG = "progress_dialog";

	private static final String SAVED_INSTANCE_MESSAGE_KEY = "message";

	private String message;

	public static IntermediateProgressDialog newInstance(String message) {
		IntermediateProgressDialog progressDialog = new IntermediateProgressDialog();

		progressDialog.message = message;

		return progressDialog;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(SAVED_INSTANCE_MESSAGE_KEY, message);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isSavedInstanceStateValid(savedInstanceState)) {
			message = savedInstanceState.getString(SAVED_INSTANCE_MESSAGE_KEY);
		}
	}

	private boolean isSavedInstanceStateValid(Bundle savedInstanceState) {
		return savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_MESSAGE_KEY);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return buildProgressDialog();
	}

	private ProgressDialog buildProgressDialog() {
		ProgressDialog progressDialog = new ProgressDialog(getActivity());

		progressDialog.setMessage(message);

		return progressDialog;
	}
}
