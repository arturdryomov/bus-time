package ru.ming13.bustime.ui.activity;


import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.db.time.Time;
import ru.ming13.bustime.ui.fragment.TimetableFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.TimetableTypeCheckLoader;
import ru.ming13.bustime.ui.util.FragmentWrapper;
import ru.ming13.bustime.ui.util.NameParser;


public class TimetableActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Boolean>
{
	private static enum Mode
	{
		UNDEFINED, FULL_WEEK, WORKDAYS, WEEKEND
	}

	private Mode mode = Mode.UNDEFINED;

	private Route route;
	private Station station;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		route = extractReceivedRoute();
		station = extractReceivedStation();

		setUpActionBarTitles();

		setUpTimetable();
	}

	private Route extractReceivedRoute() {
		Route route = getIntent().getParcelableExtra(IntentExtras.ROUTE);

		if (route == null) {
			throw new IntentException();
		}

		return route;
	}

	private Station extractReceivedStation() {
		Station station = getIntent().getParcelableExtra(IntentExtras.STATION);

		if (station == null) {
			throw new IntentException();
		}

		return station;
	}

	private void setUpActionBarTitles() {
		getSupportActionBar().setTitle(NameParser.parseRouteNumber(route.getName()));
		getSupportActionBar().setSubtitle(station.getName());
	}

	private void setUpTimetable() {
		FragmentWrapper.setUpFragment(this, TimetableFragment.newEmptyInstance(route, station));

		getSupportLoaderManager().initLoader(Loaders.TIMETABLE_TYPE_CHECK, null, this);
	}

	@Override
	public Loader<Boolean> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new TimetableTypeCheckLoader(this, route);
	}

	@Override
	public void onLoadFinished(Loader<Boolean> timetableTypeCheckLoader, Boolean isTimetableWeekPartDependent) {
		if (isTimetableWeekPartDependent) {
			setUpWeekPartDependentTimetableMode();
		}
		else {
			setUpWeekPartIndependentTimetableMode();
		}

		loadTimetable();

		setUpActionBarButtons();
	}

	@Override
	public void onLoaderReset(Loader<Boolean> timetableTypeCheckLoader) {
	}

	private void setUpWeekPartDependentTimetableMode() {
		if (Time.newInstance().isWeekend()) {
			mode = Mode.WEEKEND;
		}
		else {
			mode = Mode.WORKDAYS;
		}
	}

	private void setUpWeekPartIndependentTimetableMode() {
		mode = Mode.FULL_WEEK;
	}

	private void loadTimetable() {
		TimetableFragment timetableFragment = (TimetableFragment) getSupportFragmentManager().findFragmentById(
			android.R.id.content);

		switch (mode) {
			case FULL_WEEK:
				timetableFragment.loadFullWeekTimetable();
				break;

			case WORKDAYS:
				timetableFragment.loadWorkdaysTimetable();
				break;

			case WEEKEND:
				timetableFragment.loadWeekendTimetable();
				break;

			default:
				timetableFragment.loadFullWeekTimetable();
				break;
		}
	}

	private void setUpActionBarButtons() {
		supportInvalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_action_bar_timetable, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		switch (mode) {
			case WORKDAYS:
				menu.findItem(R.id.menu_timetable_workdays).setChecked(true);
				return true;

			case WEEKEND:
				menu.findItem(R.id.menu_timetable_weekend).setChecked(true);
				return true;

			default:
				return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.isChecked()) {
			return false;
		}

		menuItem.setChecked(true);

		switch (menuItem.getItemId()) {
			case R.id.menu_timetable_weekend:
				mode = Mode.WEEKEND;
				loadTimetable();
				return true;

			case R.id.menu_timetable_workdays:
				mode = Mode.WORKDAYS;
				loadTimetable();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
}
