package ru.ming13.bustime.ui.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.ui.fragment.StationsForRouteFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentExtras;


public class StationsActivity extends FragmentWrapperActivity implements ActionBar.OnNavigationListener
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

		public static final int SORTING_BY_TIME_SHIFT = 0;
		public static final int SORTING_BY_NAME = 1;
	}

	@Override
	protected Fragment buildFragment() {
		return StationsForRouteFragment.newInstance(extractReceivedRoute());
	}

	private Route extractReceivedRoute() {
		Route route = getIntent().getParcelableExtra(IntentExtras.ROUTE);

		if (route == null) {
			throw new IntentException();
		}

		return route;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpActionBarListNavigation();

		if (isSavedInstanceStateValid(savedInstanceState)) {
			setSelectedListNavigationIndex(savedInstanceState);
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
			themedContext, R.array.titles_stations_with_sorting, R.layout.sherlock_spinner_item);
		listNavigationAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		return listNavigationAdapter;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		StationsForRouteFragment stationsForRouteFragment = (StationsForRouteFragment)
			getSupportFragmentManager().findFragmentById(android.R.id.content);

		switch (itemPosition) {
			case ListNavigationIndices.SORTING_BY_TIME_SHIFT:
				stationsForRouteFragment.sortByTimeShift();
				return true;

			case ListNavigationIndices.SORTING_BY_NAME:
				stationsForRouteFragment.sortByName();
				return true;

			default:
				return false;
		}
	}

	private boolean isSavedInstanceStateValid(Bundle savedInstanceState) {
		return savedInstanceState != null && savedInstanceState.containsKey(
			SavedInstanceKeys.SELECTED_LIST_NAVIGATION_INDEX);
	}

	private void setSelectedListNavigationIndex(Bundle savedInstanceState) {
		int selectedListNavigationIndex = savedInstanceState.getInt(
			SavedInstanceKeys.SELECTED_LIST_NAVIGATION_INDEX);

		getSupportActionBar().setSelectedNavigationItem(selectedListNavigationIndex);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(SavedInstanceKeys.SELECTED_LIST_NAVIGATION_INDEX,
			getSelectedListNavigationIndex());
	}

	private int getSelectedListNavigationIndex() {
		return getSupportActionBar().getSelectedNavigationIndex();
	}
}
