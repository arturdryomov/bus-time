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


public class StationCoordinatesActivity extends MapActivity
{
	private final Context activityContext = this;

	private static final double MICRODEGREES_IN_DEGREE = 1E6;

	private static final int DEFAULT_ZOOM = 17;
	private static final boolean DEFAULT_IS_ZOOM_CONTROL_ENABLED = true;

	private MapView map;
	private PinOverlay pin;

	private LocationManager locationManager;

	private double latitude;
	private double longitude;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_coordinates);

		processReceivedCoordinates();

		initializeActionbar();

		initializeMap();
		initializePin();
		initializeLocationManager();
	}

	private void initializeActionbar() {
		ImageButton saveCoordinatesButton = (ImageButton) findViewById(R.id.save_button);
		saveCoordinatesButton.setOnClickListener(saveCoordinatesListener);
	}

	private final OnClickListener saveCoordinatesListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			GeoPoint coordinates = pin.getPosition();
			latitude = coordinates.getLatitudeE6() / MICRODEGREES_IN_DEGREE;
			longitude = coordinates.getLongitudeE6() / MICRODEGREES_IN_DEGREE;

			finish();
		}
	};

	@Override
	public void finish() {
		Intent resultData = new Intent();
		resultData.putExtra(IntentFactory.MESSAGE_ID, latitude);
		resultData.putExtra(IntentFactory.EXTRA_MESSAGE_ID, longitude);
		setResult(RESULT_OK, resultData);

		super.finish();
	}

	private void processReceivedCoordinates() {
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

	private void initializeMap() {
		map = (MapView) findViewById(R.id.map);

		map.getController().setCenter(getGeoPoint(latitude, longitude));

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

		pin.setPosition(getGeoPoint(latitude, longitude));
	}

	private void initializeLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		UserAlerter.alert(activityContext, getString(R.string.loading_location));
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
		GeoPoint locationGeoPoint = getGeoPoint(location.getLatitude(), location.getLongitude());

		map.getController().animateTo(locationGeoPoint);
		pin.setPosition(locationGeoPoint);
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
