package app.android.bustime.ui;


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
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;


public class StationsMapActivity extends SherlockMapActivity
{
	private static final double MICRODEGREES_IN_DEGREE = 1E6;

	private static final int DEFAULT_MAP_ZOOM = 15;
	private static final boolean ZOOM_CONTROLS_ENABLED = true;

	private static final double DEFAULT_MAP_POSITION_LATITUDE = 55.533185;
	private static final double DEFAULT_MAP_POSITION_LONGITUDE = 28.655477;

	private static final String STATION_NAME_REMARK_BEGIN_SIGN = "(";
	private static final String STATION_NAME_REMARK_END_SIGN = ")";

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

		if (!isLastLocationKnown()) {
			animateToDefaultLocation();
		}

		getMapView().getOverlays().add(myLocationOverlay);
	}

	private boolean isLastLocationKnown() {
		return myLocationOverlay.getLastFix() != null || myLocationOverlay.getMyLocation() != null;
	}

	private void animateToDefaultLocation() {
		GeoPoint defaultPoint = buildGeoPoint(DEFAULT_MAP_POSITION_LATITUDE,
			DEFAULT_MAP_POSITION_LONGITUDE);
		getMapView().getController().animateTo(defaultPoint);
	}

	private GeoPoint buildGeoPoint(double latitude, double longitude) {
		int latitudeE6 = (int) (latitude * MICRODEGREES_IN_DEGREE);
		int longitudeE6 = (int) (longitude * MICRODEGREES_IN_DEGREE);

		return new GeoPoint(latitudeE6, longitudeE6);
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
		private StationsOverlay stationsOverlay;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			showProgressInActionBar();

			stationsOverlay = buildStationsOverlay();
		}

		@Override
		protected Void doInBackground(Void... parameters) {
			for (Station station : DbProvider.getInstance().getStations().getStationsList()) {
				OverlayItem stationOverlayItem = buildStationOverlayItem(station);
				stationsOverlay.addOverlayItem(stationOverlayItem);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			addOverlayToMap(stationsOverlay);

			hideProgressInActionBar();
		}
	}

	private void showProgressInActionBar() {
		setSupportProgressBarIndeterminateVisibility(true);
	}

	private StationsOverlay buildStationsOverlay() {
		Drawable marker = getResources().getDrawable(R.drawable.ic_marker);
		MapView mapView = getMapView();

		StationsOverlay stationsOverlay = new StationsOverlay(marker, mapView);
		stationsOverlay.setOnBalloonTapListener(new StationTapListener(this));

		return stationsOverlay;
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

	private StationOverlayItem buildStationOverlayItem(Station station) {
		if (hasStationNameRemark(station.getName())) {
			return buildStationOverlayItemWithRemark(station);
		}
		else {
			return buildStationOverlayItemWithoutRemark(station);
		}
	}

	private boolean hasStationNameRemark(String stationName) {
		if (!stationName.contains(STATION_NAME_REMARK_BEGIN_SIGN)) {
			return false;
		}

		return stationName.contains(STATION_NAME_REMARK_END_SIGN);
	}

	private StationOverlayItem buildStationOverlayItemWithRemark(Station station) {
		GeoPoint stationGeoPoint = buildGeoPoint(station);
		String pureStationName = extractPureStationName(station.getName());
		String stationNameRemark = extractStationNameRemark(station.getName());

		return new StationOverlayItem(station, stationGeoPoint, pureStationName, stationNameRemark);
	}

	private GeoPoint buildGeoPoint(Station station) {
		return buildGeoPoint(station.getLatitude(), station.getLongitude());
	}

	private String extractPureStationName(String stationName) {
		int stationRemarkBeginPosition = stationName.indexOf(STATION_NAME_REMARK_BEGIN_SIGN);

		return stationName.substring(0, stationRemarkBeginPosition - 1);
	}

	private String extractStationNameRemark(String stationName) {
		int stationRemarkBeginPosition = stationName.indexOf(STATION_NAME_REMARK_BEGIN_SIGN);
		int stationRemarkEndPosition = stationName.indexOf(STATION_NAME_REMARK_END_SIGN);

		return stationName.substring(stationRemarkBeginPosition + 1, stationRemarkEndPosition);
	}

	private StationOverlayItem buildStationOverlayItemWithoutRemark(Station station) {
		GeoPoint stationGeoPoint = buildGeoPoint(station);

		return new StationOverlayItem(station, stationGeoPoint);
	}

	private void addOverlayToMap(ItemizedOverlay overlay) {
		getMapView().getOverlays().add(overlay);
		getMapView().invalidate();
	}

	private void hideProgressInActionBar() {
		setSupportProgressBarIndeterminateVisibility(false);
	}

	@Override
	protected void onPause() {
		super.onPause();

		myLocationOverlay.disableMyLocation();
	}
}
