package ru.ming13.bustime.ui.activity;


import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.fragment.StationsFragment;
import ru.ming13.bustime.ui.intent.IntentException;
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.StationLoader;
import ru.ming13.bustime.ui.util.FragmentWrapper;


public class StationsSearchActivity extends SherlockFragmentActivity implements LoaderManager.LoaderCallbacks<Station>
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
		return StationsFragment.newSearchLoadingInstance(extractReceivedSearchStationName());
	}

	private String extractReceivedSearchStationName() {
		return getIntent().getStringExtra(SearchManager.QUERY);
	}

	private void callRoutesActivityForSelectedSuggestion() {
		getSupportLoaderManager().initLoader(Loaders.STATION, getIntent().getExtras(), this);
	}

	@Override
	public Loader<Station> onCreateLoader(int loaderId, Bundle loaderArguments) {
		long selectedSuggestionStationId = Long.parseLong(
			loaderArguments.getString(SearchManager.EXTRA_DATA_KEY));

		return new StationLoader(this, selectedSuggestionStationId);
	}

	@Override
	public void onLoadFinished(Loader<Station> stationLoader, Station station) {
		callRoutesActivity(station);
	}

	private void callRoutesActivity(Station station) {
		Intent intent = IntentFactory.createRoutesIntent(this, station);
		startActivity(intent);

		finish();
	}

	@Override
	public void onLoaderReset(Loader<Station> stationLoader) {
	}

	@Override
	public boolean onSearchRequested() {
		return USE_SEARCH_IN_SEARCH_ACTIVITY;
	}
}
