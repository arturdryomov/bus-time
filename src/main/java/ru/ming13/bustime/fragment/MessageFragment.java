package ru.ming13.bustime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.util.Fragments;

public class MessageFragment extends Fragment
{
	@InjectView(R.id.text_message)
	TextView message;

	public static MessageFragment newInstance(@Nullable String message) {
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
		View view = layoutInflater.inflate(R.layout.fragment_message, container, false);

		ButterKnife.inject(this, view);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpMessage();
	}

	private void setUpMessage() {
		message.setText(getMessage());
	}

	private String getMessage() {
		return getArguments().getString(Fragments.Arguments.MESSAGE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset(this);
	}
}
