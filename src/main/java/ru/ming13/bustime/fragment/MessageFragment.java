package ru.ming13.bustime.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.util.Fragments;

public class MessageFragment extends Fragment
{
	@InjectView(R.id.text_message)
	TextView message;

	@InjectExtra(Fragments.Arguments.MESSAGE)
	String messageText;

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

		setUpInjections();

		setUpMessage();
	}

	private void setUpInjections() {
		ButterKnife.inject(this, getView());

		Dart.inject(this, getArguments());
	}

	private void setUpMessage() {
		message.setText(messageText);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset(this);
	}
}
