package app.android.bustime.ui;


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
import app.android.bustime.R;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.ui.loader.Loaders;
import app.android.bustime.ui.loader.StationsLoader;


public class StationsFragment extends AdaptedListFragment implements LoaderManager.LoaderCallbacks<List<Station>>
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_one_line,
			new String[] {LIST_ITEM_TEXT_ID}, new int[] {R.id.text});
	}

	@Override
	protected Map<String, Object> buildListItem(Object itemObject) {
		Station station = (Station) itemObject;

		Map<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, station);
		listItem.put(LIST_ITEM_TEXT_ID, station.getName());

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		setEmptyListText(getString(R.string.loading_stations));

		getLoaderManager().initLoader(Loaders.STATIONS_ID, getArguments(), this);
	}

	@Override
	public Loader<List<Station>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		if (!FragmentProcessor.haveMessage(getArguments())) {
			return new StationsLoader(getActivity());
		}
		else {
			// TODO: Extract message on fragment creation
			Route route = (Route) FragmentProcessor.extractMessage(getArguments());

			return new StationsLoader(getActivity(), route);
		}
	}


	@Override
	public void onLoadFinished(Loader<List<Station>> stationsLoader, List<Station> stations) {
		if (stations.isEmpty()) {
			setEmptyListText(getString(R.string.empty_stations));
		}
		else {
			populateList(stations);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Station>> stationsLoader) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Station selectedStation = (Station) getListItemObject(position);

		if (FragmentProcessor.haveMessage(getArguments())) {
			callTimetableActivity(selectedStation);
		}
		else {
			callRoutesActivity(selectedStation);
		}
	}

	private void callTimetableActivity(Station station) {
		Route route = (Route) FragmentProcessor.extractMessage(getArguments());

		Intent callIntent = IntentFactory.createTimetableIntent(getActivity(), route, station);
		startActivity(callIntent);
	}

	private void callRoutesActivity(Station station) {
		Intent callIntent = IntentFactory.createRoutesIntent(getActivity(), station);
		startActivity(callIntent);
	}
}
