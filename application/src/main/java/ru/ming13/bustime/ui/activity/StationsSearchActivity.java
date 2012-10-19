package ru.ming13.bustime.ui.activity;


import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.util.FragmentWrapper;


public class StationsSearchActivity extends SherlockFragmentActivity
{
	private static final boolean USE_SEARCH_IN_SEARCH_ACTIVITY = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		handleIntent();
	}

	private void handleIntent() {
		if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			setUpSearchResultsFragment();
			return;
		}

		if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
			callRoutesActivityForSelectedSuggestion();
			return;
		}

		throw new IntentException();
	}

	private void setUpSearchResultsFragment() {
		FragmentWrapper.setUpFragment(this, buildSearchResultsFragment());
	}

	private Fragment buildSearchResultsFragment() {
		return StationsFragment.newSearchLoadingInstance(extractReceivedSearchQuery());
	}

	private String extractReceivedSearchQuery() {
		return getIntent().getStringExtra(SearchManager.QUERY);
	}

	private void callRoutesActivityForSelectedSuggestion() {
		long selectedSuggestionStationId = Long.parseLong(
			getIntent().getStringExtra(SearchManager.EXTRA_DATA_KEY));

		callRoutesActivity(selectedSuggestionStationId);
	}

	private void callRoutesActivity(long stationId) {
		Intent intent = IntentFactory.createRoutesIntent(this, stationId);
		startActivity(intent);

		finish();
	}

	@Override
	public boolean onSearchRequested() {
		return USE_SEARCH_IN_SEARCH_ACTIVITY;
	}
}
