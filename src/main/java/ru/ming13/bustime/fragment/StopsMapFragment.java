package ru.ming13.bustime.fragment;

import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.venmo.cursor.CursorList;

import java.util.List;
import java.util.Map;

import icepick.Icepick;
import icepick.State;
import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.cursor.StopsCursor;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Loaders;
import ru.ming13.bustime.util.Maps;
import ru.ming13.bustime.util.Strings;

public final class StopsMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor>,
	OnMapReadyCallback,
	GoogleMap.OnInfoWindowClickListener,
	GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener
{
	private static final class Ui
	{
		private Ui() {
		}

		public static final boolean CURRENT_LOCATION_ENABLED = true;
		public static final boolean NAVIGATION_ENABLED = false;
		public static final boolean ZOOM_ENABLED = true;
	}

	private static final class Defaults
	{
		private Defaults() {
		}

		public static final double LOCATION_LATITUDE = 55.533391;
		public static final double LOCATION_LONGITUDE = 28.650013;

		public static final int FAR_AWAY_DISTANCE_IN_METERS = 20000;

		public static final int ZOOM = 15;
	}

	private GoogleMap map;

	private GoogleApiClient locationClient;

	private Map<String, Long> stopIds;

	@State
	CameraPosition cameraPosition;

	@NonNull
	public static StopsMapFragment newInstance() {
		return new StopsMapFragment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpState(savedInstanceState);

		setUpMap();
	}

	private void setUpState(Bundle state) {
		Icepick.restoreInstanceState(this, state);
	}

	private void setUpMap() {
		getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap map) {
		setUpMap(map);

		setUpStops();
	}

	private void setUpMap(GoogleMap map) {
		this.map = map;

		setUpUi();
		setUpListeners();
		setUpCamera();
	}

	private void setUpUi() {
		map.setMyLocationEnabled(Ui.CURRENT_LOCATION_ENABLED);
		map.getUiSettings().setMyLocationButtonEnabled(Ui.CURRENT_LOCATION_ENABLED);

		map.getUiSettings().setMapToolbarEnabled(Ui.NAVIGATION_ENABLED);

		map.getUiSettings().setZoomControlsEnabled(Ui.ZOOM_ENABLED);

		Bartender bartender = Bartender.at(getActivity());
		map.setPadding(
			bartender.getLeftUiPadding(),
			bartender.getTopUiPadding(),
			bartender.getRightUiPadding(),
			bartender.getBottomUiPadding());
	}

	private void setUpListeners() {
		map.setOnInfoWindowClickListener(this);
	}

	@Override
	public void onInfoWindowClick(Marker stopMarker) {
		if (isStopIdAvailable(stopMarker)) {
			BusProvider.getBus().post(new StopSelectedEvent(getStop(stopMarker)));
		}
	}

	private boolean isStopIdAvailable(Marker stopMarker) {
		return (stopIds != null) && (stopIds.containsKey(stopMarker.getId()));
	}

	private Stop getStop(Marker stopMarker) {
		long stopId = stopIds.get(stopMarker.getId());
		String stopName = stopMarker.getTitle();
		String stopDirection = stopMarker.getSnippet();
		double stopLatitude = stopMarker.getPosition().latitude;
		double stopLongitude = stopMarker.getPosition().longitude;

		return new Stop(stopId, stopName, stopDirection, stopLatitude, stopLongitude);
	}

	private void setUpCamera() {
		if (cameraPosition != null) {
			setUpSavedLocation();
		} else {
			// Avoid default location flickering at a center of the planet

			setUpDefaultLocation();

			setUpLocationClient();
		}
	}

	private void setUpSavedLocation() {
		map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private void setUpDefaultLocation() {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(getDefaultLocation(), Defaults.ZOOM));
	}

	private LatLng getDefaultLocation() {
		return new LatLng(Defaults.LOCATION_LATITUDE, Defaults.LOCATION_LONGITUDE);
	}

	private void setUpLocationClient() {
		locationClient = new GoogleApiClient.Builder(getActivity())
			.addApi(LocationServices.API)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();

		locationClient.connect();
	}

	@Override
	public void onConnectionSuspended(int suspensionCause) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		setUpCurrentLocation();

		tearDownLocationClient();
	}

	private void setUpCurrentLocation() {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLocation(), Defaults.ZOOM));
	}

	private LatLng getCurrentLocation() {
		Location location = LocationServices.FusedLocationApi.getLastLocation(locationClient);

		if (!isLocationAvailable(location)) {
			return getDefaultLocation();
		}

		if (isLocationFarAway(location)) {
			return getDefaultLocation();
		}

		return new LatLng(location.getLatitude(), location.getLongitude());
	}

	private boolean isLocationAvailable(Location location) {
		return location != null;
	}

	private boolean isLocationFarAway(Location location) {
		Location defaultLocation = convertLocation(getDefaultLocation());

		return location.distanceTo(defaultLocation) > Defaults.FAR_AWAY_DISTANCE_IN_METERS;
	}

	private Location convertLocation(LatLng latLng) {
		Location location = new Location(Strings.EMPTY);

		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);

		return location;
	}

	private void tearDownLocationClient() {
		locationClient.disconnect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Maps maps = Maps.at(getActivity());

		if (maps.isResolvable(connectionResult)) {
			maps.resolve(connectionResult);
		} else {
			maps.showErrorDialog(connectionResult);
		}
	}

	private void setUpStops() {
		setUpStopsContent();
	}

	private void setUpStopsContent() {
		this.stopIds = new ArrayMap<>();

		getLoaderManager().initLoader(Loaders.STOPS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getStopsUri(), null, null, null, null);
	}

	private Uri getStopsUri() {
		return BusTimeContract.Stops.getStopsUri();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> stopsLoader, Cursor stopsCursor) {
		List<Stop> stops = new CursorList<>(new StopsCursor(stopsCursor));

		setUpStopsMarkers(stops);
	}

	private void setUpStopsMarkers(List<Stop> stops) {
		stopIds.clear();

		for (Stop stop : stops) {
			Marker stopMarker = map.addMarker(buildStopMarkerOptions(stop));

			stopIds.put(stopMarker.getId(), stop.getId());
		}
	}

	private MarkerOptions buildStopMarkerOptions(Stop stop) {
		return new MarkerOptions()
			.title(stop.getName())
			.snippet(stop.getDirection())
			.position(new LatLng(stop.getLatitude(), stop.getLongitude()))
			.icon(BitmapDescriptorFactory.defaultMarker(getStopMarkerHue()));
	}

	private float getStopMarkerHue() {
		return getResources().getInteger(R.integer.hue_primary);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> stopsLoader) {
	}

	@Override
	public void onPause() {
		super.onPause();

		this.cameraPosition = map.getCameraPosition();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		tearDownState(outState);
	}

	private void tearDownState(Bundle state) {
		Icepick.saveInstanceState(this, state);
	}
}
