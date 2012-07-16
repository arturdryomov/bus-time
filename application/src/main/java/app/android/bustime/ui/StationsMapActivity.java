package app.android.bustime.ui;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import app.android.bustime.R;
import app.android.bustime.db.DbProvider;
import app.android.bustime.db.Station;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Window;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;


public class StationsMapActivity extends SherlockMapActivity
{
	private static final double MICRODEGREES_IN_DEGREE = 1E6;

	private static final int DEFAULT_MAP_ZOOM = 20;
	private static final boolean ZOOM_CONTROLS_ENABLED = true;

	private MyLocationOverlay myLocationOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_map);

		setUpMapView();
		setUpMyLocationOverlay();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void setUpMapView() {
		getMapView().setBuiltInZoomControls(ZOOM_CONTROLS_ENABLED);

		MapController mapController = getMapView().getController();
		mapController.setZoom(DEFAULT_MAP_ZOOM);
	}

	private MapView getMapView() {
		return (MapView) findViewById(R.id.map);
	}

	private void setUpMyLocationOverlay() {
		myLocationOverlay = new MyLocationOverlay(this, getMapView());

		myLocationOverlay.runOnFirstFix(new Runnable()
		{
			@Override
			public void run() {
				getMapView().getController().animateTo(myLocationOverlay.getMyLocation());
			}
		});

		getMapView().getOverlays().add(myLocationOverlay);
	}

	@Override
	protected void onResume() {
		super.onResume();

		populateMap();

		myLocationOverlay.enableMyLocation();
	}

	private void populateMap() {
		new PopulateMapTask().execute();
	}

	private class PopulateMapTask extends AsyncTask<Void, Void, Void>
	{
		private List<Station> stations;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Void doInBackground(Void... parameters) {
			stations = DbProvider.getInstance().getStations().getStationsList();

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			setSupportProgressBarIndeterminateVisibility(false);

			setUpStationsOnMap(stations);
		}
	}

	private void setUpStationsOnMap(List<Station> stations) {
		StationsOverlay stationsOverlay = buildStationsOverlay();

		for (Station station : stations) {
			OverlayItem stationOverlayItem = new StationOverlayItem(buildGeoPoint(station),
				station);
			stationsOverlay.addOverlay(stationOverlayItem);
		}

		getMapView().getOverlays().add(stationsOverlay);
	}

	private StationsOverlay buildStationsOverlay() {
		Drawable marker = getResources().getDrawable(R.drawable.ic_marker);
		MapView mapView = getMapView();

		StationsOverlay stationsOverlay = new StationsOverlay(marker, mapView);
		stationsOverlay.setOnBalloonTapListener(new StationTapListener(this));

		return stationsOverlay;
	}

	private GeoPoint buildGeoPoint(Station station) {
		int latitudeE6 = (int) (station.getLatitude() * MICRODEGREES_IN_DEGREE);
		int longitudeE6 = (int) (station.getLongitude() * MICRODEGREES_IN_DEGREE);

		return new GeoPoint(latitudeE6, longitudeE6);
	}

	private static class StationTapListener implements StationsOverlay.OnBalloonTapListener
	{
		private final Activity activity;

		public StationTapListener(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onBalloonTap(StationOverlayItem stationOverlayItem) {
			callRoutesActivity(stationOverlayItem.getStation());
		}

		private void callRoutesActivity(Station station) {
			Intent callIntent = IntentFactory.createRoutesIntent(activity, station);
			activity.startActivity(callIntent);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		myLocationOverlay.disableMyLocation();
	}
}
