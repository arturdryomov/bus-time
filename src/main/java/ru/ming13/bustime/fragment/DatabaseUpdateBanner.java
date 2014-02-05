package ru.ming13.bustime.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.DatabaseUpdateAcceptedEvent;
import ru.ming13.bustime.bus.DatabaseUpdateDiscardedEvent;

public class DatabaseUpdateBanner extends Fragment implements View.OnClickListener
{
	public static final String TAG = "DATABASE_UPDATES_BANNER";

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
			.setCustomAnimations(R.anim.slide_in_banner_updates, R.anim.slide_out_banner_updates)
			.add(android.R.id.content, this, TAG)
			.commit();
	}

	public void hide(FragmentManager fragmentManager) {
		fragmentManager
			.beginTransaction()
			.setCustomAnimations(R.anim.slide_in_banner_updates, R.anim.slide_out_banner_updates)
			.remove(this)
			.commit();
	}

	@Override
	public void onStart() {
		super.onStart();

		setUpButtonsListener();
	}

	private void setUpButtonsListener() {
		ImageButton acceptButton = (ImageButton) getView().findViewById(R.id.button_accept);
		ImageButton discardButton = (ImageButton) getView().findViewById(R.id.button_discard);

		acceptButton.setOnClickListener(this);
		discardButton.setOnClickListener(this);
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
