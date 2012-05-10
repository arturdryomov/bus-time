package app.android.bustime.ui;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import app.android.bustime.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;


public class StationCoordinatesActivity extends MapActivity
{
	private final Context activityContext = this;

	private static final double DEFAULT_LATITUDE = 55.534229;
	private static final double DEFAULT_LONGITUDE = 28.661546;

	private static final int MICRODEGREES_IN_DEGREE = 1000000;

	private static final int DEFAULT_ZOOM = 17;
	private static final boolean DEFAULT_IS_ZOOM_CONTROL_ENABLED = true;

	private MapView map;
	private PinOverlay pin;

	private LocationManager locationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_coordinates);

		initializeMap();
		initializePin();
		initializeLocationManager();
	}

	private void initializeMap() {
		map = (MapView) findViewById(R.id.map);

		map.getController().setCenter(getGeoPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE));

		map.getController().setZoom(DEFAULT_ZOOM);
		map.setBuiltInZoomControls(DEFAULT_IS_ZOOM_CONTROL_ENABLED);
	}

	private GeoPoint getGeoPoint(double latitude, double longitude) {
		return (new GeoPoint((int) (latitude * MICRODEGREES_IN_DEGREE),
			(int) (longitude * MICRODEGREES_IN_DEGREE)));
	}

	private void initializePin() {
		Drawable pinImage = getResources().getDrawable(R.drawable.pin);
		pinImage.setBounds(0, 0, pinImage.getIntrinsicWidth(), pinImage.getIntrinsicHeight());

		ImageView pinView = (ImageView) findViewById(R.id.pin);

		pin = new PinOverlay(pinImage, pinView, map);

		map.getOverlays().add(pin);
	}

	private void initializeLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
			goToLocation(location);

			locationManager.removeUpdates(locationListener);
		}
	};

	private void goToLocation(Location location) {
		int latitude = (int) location.getLatitude() * MICRODEGREES_IN_DEGREE;
		int longitude = (int) location.getLongitude() * MICRODEGREES_IN_DEGREE;

		map.getController().animateTo(new GeoPoint(latitude, longitude));
		pin.setPosition(new GeoPoint(latitude, longitude));
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();

		locationManager.removeUpdates(locationListener);
	}
}
