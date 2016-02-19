package ru.ming13.bustime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.fragment.StopsMapFragment;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Android;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Intents;

public class StopsMapActivity extends AppCompatActivity
{
	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container_map);

		setUpBindings();

		setUpBars();
		setUpToolbar();

		setUpMapFragment();
	}

	private void setUpBindings() {
		ButterKnife.bind(this);
	}

	private void setUpBars() {
		Bartender.at(this).showSystemBarsBackground();
	}

	private void setUpToolbar() {
		setUpToolbarPosition();

		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpToolbarPosition() {
		if (Android.isKitKatOrLater()) {
			RelativeLayout.LayoutParams toolbarParams = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();

			toolbarParams.topMargin = Bartender.at(this).getStatusBarHeight();

			toolbar.setLayoutParams(toolbarParams);
		}
	}

	private void setUpMapFragment() {
		Fragments.Operator.at(this).set(getMapFragment(), R.id.container_fragment);
	}

	private Fragment getMapFragment() {
		return StopsMapFragment.newInstance();
	}

	@Subscribe
	public void onStopSelected(StopSelectedEvent event) {
		startStopRoutesActivity(event.getStop());
	}

	private void startStopRoutesActivity(Stop stop) {
		Intent intent = Intents.Builder.with(this).buildStopRoutesIntent(stop);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
