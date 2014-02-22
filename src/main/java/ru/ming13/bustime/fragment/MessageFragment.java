package ru.ming13.bustime.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ming13.bustime.R;
import ru.ming13.bustime.util.Fragments;

public class MessageFragment extends Fragment
{
	public static MessageFragment newInstance(String message) {
		MessageFragment fragment = new MessageFragment();

		fragment.setArguments(buildArguments(message));

		return fragment;
	}

	private static Bundle buildArguments(String message) {
		Bundle arguments = new Bundle();

		arguments.putString(Fragments.Arguments.MESSAGE, message);

		return arguments;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_frame_empty, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpMessage();
	}

	private void setUpMessage() {
		TextView messageView = (TextView) getView().findViewById(R.id.text_message);
		messageView.setText(getMessage());
	}

	private String getMessage() {
		return getArguments().getString(Fragments.Arguments.MESSAGE);
	}
}
