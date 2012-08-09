package app.android.bustime.ui.activity;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import app.android.bustime.R;
import app.android.bustime.db.model.Route;
import app.android.bustime.db.model.Station;
import app.android.bustime.db.time.Time;
import app.android.bustime.ui.fragment.TimetableFragment;
import app.android.bustime.ui.intent.IntentException;
import app.android.bustime.ui.intent.IntentExtras;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;


public class TimetableActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener
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
		new SetUpTimetableTask().execute();
	}

	// TODO: Move to loader
	private class SetUpTimetableTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... voids) {
			return Boolean.valueOf(route.isWeekPartDependent());
		}

		@Override
		protected void onPostExecute(Boolean isRouteWeekPartDependent) {
			super.onPostExecute(isRouteWeekPartDependent);

			buildTimetableFragments();

			if (isRouteWeekPartDependent) {
				setUpWeekPartDependentTimetable();
			}
			else {
				setUpWeekPartIndependentTimetable();
			}
		}
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
