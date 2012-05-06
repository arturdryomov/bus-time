package app.android.bustime.ui.dispatch.stations;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.local.DbProvider;
import app.android.bustime.local.Station;
import app.android.bustime.ui.IntentFactory;
import app.android.bustime.ui.SimpleAdapterListActivity;


public class StationsListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations);

		initializeActionbar();
		initializeList();
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.item_creation_button);
		itemCreationButton.setOnClickListener(stationCreationListener);
	}

	private final OnClickListener stationCreationListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			callStationCreation();
		}

		private void callStationCreation() {
			Intent callIntent = DispatchStationsIntentFactory
				.createStationCreationIntent(activityContext);
			startActivity(callIntent);
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter stationsAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.list_item_one_line, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

		setListAdapter(stationsAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		registerForContextMenu(getListView());
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadStations();
	}

	private void loadStations() {
		new LoadStationsTask().execute();
	}

	private class LoadStationsTask extends AsyncTask<Void, Void, Void>
	{
		private List<Station> stations;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loading_stations));
		}

		@Override
		protected Void doInBackground(Void... params) {
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
				fillList(stations);
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Station station = (Station) itemData;

		HashMap<String, Object> stationItem = new HashMap<String, Object>();

		stationItem.put(LIST_ITEM_TEXT_ID, station.getName());
		stationItem.put(LIST_ITEM_OBJECT_ID, station);

		listData.add(stationItem);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);

		getMenuInflater().inflate(R.menu.dispatch_stations_stations_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int stationPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.rename:
				callStationRenaming(stationPosition);
				return true;
			case R.id.delete:
				callStationDeleting(stationPosition);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callStationRenaming(int stationPosition) {
		Station station = getStation(stationPosition);

		Intent callIntent = IntentFactory.createStationRenamingIntent(activityContext, station);
		startActivity(callIntent);
	}

	private Station getStation(int stationPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(stationPosition);

		return (Station) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	private void callStationDeleting(int stationPosition) {
		new DeleteStationTask(stationPosition).execute();
	}

	private class DeleteStationTask extends AsyncTask<Void, Void, Void>
	{
		private final int stationPosition;
		private final Station station;

		public DeleteStationTask(int stationPosition) {
			super();

			this.stationPosition = stationPosition;
			this.station = getStation(stationPosition);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			listData.remove(stationPosition);
			updateList();

			if (listData.isEmpty()) {
				setEmptyListText(getString(R.string.empty_stations));
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			DbProvider.getInstance().getStations().deleteStation(station);

			return null;
		}
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		callRoutesList(position);
	}

	private void callRoutesList(int stationPosition) {
		Station station = getStation(stationPosition);

		Intent callIntent = DispatchStationsIntentFactory.createRoutesListIntent(activityContext,
			station);
		startActivity(callIntent);
	}
}
