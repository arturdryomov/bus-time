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
	private static final class ListNavigationIndices
	{
		private ListNavigationIndices() {
		}

		public static final int WORKDAYS = 0;
		public static final int WEEKEND = 1;
	}

	private Route route;
	private Station station;

	private Fragment fullWeekTimetableFragment;
	private Fragment workdaysTimetableFragment;
	private Fragment weekendTimetableFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		getSupportLoaderManager().initLoader(Loaders.TIMETABLE_TYPE_CHECK, null, this);
	}

	@Override
	public Loader<Boolean> onCreateLoader(int i, Bundle bundle) {
		return new TimetableTypeCheckLoader(this, route);
	}

	@Override
	public void onLoadFinished(Loader<Boolean> timetableTypeCheckLoader, Boolean isTimetableWeekPartDependent) {
		buildTimetableFragments();

		setUpTimetableLoaderSafe(isTimetableWeekPartDependent);
	}

	private void buildTimetableFragments() {
		fullWeekTimetableFragment = TimetableFragment.newFullWeekInstance(route, station);
		workdaysTimetableFragment = TimetableFragment.newWorkdaysInstance(route, station);
		weekendTimetableFragment = TimetableFragment.newWeekendInstance(route, station);
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

		setCurrentWeekPartListNavigationItem();
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
		switch (itemPosition) {
			case ListNavigationIndices.WORKDAYS:
				tearDownFragment(weekendTimetableFragment);
				setUpFragment(workdaysTimetableFragment);
				return true;

			case ListNavigationIndices.WEEKEND:
				tearDownFragment(workdaysTimetableFragment);
				setUpFragment(weekendTimetableFragment);
				return true;

			default:
				return false;
		}
	}

	private void tearDownFragment(Fragment fragment) {
		if (!fragment.isAdded()) {
			return;
		}

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.detach(fragment);

		fragmentTransaction.commit();
	}

	private void setUpFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		if (fragment.isDetached()) {
			fragmentTransaction.attach(fragment);
		}
		else {
			fragmentTransaction.replace(android.R.id.content, fragment);
		}

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

	private void setUpWeekPartIndependentTimetable() {
		setUpFragment(fullWeekTimetableFragment);
	}
}
