package ru.ming13.bustime.fragment;

import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.ArrayMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Loaders;
import ru.ming13.bustime.util.MapsUtil;

public class StopsMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor>,
	GoogleMap.OnInfoWindowClickListener,
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener
{
	private static final class Ui
	{
		private Ui() {
		}

		public static final boolean CURRENT_LOCATION_ENABLED = true;
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

	private LocationClient locationClient;
	private Map<String, Long> stopIds;

	public static StopsMapFragment newInstance() {
		return new StopsMapFragment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpMap();
		setUpStops();

		setUpCameraPosition(savedInstanceState);
	}

	private void setUpMap() {
		setUpUi();
		setUpStopMarkersListener();
	}

	private void setUpUi() {
		GoogleMap map = getMap();

		map.setMyLocationEnabled(Ui.CURRENT_LOCATION_ENABLED);
		map.getUiSettings().setMyLocationButtonEnabled(Ui.CURRENT_LOCATION_ENABLED);

		map.getUiSettings().setZoomControlsEnabled(Ui.ZOOM_ENABLED);

		Bartender bartender = Bartender.at(getActivity());
		map.setPadding(
			bartender.getLeftUiPadding(),
			bartender.getTopUiPadding(),
			bartender.getRightUiPadding(),
			bartender.getBottomUiPadding());
	}

	private void setUpStopMarkersListener() {
		getMap().setOnInfoWindowClickListener(this);
	}

	@Override
	public void onInfoWindowClick(Marker stopMarker) {
		if (!isStopIdAvailable(stopMarker)) {
			return;
		}

		long stopId = stopIds.get(stopMarker.getId());
		String stopName = stopMarker.getTitle();
		String stopDirection = stopMarker.getSnippet();
		double stopLatitude = stopMarker.getPosition().latitude;
		double stopLongitude = stopMarker.getPosition().longitude;

		Stop stop = new Stop(stopId, stopName, stopDirection, stopLatitude, stopLongitude);

		BusProvider.getBus().post(new StopSelectedEvent(stop));
	}

	private boolean isStopIdAvailable(Marker stopMarker) {
		return (stopIds != null) && (stopIds.containsKey(stopMarker.getId()));
	}

	private void setUpStops() {
		setUpStopsIds();
		setUpStopsContent();
	}

	private void setUpStopsIds() {
		stopIds = new ArrayMap<String, Long>();
	}

	private void setUpStopsContent() {
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
		setUpStopsCursor(stopsCursor);
		setUpStopsMarkers(stopsCursor);
	}

	private void setUpStopsCursor(Cursor stopsCursor) {
		if (stopsCursor.isBeforeFirst()) {
			return;
		}

		stopsCursor.moveToFirst();
		stopsCursor.moveToPrevious();
	}

	private void setUpStopsMarkers(Cursor stopsCursor) {
		GoogleMap map = getMap();

		stopIds.clear();

		while (stopsCursor.moveToNext()) {
			Marker stopMarker = map.addMarker(buildStopMarkerOptions(stopsCursor));
			stopIds.put(stopMarker.getId(), getStopId(stopsCursor));
		}
	}

	private MarkerOptions buildStopMarkerOptions(Cursor stopsCursor) {
		String stopName = getStopName(stopsCursor);
		String stopDirection = getStopDirection(stopsCursor);
		double stopLatitude = getStopLatitude(stopsCursor);
		double stopLongitude = getStopLongitude(stopsCursor);

		return new MarkerOptions()
			.title(stopName)
			.snippet(stopDirection)
			.position(new LatLng(stopLatitude, stopLongitude))
			.icon(BitmapDescriptorFactory.defaultMarker(getStopMarkerHue()));
	}

	private String getStopName(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.NAME));
	}

	private String getStopDirection(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.DIRECTION));
	}

	private double getStopLatitude(Cursor stopsCursor) {
		return stopsCursor.getDouble(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.LATITUDE));
	}

	private double getStopLongitude(Cursor stopsCursor) {
		return stopsCursor.getDouble(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.LONGITUDE));
	}

	private float getStopMarkerHue() {
		return getResources().getInteger(R.integer.hue_marker_stop);
	}

	private long getStopId(Cursor stopsCursor) {
		return stopsCursor.getLong(
			stopsCursor.getColumnIndex(BusTimeContract.Stops._ID));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> stopsLoader) {
	}

	private void setUpCameraPosition(Bundle state) {
		if (isCameraPositionSaved(state)) {
			setUpSavedCameraPosition(state);
		} else {
			setUpLocationClient();
		}
	}

	private boolean isCameraPositionSaved(Bundle state) {
		return (state != null) && (loadCameraPosition(state) != null);
	}

	private CameraPosition loadCameraPosition(Bundle state) {
		return state.getParcelable(Fragments.States.CAMERA_POSITION);
	}

	private void setUpSavedCameraPosition(Bundle state) {
		CameraPosition cameraPosition = loadCameraPosition(state);
		getMap().moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private void setUpLocationClient() {
		locationClient = new LocationClient(getActivity(), this, this);
		locationClient.connect();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		setUpCurrentLocation();
		tearDownLocationClient();
	}

	private void setUpCurrentLocation() {
		getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(getCurrentLocation(), Defaults.ZOOM));
	}

	private LatLng getCurrentLocation() {
		Location location = locationClient.getLastLocation();

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

	private LatLng getDefaultLocation() {
		return new LatLng(Defaults.LOCATION_LATITUDE, Defaults.LOCATION_LONGITUDE);
	}

	private boolean isLocationFarAway(Location location) {
		Location defaultLocation = convertLocation(getDefaultLocation());

		return location.distanceTo(defaultLocation) > Defaults.FAR_AWAY_DISTANCE_IN_METERS;
	}

	private Location convertLocation(LatLng latLng) {
		Location location = new Location(StringUtils.EMPTY);

		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);

		return location;
	}

	private void tearDownLocationClient() {
		locationClient.disconnect();
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		MapsUtil mapsUtil = MapsUtil.with(getActivity());

		if (mapsUtil.isResolvable(connectionResult)) {
			mapsUtil.resolve(connectionResult);
		} else {
			mapsUtil.showErrorDialog(connectionResult);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		saveCameraPosition(outState);
	}

	private void saveCameraPosition(Bundle state) {
		state.putParcelable(Fragments.States.CAMERA_POSITION, getMap().getCameraPosition());
	}
}
