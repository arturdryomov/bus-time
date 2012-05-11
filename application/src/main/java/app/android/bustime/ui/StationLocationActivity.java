package app.android.bustime.ui;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import app.android.bustime.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;


public class StationLocationActivity extends MapActivity
{
	private final Context activityContext = this;

	private static final double MICRODEGREES_IN_DEGREE = 1E6;

	private static final int DEFAULT_ZOOM = 17;
	private static final boolean IS_ZOOM_CONTROL_ENABLED = true;

	private MapView map;
	private PinOverlay pin;

	private LocationManager locationManager;

	private double latitude;
	private double longitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_location);

		processReceivedLocation();

		initializeActionbar();

		initializeMap();
		initializePin();
		initializeLocationManager();
	}

	private void processReceivedLocation() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			if (receivedData.containsKey(IntentFactory.EXTRA_MESSAGE_ID)) {
				latitude = receivedData.getDouble(IntentFactory.MESSAGE_ID);
				longitude = receivedData.getDouble(IntentFactory.EXTRA_MESSAGE_ID);

				return;
			}
		}

		UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

		finish();
	}

	private void initializeActionbar() {
		ImageButton saveLocationButton = (ImageButton) findViewById(R.id.save_button);
		saveLocationButton.setOnClickListener(saveLocationListener);
	}

	private final OnClickListener saveLocationListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			savePinLocation();

			finish();
		}
	};

	private void savePinLocation() {
		GeoPoint location = pin.getPosition();
		latitude = location.getLatitudeE6() / MICRODEGREES_IN_DEGREE;
		longitude = location.getLongitudeE6() / MICRODEGREES_IN_DEGREE;
	}

	@Override
	public void finish() {
		Intent location = new Intent();
		location.putExtra(IntentFactory.MESSAGE_ID, latitude);
		location.putExtra(IntentFactory.EXTRA_MESSAGE_ID, longitude);
		setResult(RESULT_OK, location);

		super.finish();
	}

	private void initializeMap() {
		map = (MapView) findViewById(R.id.map);

		map.getController().setCenter(constructGeoPoint(latitude, longitude));

		map.getController().setZoom(DEFAULT_ZOOM);
		map.setBuiltInZoomControls(IS_ZOOM_CONTROL_ENABLED);
	}

	private GeoPoint constructGeoPoint(double latitude, double longitude) {
		int latitudeE6 = (int) (latitude * MICRODEGREES_IN_DEGREE);
		int longitudeE6 = (int) (longitude * MICRODEGREES_IN_DEGREE);

		return new GeoPoint(latitudeE6, longitudeE6);
	}

	private void initializePin() {
		Drawable pinImage = getResources().getDrawable(R.drawable.pin);
		pinImage.setBounds(0, 0, pinImage.getIntrinsicWidth(), pinImage.getIntrinsicHeight());

		ImageView pinView = (ImageView) findViewById(R.id.pin);

		pin = new PinOverlay(pinImage, pinView, map);

		map.getOverlays().add(pin);

		pin.setPosition(constructGeoPoint(latitude, longitude));
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

			stopLocationUpdates();
		}
	};

	private void stopLocationUpdates() {
		locationManager.removeUpdates(locationListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		stopLocationUpdates();
	}

	private void goToLocation(Location location) {
		GeoPoint locationGeoPoint = constructGeoPoint(location.getLatitude(), location.getLongitude());

		map.getController().animateTo(locationGeoPoint);
		pin.setPosition(locationGeoPoint);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
