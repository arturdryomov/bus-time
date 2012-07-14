package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Station;


public class StationsFragment extends AdaptedListFragment
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
		new PopulateListTask().execute();
	}

	private class PopulateListTask extends AsyncTask<Void, Void, Void>
	{
		private List<Station> stations;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loading_stations));
		}

		@Override
		protected Void doInBackground(Void... parameters) {
			stations = DbProvider.getInstance().getStations().getStationsList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (stations.isEmpty()) {
				setEmptyListText(getString(R.string.empty_stations));
			}
			else {
				populateList(stations);
			}
		}
	}
}
