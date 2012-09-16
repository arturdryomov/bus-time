package ru.ming13.bustime.ui.activity;


import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.Fragment;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentException;


public class StationsSearchActivity extends FragmentWrapperActivity
{
	private static final boolean USE_SEARCH_IN_SEARCH_ACTIVITY = false;

	@Override
	protected Fragment buildFragment() {
		return StationsFragment.newSearchLoadingInstance(extractReceivedSearchStationName());
	}

	private String extractReceivedSearchStationName() {
		if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			throw new IntentException();
		}

		return getIntent().getStringExtra(SearchManager.QUERY);
	}

	@Override
	public boolean onSearchRequested() {
		return USE_SEARCH_IN_SEARCH_ACTIVITY;
	}
}
