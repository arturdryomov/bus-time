package ru.ming13.bustime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.fragment.MessageFragment;
import ru.ming13.bustime.fragment.StopRoutesFragment;
import ru.ming13.bustime.fragment.TimetableFragment;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.Maps;
import ru.ming13.bustime.util.TitleBuilder;

public class StopRoutesActivity extends ActionBarActivity
{
	@InjectView(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);

		setUpInjections();

		setUpUi();
	}

	private void setUpInjections() {
		ButterKnife.inject(this);
	}

	private void setUpUi() {
		setUpToolbar();

		if (Frames.at(this).areAvailable()) {
			setUpTitle();
			setUpFrameTitles();
			setUpMessageFragment();
		} else {
			setUpSubtitle();
		}

		setUpRoutesFragment();
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(getStopTitle());
	}

	private String getStopTitle() {
		return TitleBuilder.with(this).buildStopTitle(getStop());
	}

	private Stop getStop() {
		return getIntent().getParcelableExtra(Intents.Extras.STOP);
	}

	private void setUpFrameTitles() {
		Frames.at(this).setLeftFrameTitle(R.string.title_routes);
		Frames.at(this).setRightFrameTitle(R.string.title_timetable);
	}

	private void setUpMessageFragment() {
		Fragments.Operator.at(this).set(getMessageFragment(), R.id.container_right_frame);
	}

	private Fragment getMessageFragment() {
		return MessageFragment.newInstance(getString(R.string.message_no_route));
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(getStopTitle());
	}

	private void setUpRoutesFragment() {
		if (Frames.at(this).areAvailable()) {
			Fragments.Operator.at(this).set(getRoutesFragment(), R.id.container_left_frame);
		} else {
			Fragments.Operator.at(this).set(getRoutesFragment(), R.id.container_fragment);
		}
	}

	private Fragment getRoutesFragment() {
		return StopRoutesFragment.newInstance(getStop());
	}

	@Subscribe
	public void onRouteSelected(RouteSelectedEvent event) {
		setUpTimetable(event.getRoute());
	}

	private void setUpTimetable(Route route) {
		if (Frames.at(this).areAvailable()) {
			setUpTimetableFragment(route);
		} else {
			startTimetableActivity(route);
		}
	}

	private void setUpTimetableFragment(Route route) {
		if (Fragments.Operator.at(this).get(R.id.container_right_frame) instanceof MessageFragment) {
			Fragments.Operator.at(this).resetSliding(getTimetableFragment(route), R.id.container_right_frame);
		} else {
			Fragments.Operator.at(this).resetFading(getTimetableFragment(route), R.id.container_right_frame);
		}
	}

	private Fragment getTimetableFragment(Route route) {
		return TimetableFragment.newInstance(route, getStop());
	}

	private void startTimetableActivity(Route route) {
		Intent intent = Intents.Builder.with(this).buildTimetableIntent(route, getStop());
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_stop_routes, menu);

		setUpStopMap(menu);

		return super.onCreateOptionsMenu(menu);
	}

	private void setUpStopMap(Menu menu) {
		if (!Maps.at(this).areHardwareAvailable()) {
			menu.findItem(R.id.menu_map).setVisible(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				finish();
				return true;

			case R.id.menu_map:
				startStopMapActivity();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void startStopMapActivity() {
		if (Maps.at(this).areSoftwareAvailable()) {
			Intent intent = Intents.Builder.with(this).buildStopMapIntent(getStop());
			startActivity(intent);
		} else {
			Maps.at(this).showErrorDialog();
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
