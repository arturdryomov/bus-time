package ru.ming13.bustime.ui.activity;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import org.apache.commons.lang3.StringUtils;
import ru.ming13.bustime.R;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Station;
import ru.ming13.bustime.ui.intent.IntentFactory;
import ru.ming13.bustime.ui.map.StationOverlayItem;
import ru.ming13.bustime.ui.map.StationsOverlay;


public class StationsMapActivity extends SherlockMapActivity
{
	private static final double MICRODEGREES_IN_DEGREE = 1E6;

	private static final int DEFAULT_MAP_ZOOM = 15;
	private static final boolean ZOOM_CONTROLS_ENABLED = true;

	private static final double DEFAULT_MAP_POSITION_LATITUDE = 55.533185;
	private static final double DEFAULT_MAP_POSITION_LONGITUDE = 28.655477;

	private static final int FAR_FROM_DISTANCE_IN_METERS = 50000;

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
		setUpDefaultLocation();

		populateMap();
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

		setUpAnimationToCurrentLocation();

		getMapView().getOverlays().add(myLocationOverlay);
	}

	private void setUpAnimationToCurrentLocation() {
		myLocationOverlay.runOnFirstFix(new Runnable()
		{
			@Override
			public void run() {
				animateToCurrentLocation();
			}
		});
	}

	private void animateToCurrentLocation() {
		GeoPoint currentLocation = myLocationOverlay.getMyLocation();

		if (currentLocation == null) {
			return;
		}

		if (isLocationFarFromDefaultLocation(currentLocation)) {
			return;
		}

		getMapView().getController().animateTo(currentLocation);
	}

	private boolean isLocationFarFromDefaultLocation(GeoPoint locationPoint) {
		Location location = buildLocation(locationPoint);
		Location defaultLocation = buildLocation(getDefaultLocation());

		return location.distanceTo(defaultLocation) > FAR_FROM_DISTANCE_IN_METERS;
	}

	private Location buildLocation(GeoPoint geoPoint) {
		Location location = new Location(StringUtils.EMPTY);

		location.setLatitude(geoPoint.getLatitudeE6() / MICRODEGREES_IN_DEGREE);
		location.setLongitude(geoPoint.getLongitudeE6() / MICRODEGREES_IN_DEGREE);

		return location;
	}

	private GeoPoint getDefaultLocation() {
		return buildGeoPoint(DEFAULT_MAP_POSITION_LATITUDE, DEFAULT_MAP_POSITION_LONGITUDE);
	}

	private GeoPoint buildGeoPoint(double latitude, double longitude) {
		int latitudeE6 = (int) (latitude * MICRODEGREES_IN_DEGREE);
		int longitudeE6 = (int) (longitude * MICRODEGREES_IN_DEGREE);

		return new GeoPoint(latitudeE6, longitudeE6);
	}

	private void setUpDefaultLocation() {
		if (isLastLocationKnown() && !isLastKnownLocationFarFromDefaultLocation()) {
			animateToLastKnownLocation();
		}
		else {
			animateToDefaultLocation();
		}
	}

	private boolean isLastLocationKnown() {
		return getLastKnownLocation() != null;
	}

	private GeoPoint getLastKnownLocation() {
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Location gpsLastKnownLocation = locationManager.getLastKnownLocation(
			LocationManager.GPS_PROVIDER);
		if (gpsLastKnownLocation != null) {
			return buildGeoPoint(gpsLastKnownLocation.getLatitude(), gpsLastKnownLocation.getLongitude());
		}

		Location networkLastKnownLocation = locationManager.getLastKnownLocation(
			LocationManager.NETWORK_PROVIDER);
		if (networkLastKnownLocation != null) {
			return buildGeoPoint(networkLastKnownLocation.getLatitude(),
				networkLastKnownLocation.getLongitude());
		}

		return null;
	}

	private boolean isLastKnownLocationFarFromDefaultLocation() {
		GeoPoint lastKnownLocation = getLastKnownLocation();

		return isLocationFarFromDefaultLocation(lastKnownLocation);
	}

	private void animateToLastKnownLocation() {
		GeoPoint lastKnownLocation = getLastKnownLocation();

		getMapView().getController().animateTo(lastKnownLocation);
	}

	private void animateToDefaultLocation() {
		GeoPoint defaultLocation = getDefaultLocation();

		getMapView().getController().animateTo(defaultLocation);
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
			List<StationOverlayItem> stationOverlayItems = new ArrayList<StationOverlayItem>();

			for (Station station : DbProvider.getInstance().getStations().getStationsList()) {
				stationOverlayItems.add(buildStationOverlayItem(station));
			}

			stationsOverlay.populate(stationOverlayItems);

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
	protected void onResume() {
		super.onResume();

		myLocationOverlay.enableMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();

		myLocationOverlay.disableMyLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_action_bar_map, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case R.id.menu_current_location:
				setUpAnimationToCurrentLocation();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
}
