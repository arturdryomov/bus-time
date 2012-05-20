package app.android.bustime.ui.dispatch.routes;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.ui.IntentFactory;
import app.android.bustime.ui.SimpleAdapterListActivity;
import app.android.bustime.ui.UserAlerter;


public class StationsListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private boolean areLoadedStationsNearby = false;

	private LocationManager locationManager;
	private final static int COORDINATES_REQUEST_CODE = 42;
	private Station stationForChangingLocation;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	private Route route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stations);

		processReceivedRoute();
		initializeLocationManager();

		initializeActionbar();
		initializeList();
	}

	private void processReceivedRoute() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

			finish();
		}
	}

	private void initializeLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.item_creation_button);
		itemCreationButton.setOnClickListener(stationCreationListener);

		ImageButton stationsNearbyButton = (ImageButton) findViewById(R.id.stations_nearby_button);
		stationsNearbyButton.setOnClickListener(stationsNearbyListener);
	}

	private final OnClickListener stationCreationListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			callStationCreation();
		}

		private void callStationCreation() {
			Intent callIntent = DispatchRoutesIntentFactory.createStationCreationIntent(activityContext,
				route);
			activityContext.startActivity(callIntent);
		}
	};

	private final OnClickListener stationsNearbyListener = new OnClickListener() {
		@Override
		public void onClick(View view) {

			if (areLoadedStationsNearby) {
				loadStations();

				stopLocationUpdates();

				areLoadedStationsNearby = false;
			}
			else {
				loadStationsNearby();

				areLoadedStationsNearby = true;
			}

			updateActionbarNearbyButtonIcon();
		}

		private void updateActionbarNearbyButtonIcon() {
			ImageButton nearbyButton = (ImageButton) findViewById(R.id.stations_nearby_button);

			if (areLoadedStationsNearby) {
				nearbyButton.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_menu_location_enabled));

			}
			else {
				nearbyButton.setImageDrawable(getResources().getDrawable(
					R.drawable.ic_menu_location_disabled));
			}
		}

	};

	private void loadStationsNearby() {
		emptyStationsList();
		setEmptyListText(getString(R.string.loading_location));

		loadLocation();
	}

	private void emptyStationsList() {
		listData.clear();
		updateList();
	}

	private void loadLocation() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			loadStationsNearby(location.getLatitude(), location.getLongitude());

			stopLocationUpdates();
		}
	};

	private void loadStationsNearby(double latitude, double longitude) {
		new LoadStationsByLocationTask(latitude, longitude).execute();
	}

	private class LoadStationsByLocationTask extends AsyncTask<Void, Void, Void>
	{
		private List<Station> stations;

		private final double latitude;
		private final double longitude;

		public LoadStationsByLocationTask(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

		@Override
		protected Void doInBackground(Void... params) {
			stations = DbProvider.getInstance().getStations().getStationsList(route, latitude, longitude);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (stations.isEmpty()) {
				setEmptyListText(getString(R.string.empty_stations_nearby));
			}
			else {
				fillList(stations);
			}
		}
	}

	private void stopLocationUpdates() {
		locationManager.removeUpdates(locationListener);
	}

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

		if (areLoadedStationsNearby) {
			loadStationsNearby();
		}
		else {
			loadStations();
		}
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
			stations = DbProvider.getInstance().getStations().getStationsList(route);

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

		getMenuInflater().inflate(R.menu.dispatch_routes_stations_context_menu_items, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo itemInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		int stationPosition = itemInfo.position;

		switch (item.getItemId()) {
			case R.id.rename_station:
				callStationRenaming(stationPosition);
				return true;
			case R.id.change_location:
				callStationLocationUpdating(stationPosition);
				return true;
			case R.id.change_shift_time:
				callShiftTimeEditing(stationPosition);
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

	private void callStationLocationUpdating(int stationPosition) {
		stationForChangingLocation = getStation(stationPosition);

		callStationLocationActivity();
	}

	private void callStationLocationActivity() {
		Intent callIntent = IntentFactory.createStationLocationIntent(activityContext,
			stationForChangingLocation.getLatitude(), stationForChangingLocation.getLongitude());
		startActivityForResult(callIntent, COORDINATES_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((resultCode == RESULT_OK) && (requestCode == COORDINATES_REQUEST_CODE)) {
			double latitude = data.getExtras().getDouble(IntentFactory.MESSAGE_ID);
			double longitude = data.getExtras().getDouble(IntentFactory.EXTRA_MESSAGE_ID);

			callStationUpdating(latitude, longitude);
		}
	}

	private void callStationUpdating(double latitude, double longitude) {
		new UpdateStationLocationTask(latitude, longitude).execute();
	}

	private class UpdateStationLocationTask extends AsyncTask<Void, Void, Void>
	{
		private final double latitude;
		private final double longitude;

		public UpdateStationLocationTask(double latitude, double longitude) {
			super();

			this.latitude = latitude;
			this.longitude = longitude;
		}

		@Override
		protected Void doInBackground(Void... params) {
			stationForChangingLocation.setLocation(latitude, longitude);

			return null;
		}
	}

	private void callShiftTimeEditing(int stationPosition) {
		Station station = getStation(stationPosition);

		Intent callIntent = IntentFactory.createShiftTimeEditingIntent(activityContext, route, station);
		startActivity(callIntent);
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

		callTimetable(position);
	}

	private void callTimetable(int stationPosition) {
		Station station = getStation(stationPosition);

		Intent callIntent = IntentFactory.createTimetableIntent(activityContext, route, station);
		startActivity(callIntent);
	}
}
