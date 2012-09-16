package ru.ming13.bustime.ui.fragment;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.model.Route;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.loader.Loaders;
import ru.ming13.bustime.ui.loader.StationsLoader;


public class StationsFragment extends AdaptedListFragment<Station> implements LoaderManager.LoaderCallbacks<List<Station>>
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	private static enum LoadingMode
	{
		ALL, FOR_ROUTE_SORTED_BY_NAME, FOR_ROUTE_SORTED_BY_TIME_SHIFT, SEARCH
	}

	private LoadingMode loadingMode;

	private Route route;

	private String searchQuery;

	public static StationsFragment newAllLoadingInstance() {
		StationsFragment stationsFragment = new StationsFragment();

		stationsFragment.setArguments(buildArguments(LoadingMode.ALL, null, null));

		return stationsFragment;
	}

	private static Bundle buildArguments(LoadingMode loadingMode, Route route, String searchQuery) {
		Bundle arguments = new Bundle();

		arguments.putSerializable(FragmentArguments.MODE, loadingMode);
		arguments.putParcelable(FragmentArguments.ROUTE, route);
		arguments.putString(FragmentArguments.SEARCH_QUERY, searchQuery);

		return arguments;
	}

	public static StationsFragment newForRouteSortedByNameLoadingInstance(Route route) {
		StationsFragment stationsFragment = new StationsFragment();

		stationsFragment.setArguments(
			buildArguments(LoadingMode.FOR_ROUTE_SORTED_BY_NAME, route, null));

		return stationsFragment;
	}

	public static StationsFragment newForRouteSortedByTimeShiftLoadingInstance(Route route) {
		StationsFragment stationsFragment = new StationsFragment();

		stationsFragment.setArguments(
			buildArguments(LoadingMode.FOR_ROUTE_SORTED_BY_TIME_SHIFT, route, null));

		return stationsFragment;
	}

	public static StationsFragment newSearchLoadingInstance(String searchQuery) {
		StationsFragment stationsFragment = new StationsFragment();

		stationsFragment.setArguments(buildArguments(LoadingMode.SEARCH, null, searchQuery));

		return stationsFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadingMode = (LoadingMode) getArguments().getSerializable(FragmentArguments.MODE);
		route = getArguments().getParcelable(FragmentArguments.ROUTE);
		searchQuery = getArguments().getString(FragmentArguments.SEARCH_QUERY);
	}

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_one_line,
			new String[] {LIST_ITEM_TEXT_ID}, new int[] {R.id.text});
	}

	@Override
	protected Map<String, Object> buildListItem(Station station) {
		Map<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, station);
		listItem.put(LIST_ITEM_TEXT_ID, station.getName());

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		setEmptyListText(R.string.loading_stations);

		getLoaderManager().initLoader(Loaders.STATIONS, getArguments(), this);
	}

	@Override
	public Loader<List<Station>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (loadingMode) {
			case ALL:
				return StationsLoader.newAllLoadingInstance(getActivity());

			case FOR_ROUTE_SORTED_BY_NAME:
				return StationsLoader.newForRouteSortedByNameLoadingInstance(getActivity(), route);

			case FOR_ROUTE_SORTED_BY_TIME_SHIFT:
				return StationsLoader.newForRouteSortedByTimeShiftLoadingInstance(getActivity(), route);

			case SEARCH:
				return StationsLoader.newSearchLoadingInstance(getActivity(), searchQuery);

			default:
				return StationsLoader.newAllLoadingInstance(getActivity());
		}
	}

	@Override
	public void onLoadFinished(Loader<List<Station>> stationsLoader, List<Station> stations) {
		if (stations.isEmpty()) {
			setEmptyListText(R.string.empty_stations);
		}
		else {
			populateList(stations);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Station>> stationsLoader) {
	}

	@Override
	public void callListRepopulation() {
		setEmptyListText(R.string.loading_stations);
		clearList();

		getLoaderManager().restartLoader(Loaders.STATIONS, null, this);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Station selectedStation = getListItemObject(position);

		switch (loadingMode) {
			case ALL:
				callRoutesActivity(selectedStation);
				return;

			case FOR_ROUTE_SORTED_BY_NAME:
				callTimetableActivity(selectedStation);
				return;

			case FOR_ROUTE_SORTED_BY_TIME_SHIFT:
				callTimetableActivity(selectedStation);
				return;

			case SEARCH:
				callRoutesActivity(selectedStation);
				return;

			default:
				callRoutesActivity(selectedStation);
		}
	}

	private void callTimetableActivity(Station station) {
		Intent callIntent = IntentFactory.createTimetableIntent(getActivity(), route, station);
		startActivity(callIntent);
	}

	private void callRoutesActivity(Station station) {
		Intent callIntent = IntentFactory.createRoutesIntent(getActivity(), station);
		startActivity(callIntent);
	}

	public void sortByName() {
		loadingMode = LoadingMode.FOR_ROUTE_SORTED_BY_NAME;

		callListRepopulation();
	}

	public void sortByTimeShift() {
		loadingMode = LoadingMode.FOR_ROUTE_SORTED_BY_TIME_SHIFT;

		callListRepopulation();
	}
}
