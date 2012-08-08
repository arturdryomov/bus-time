package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;


public class StationsFragment extends AdaptedListFragment implements LoaderManager.LoaderCallbacks<List<Station>>
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	private static final int STATIONS_LOADER_ID = 0;

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

		getLoaderManager().initLoader(STATIONS_LOADER_ID, getArguments(), this);
	}

	@Override
	public Loader<List<Station>> onCreateLoader(int loaderId, Bundle loaderArguments) {
		switch (loaderId) {
			case STATIONS_LOADER_ID:
				return new StationsLoader(getActivity(), loaderArguments);

			default:
				throw new LoaderException();
		}
	}

	private static class StationsLoader extends AsyncTaskLoader<List<Station>>
	{
		private final Bundle fragmentArguments;

		public StationsLoader(Context context, Bundle fragmentArguments) {
			super(context);

			this.fragmentArguments = fragmentArguments;
		}

		@Override
		protected void onStartLoading() {
			super.onStartLoading();

			forceLoad();
		}

		@Override
		public List<Station> loadInBackground() {
			if (FragmentProcessor.haveMessage(fragmentArguments)) {
				Route route = (Route) FragmentProcessor.extractMessage(fragmentArguments);

				return DbProvider.getInstance().getStations().getStationsList(route);
			}
			else {
				return DbProvider.getInstance().getStations().getStationsList();
			}
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
