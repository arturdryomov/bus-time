package app.android.bustime.ui;


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
import app.android.bustime.local.DbException;
import app.android.bustime.local.DbProvider;
import app.android.bustime.local.Route;
import app.android.bustime.local.Station;


public class StationsListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	private Route route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stations);

		processReceivedRoute();

		initializeActionbar();
		initializeList();
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		itemCreationButton.setOnClickListener(stationCreationListener);
	}

	private final OnClickListener stationCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callStationCreation();
		}

		private void callStationCreation() {
			Intent callIntent = IntentFactory.createStationCreationIntent(activityContext, route);
			activityContext.startActivity(callIntent);
		}
	};

	private void processReceivedRoute() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.someError));

			finish();
		}
	}

	@Override
	protected void initializeList() {
		SimpleAdapter stationsAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.stations_list_item, new String[] { LIST_ITEM_TEXT_ID }, new int[] { R.id.text });

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

	private class LoadStationsTask extends AsyncTask<Void, Void, String>
	{
		private List<Station> stations;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loadingStations));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				stations = DbProvider.getInstance().getStations().getStationsByRoute(route);
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (stations.isEmpty()) {
				setEmptyListText(getString(R.string.noStations));
			}
			else {
				fillList(stations);
				updateList();
			}

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
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

		getMenuInflater().inflate(R.menu.stations_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int stationPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.edit:
				// TODO: callStationEditing(stationPosition);
				return true;
			case R.id.delete:
				callStationDeleting(stationPosition);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void callStationDeleting(int stationPosition) {
		new DeleteStationTask(stationPosition).execute();
	}

	private class DeleteStationTask extends AsyncTask<Void, Void, String>
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
				setEmptyListText(getString(R.string.noStations));
			}
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				DbProvider.getInstance().getStations().deleteStation(station);
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private Station getStation(int stationPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> adapterItem = (Map<String, Object>) listAdapter.getItem(stationPosition);

		return (Station) adapterItem.get(LIST_ITEM_OBJECT_ID);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		callTimetable(position);
	}

	private void callTimetable(int stationPosition) {
		Station station = getStation(stationPosition);

		Intent callIntent = IntentFactory.createTimetableIntent(activityContext, route, station);
		startActivity(callIntent);
	}
}
