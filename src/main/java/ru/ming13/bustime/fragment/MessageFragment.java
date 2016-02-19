package ru.ming13.bustime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.ming13.bustime.R;
import ru.ming13.bustime.util.Fragments;

public final class MessageFragment extends Fragment
{
	@Bind(R.id.text_message)
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
		return layoutInflater.inflate(R.layout.fragment_message, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpBindings();

		setUpMessage();
	}

	private void setUpBindings() {
		ButterKnife.bind(this, getView());
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

		ButterKnife.unbind(this);
	}
}
