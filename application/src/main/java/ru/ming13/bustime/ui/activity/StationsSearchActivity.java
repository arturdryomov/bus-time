package ru.ming13.bustime.ui.activity;


import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.util.FragmentWrapper;


public class StationsSearchActivity extends SherlockFragmentActivity
{
	private static final boolean USE_SEARCH_IN_SEARCH_ACTIVITY = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentWrapper.setUpFragment(this, buildFragment());
	}

	private Fragment buildFragment() {
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
