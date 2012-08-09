package app.android.bustime.ui.activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.ArrayAdapter;
import app.android.bustime.R;
import app.android.bustime.db.model.Route;
import app.android.bustime.db.model.Station;
import app.android.bustime.db.time.Time;
import app.android.bustime.ui.fragment.TimetableFragment;
import app.android.bustime.ui.intent.IntentException;
import app.android.bustime.ui.intent.IntentExtras;
import app.android.bustime.ui.loader.Loaders;
import app.android.bustime.ui.loader.TimetableTypeCheckLoader;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;


public class TimetableActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener, LoaderManager.LoaderCallbacks<Bundle>
{
	private static final int LIST_NAVIGATION_WORKDAYS_ITEM_INDEX = 0;
	private static final int LIST_NAVIGATION_WEEKEND_ITEM_INDEX = 1;

	private Route route;
	private Station station;

	private Fragment fullWeekTimetableFragment;
	private Fragment workdaysTimetableFragment;
	private Fragment weekendTimetableFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		readReceivedRoute();
		readReceivedStation();

		setUpTimetable();
	}

	private void readReceivedRoute() {
		route = getIntent().getParcelableExtra(IntentExtras.ROUTE);

		if (route == null) {
			throw new IntentException();
		}
	}

	private void readReceivedStation() {
		station = getIntent().getParcelableExtra(IntentExtras.STATION);

		if (route == null) {
			throw new IntentException();
		}
	}

	private void setUpTimetable() {
		getSupportLoaderManager().initLoader(Loaders.TIMETABLE_TYPE_CHECK_ID, null, this);
	}

	@Override
	public Loader<Bundle> onCreateLoader(int i, Bundle bundle) {
		return new TimetableTypeCheckLoader(this, route);
	}

	@Override
	public void onLoadFinished(Loader<Bundle> timetableTypeCheckLoader, Bundle timetableTypeCheckResult) {
		buildTimetableFragments();

		boolean isTimetableWeekPartDependent = timetableTypeCheckResult.getBoolean(
			TimetableTypeCheckLoader.RESULT_TIMETABLE_WEEK_PART_DEPENDENT_KEY);

		setUpTimetableLoaderSafe(isTimetableWeekPartDependent);
	}

	private void setUpTimetableLoaderSafe(final boolean isTimetableWeekPartDependent) {
		Handler handler = new Handler();

		handler.post(new Runnable()
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
		});
	}

	@Override
	public void onLoaderReset(Loader<Bundle> bundleLoader) {
	}

	private void buildTimetableFragments() {
		fullWeekTimetableFragment = TimetableFragment.newFullWeekInstance(route, station);
		workdaysTimetableFragment = TimetableFragment.newWorkdaysInstance(route, station);
		weekendTimetableFragment = TimetableFragment.newWeekendInstance(route, station);
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
		if (itemPosition == LIST_NAVIGATION_WORKDAYS_ITEM_INDEX) {
			tearDownFragment(weekendTimetableFragment);
			setUpFragment(workdaysTimetableFragment);
		}
		else {
			tearDownFragment(workdaysTimetableFragment);
			setUpFragment(weekendTimetableFragment);
		}

		return true;
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
			listNavigationIndex = LIST_NAVIGATION_WEEKEND_ITEM_INDEX;
		}
		else {
			listNavigationIndex = LIST_NAVIGATION_WORKDAYS_ITEM_INDEX;
		}

		getSupportActionBar().setSelectedNavigationItem(listNavigationIndex);
	}

	private void setUpWeekPartIndependentTimetable() {
		setUpFragment(fullWeekTimetableFragment);
	}
}
