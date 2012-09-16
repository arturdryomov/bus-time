package ru.ming13.bustime.ui.activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;
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


public class TimetableActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener, LoaderManager.LoaderCallbacks<Boolean>
{
	private static final class SavedInstanceKeys
	{
		private SavedInstanceKeys() {
		}

		public static final String SELECTED_LIST_NAVIGATION_INDEX = "list_navigation_index";
	}

	private static final class ListNavigationIndices
	{
		private ListNavigationIndices() {
		}

		public static final int WORKDAYS = 0;
		public static final int WEEKEND = 1;
	}

	private Route route;
	private Station station;

	private int selectedNavigationItemIndex = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		route = extractReceivedRoute();
		station = extractReceivedStation();

		setUpTimetable();

		if (isSavedInstanceStateValid(savedInstanceState)) {
			restorePreviousSelectedListNavigationIndex(savedInstanceState);
		}
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
		getSupportLoaderManager().initLoader(Loaders.TIMETABLE_TYPE_CHECK, null, this);
	}

	@Override
	public Loader<Boolean> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new TimetableTypeCheckLoader(this, route);
	}

	@Override
	public void onLoadFinished(Loader<Boolean> timetableTypeCheckLoader, Boolean isTimetableWeekPartDependent) {
		setUpTimetableLoaderSafe(isTimetableWeekPartDependent);
	}

	private void setUpTimetableLoaderSafe(final boolean isTimetableWeekPartDependent) {
		// Loaders don’t allow to run transactions on load finished

		Runnable loaderRunnable = new Runnable()
		{
			@Override
			public void run() {
				if (isTimetableWeekPartDependent) {
					setUpWeekPartDependentTimetable();
				}
				else {
					setUpWeekPartIndependentTimetable();
				}
			}
		};

		new Handler().post(loaderRunnable);
	}

	@Override
	public void onLoaderReset(Loader<Boolean> timetableTypeCheckLoader) {
	}

	private void setUpWeekPartDependentTimetable() {
		setUpActionBarListNavigation();

		setUpWeekPartDependentTimetableFragment();

		if (!isPreviousSelectedListNavigationItemValid()) {
			setCurrentWeekPartListNavigationItem();
		}
		else {
			setPreviousSelectedListNavigationItem();
		}
	}

	private void setUpActionBarListNavigation() {
		ActionBar actionBar = getSupportActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(false);

		actionBar.setListNavigationCallbacks(buildListNavigationAdapter(), this);
	}

	private ArrayAdapter<CharSequence> buildListNavigationAdapter() {
		Context themedContext = getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> listNavigationAdapter = ArrayAdapter.createFromResource(
			themedContext, R.array.week_part_dependent_timetable, R.layout.sherlock_spinner_item);
		listNavigationAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		return listNavigationAdapter;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		TimetableFragment timetableFragment = (TimetableFragment) getSupportFragmentManager().findFragmentById(
			android.R.id.content);

		switch (itemPosition) {
			case ListNavigationIndices.WORKDAYS:
				selectedNavigationItemIndex = ListNavigationIndices.WORKDAYS;
				timetableFragment.loadWorkdaysTimetable();
				return true;

			case ListNavigationIndices.WEEKEND:
				selectedNavigationItemIndex = ListNavigationIndices.WEEKEND;
				timetableFragment.loadWeekendTimetable();
				return true;

			default:
				return false;
		}
	}

	private void setUpWeekPartDependentTimetableFragment() {
		if (Time.newInstance().isWeekend()) {
			setUpFragment(TimetableFragment.newWeekendInstance(route, station));
		}
		else {
			setUpFragment(TimetableFragment.newWorkdaysInstance(route, station));
		}
	}

	private void setUpFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.replace(android.R.id.content, fragment);

		fragmentTransaction.commit();
	}

	private void setCurrentWeekPartListNavigationItem() {
		int listNavigationIndex;

		if (Time.newInstance().isWeekend()) {
			listNavigationIndex = ListNavigationIndices.WEEKEND;
		}
		else {
			listNavigationIndex = ListNavigationIndices.WORKDAYS;
		}

		getSupportActionBar().setSelectedNavigationItem(listNavigationIndex);
	}

	private void setPreviousSelectedListNavigationItem() {
		if (isPreviousSelectedListNavigationItemValid()) {
			getSupportActionBar().setSelectedNavigationItem(selectedNavigationItemIndex);
		}
	}

	private boolean isPreviousSelectedListNavigationItemValid() {
		return selectedNavigationItemIndex >= 0;
	}

	private void setUpWeekPartIndependentTimetable() {
		setUpFragment(TimetableFragment.newFullWeekInstance(route, station));
	}

	private boolean isSavedInstanceStateValid(Bundle savedInstanceState) {
		return savedInstanceState != null && savedInstanceState.containsKey(
			SavedInstanceKeys.SELECTED_LIST_NAVIGATION_INDEX);
	}

	private void restorePreviousSelectedListNavigationIndex(Bundle savedInstanceState) {
		selectedNavigationItemIndex = savedInstanceState.getInt(
			SavedInstanceKeys.SELECTED_LIST_NAVIGATION_INDEX);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SavedInstanceKeys.SELECTED_LIST_NAVIGATION_INDEX, selectedNavigationItemIndex);
	}
}
