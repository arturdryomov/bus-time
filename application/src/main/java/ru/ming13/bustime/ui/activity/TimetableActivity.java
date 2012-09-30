package ru.ming13.bustime.ui.activity;


import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
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
import ru.ming13.bustime.ui.util.ListNavigationProvider;


public class TimetableActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener, LoaderManager.LoaderCallbacks<Boolean>
{
	private static enum Mode
	{
		FULL_WEEK, WORKDAYS, WEEKEND
	}

	private static final class ListNavigationIndexes
	{
		private ListNavigationIndexes() {
		}

		public static final int WORKDAYS = 0;
		public static final int WEEKEND = 1;
	}

	private ListNavigationProvider listNavigationProvider;

	private Route route;
	private Station station;

	private Bundle savedInstanceState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;

		route = extractReceivedRoute();
		station = extractReceivedStation();

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
			setUpWeekPartDependentTimetable();
		}
		else {
			setUpWeekPartIndependentTimetable();
		}
	}

	@Override
	public void onLoaderReset(Loader<Boolean> timetableTypeCheckLoader) {
	}

	private void setUpWeekPartDependentTimetable() {
		setUpListNavigation();

		if (listNavigationProvider.isStateValid(savedInstanceState)) {
			listNavigationProvider.restoreSelectedNavigationIndex(savedInstanceState);
		}
		else {
			setCurrentWeekPartListNavigationItem();
		}
	}

	private void setUpListNavigation() {
		listNavigationProvider = new ListNavigationProvider(this);

		getSupportActionBar().setDisplayShowTitleEnabled(false);
		listNavigationProvider.setUpListNavigation(this, R.array.week_part_dependent_timetable);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		switch (itemPosition) {
			case ListNavigationIndexes.WORKDAYS:
				loadTimetable(Mode.WORKDAYS);
				return true;

			case ListNavigationIndexes.WEEKEND:
				loadTimetable(Mode.WEEKEND);
				return true;

			default:
				return false;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		switch (getSupportActionBar().getNavigationMode()) {
			case ActionBar.NAVIGATION_MODE_LIST:
				listNavigationProvider.saveSelectedNavigationIndex(outState);
				break;

			default:
				break;
		}
	}

	private void loadTimetable(Mode mode) {
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

	private void setCurrentWeekPartListNavigationItem() {
		int listNavigationIndex;

		if (Time.newInstance().isWeekend()) {
			listNavigationIndex = ListNavigationIndexes.WEEKEND;
		}
		else {
			listNavigationIndex = ListNavigationIndexes.WORKDAYS;
		}

		getSupportActionBar().setSelectedNavigationItem(listNavigationIndex);
	}

	private void setUpWeekPartIndependentTimetable() {
		loadTimetable(Mode.FULL_WEEK);
	}
}
