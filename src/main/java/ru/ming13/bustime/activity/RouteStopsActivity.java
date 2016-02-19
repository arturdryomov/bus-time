package ru.ming13.bustime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.fragment.MessageFragment;
import ru.ming13.bustime.fragment.RouteStopsFragment;
import ru.ming13.bustime.fragment.TimetableFragment;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Intents;
import ru.ming13.bustime.util.Maps;
import ru.ming13.bustime.util.TitleBuilder;

public final class RouteStopsActivity extends AppCompatActivity
{
	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);

		setUpBindings();

		setUpUi();
	}

	private void setUpBindings() {
		ButterKnife.bind(this);
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

		setUpStopsFragment();
	}

	private void setUpToolbar() {
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void setUpTitle() {
		getSupportActionBar().setTitle(getRouteTitle());
	}

	private String getRouteTitle() {
		return TitleBuilder.with(this).buildRouteTitle(getRoute());
	}

	private Route getRoute() {
		return getIntent().getParcelableExtra(Intents.Extras.ROUTE);
	}

	private void setUpFrameTitles() {
		Frames.at(this).setLeftFrameTitle(R.string.title_stops);
		Frames.at(this).setRightFrameTitle(R.string.title_timetable);
	}

	private void setUpMessageFragment() {
		Fragments.Operator.at(this).set(getMessageFragment(), R.id.container_right_frame);
	}

	private Fragment getMessageFragment() {
		return MessageFragment.newInstance(getString(R.string.message_no_stop));
	}

	private void setUpSubtitle() {
		getSupportActionBar().setSubtitle(getRouteTitle());
	}

	private void setUpStopsFragment() {
		if (Frames.at(this).areAvailable()) {
			Fragments.Operator.at(this).set(getStopsFragment(), R.id.container_left_frame);
		} else {
			Fragments.Operator.at(this).set(getStopsFragment(), R.id.container_fragment);
		}
	}

	private Fragment getStopsFragment() {
		return RouteStopsFragment.newInstance(getRoute());
	}

	@Subscribe
	public void onStopSelected(StopSelectedEvent event) {
		setUpTimetable(event.getStop());
	}

	private void setUpTimetable(Stop stop) {
		if (Frames.at(this).areAvailable()) {
			setUpTimetableFragment(stop);
		} else {
			startTimetableActivity(stop);
		}
	}

	private void setUpTimetableFragment(Stop stop) {
		if (Fragments.Operator.at(this).get(R.id.container_right_frame) instanceof MessageFragment) {
			Fragments.Operator.at(this).resetSliding(getTimetableFragment(stop), R.id.container_right_frame);
		} else {
			Fragments.Operator.at(this).resetFading(getTimetableFragment(stop), R.id.container_right_frame);
		}
	}

	private Fragment getTimetableFragment(Stop stop) {
		return TimetableFragment.newInstance(getRoute(), stop);
	}

	private void startTimetableActivity(Stop stop) {
		Intent intent = Intents.Builder.with(this).buildTimetableIntent(getRoute(), stop);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.action_bar_route_stops, menu);

		setUpRouteMap(menu);

		return super.onCreateOptionsMenu(menu);
	}

	private void setUpRouteMap(Menu menu) {
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
				startRouteMapActivity();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	private void startRouteMapActivity() {
		if (Maps.at(this).areSoftwareAvailable()) {
			Intent intent = Intents.Builder.with(this).buildRouteMapIntent(getRoute());
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
