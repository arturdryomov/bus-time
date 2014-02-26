package ru.ming13.bustime.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.DatabaseUpdateAcceptedEvent;
import ru.ming13.bustime.bus.DatabaseUpdateDiscardedEvent;

public class DatabaseUpdateBanner extends Fragment implements View.OnClickListener
{
	public static final String TAG = "database_update_banner";

	public static DatabaseUpdateBanner newInstance() {
		return new DatabaseUpdateBanner();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_banner_database_update, container, false);
	}

	public void show(FragmentManager fragmentManager) {
		fragmentManager
			.beginTransaction()
			.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
			.add(android.R.id.content, this, TAG)
			.commit();
	}

	public void hide(FragmentManager fragmentManager) {
		fragmentManager
			.beginTransaction()
			.setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
			.remove(this)
			.commit();
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpButtonsListener();
	}

	private void setUpButtonsListener() {
		getView().findViewById(R.id.button_accept).setOnClickListener(this);
		getView().findViewById(R.id.button_discard).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_accept:
				BusProvider.getBus().post(new DatabaseUpdateAcceptedEvent());
				break;

			case R.id.button_discard:
				BusProvider.getBus().post(new DatabaseUpdateDiscardedEvent());
				break;

			default:
				break;
		}
	}
}
