package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Route;


public class RoutesFragment extends AdaptedListFragment
{
	private static final String LIST_ITEM_TEXT_ID = "text";

	@Override
	protected SimpleAdapter buildListAdapter() {
		return new SimpleAdapter(getActivity(), list, R.layout.list_item_one_line,
			new String[] {LIST_ITEM_TEXT_ID}, new int[] {R.id.text});
	}

	@Override
	protected Map<String, Object> buildListItem(Object itemObject) {
		Route route = (Route) itemObject;

		Map<String, Object> listItem = new HashMap<String, Object>();

		listItem.put(LIST_ITEM_OBJECT_ID, route);
		listItem.put(LIST_ITEM_TEXT_ID, route.getName());

		return listItem;
	}

	@Override
	protected void callListPopulation() {
		new PopulateListTask().execute();
	}

	private class PopulateListTask extends AsyncTask<Void, Void, Void>
	{
		private List<Route> routes;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loading_routes));
		}

		@Override
		protected Void doInBackground(Void... parameters) {
			routes = DbProvider.getInstance().getRoutes().getRoutesList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (routes.isEmpty()) {
				setEmptyListText(getString(R.string.empty_routes));
			}
			else {
				populateList(routes);
			}
		}
	}
}
