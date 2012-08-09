package app.android.bustime.ui;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ArrayAdapter;
import app.android.bustime.R;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.db.Time;
import app.android.bustime.ui.fragment.TimetableFragment;
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

		extractReceivedRouteAndStation();

		buildTimetableFragments();
		setUpTimetable();
	}

	private void extractReceivedRouteAndStation() {
		route = extractReceivedRoute();
		station = extractReceivedStation();
	}

	private Route extractReceivedRoute() {
		Bundle intentExtras = getIntent().getExtras();

		if (!IntentProcessor.haveMessage(intentExtras)) {
			UserAlerter.alert(this, getString(R.string.error_unspecified));
			finish();
		}

		return (Route) IntentProcessor.extractMessage(intentExtras);
	}

	private Station extractReceivedStation() {
		Bundle intentExtras = getIntent().getExtras();

		if (!IntentProcessor.haveExtraMessage(intentExtras)) {
			UserAlerter.alert(this, getString(R.string.error_unspecified));
			finish();
		}

		return (Station) IntentProcessor.extractExtraMessage(intentExtras);
	}

	private void buildTimetableFragments() {
		fullWeekTimetableFragment = TimetableFragment.newFullWeekInstance(route, station);
		workdaysTimetableFragment = TimetableFragment.newWorkdaysInstance(route, station);
		weekendTimetableFragment = TimetableFragment.newWeekendInstance(route, station);
	}

	private void setUpTimetable() {
		new SetUpTimetableTask().execute();
	}

	private class SetUpTimetableTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... voids) {
			return Boolean.valueOf(route.isWeekPartDependent());
		}

		@Override
		protected void onPostExecute(Boolean isRouteWeekPartDependent) {
			super.onPostExecute(isRouteWeekPartDependent);

			if (isRouteWeekPartDependent) {
				setUpWeekPartDependentTimetable();
			}
			else {
				setUpWeekPartIndependentTimetable();
			}
		}
	}

	private void setUpWeekPartDependentTimetable() {
		setUpActionBarListNavigation();

		setUpWeekPartDependentFragments();
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
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		if (itemPosition == LIST_NAVIGATION_WORKDAYS_ITEM_INDEX) {
			fragmentTransaction.detach(weekendTimetableFragment);
			fragmentTransaction.attach(workdaysTimetableFragment);
		}
		else {
			fragmentTransaction.detach(workdaysTimetableFragment);
			fragmentTransaction.attach(weekendTimetableFragment);
		}

		fragmentTransaction.commit();

		return true;
	}

	private void setUpWeekPartDependentFragments() {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.add(android.R.id.content, workdaysTimetableFragment);
		fragmentTransaction.add(android.R.id.content, weekendTimetableFragment);

		fragmentTransaction.detach(workdaysTimetableFragment);
		fragmentTransaction.detach(weekendTimetableFragment);

		fragmentTransaction.commit();
	}

	private void setCurrentWeekPartListNavigationItem() {
		int listNavigationIndex;

		if (Time.getInstance().isWeekend()) {
			listNavigationIndex = LIST_NAVIGATION_WEEKEND_ITEM_INDEX;
		}
		else {
			listNavigationIndex = LIST_NAVIGATION_WORKDAYS_ITEM_INDEX;
		}

		getSupportActionBar().setSelectedNavigationItem(listNavigationIndex);
	}

	private void setUpWeekPartIndependentTimetable() {
		if (!isFragmentSetUp()) {
			setUpFragment(fullWeekTimetableFragment);
		}
	}

	private boolean isFragmentSetUp() {
		return getSupportFragmentManager().findFragmentById(android.R.id.content) != null;
	}

	private void setUpFragment(Fragment fragment) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.add(android.R.id.content, fragment);

		fragmentTransaction.commit();
	}
}
