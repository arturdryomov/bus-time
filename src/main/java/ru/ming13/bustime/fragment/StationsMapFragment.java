package ru.ming13.bustime.fragment;

import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StationSelectedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Loaders;
import ru.ming13.bustime.util.MapsUtil;

public class StationsMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor>,
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

		public static final int FAR_AWAY_DISTANCE_IN_METERS = 50000;

		public static final int ZOOM = 15;
	}

	private LocationClient locationClient;
	private Map<String, Long> stationMarkerIds;

	public static StationsMapFragment newInstance() {
		return new StationsMapFragment();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpMap();
		setUpStations();
		setUpLocationClient();
	}

	private void setUpMap() {
		setUpUi();
		setUpStationMarkersListener();
	}

	private void setUpUi() {
		GoogleMap map = getMap();

		map.setMyLocationEnabled(Ui.CURRENT_LOCATION_ENABLED);
		map.getUiSettings().setMyLocationButtonEnabled(Ui.CURRENT_LOCATION_ENABLED);

		map.getUiSettings().setZoomControlsEnabled(Ui.ZOOM_ENABLED);

		map.setPadding(0, getTopPadding(), 0, 0);
	}

	private int getTopPadding() {
		return getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);
	}

	private void setUpStationMarkersListener() {
		getMap().setOnInfoWindowClickListener(this);
	}

	@Override
	public void onInfoWindowClick(Marker stationMarker) {
		long stationId = stationMarkerIds.get(stationMarker.getId());
		String stationName = stationMarker.getTitle();
		String stationDirection = stationMarker.getSnippet();

		sendStationSelectedEvent(stationId, stationName, stationDirection);
	}

	private void sendStationSelectedEvent(long stationId, String stationName, String stationDirection) {
		BusProvider.getBus().post(new StationSelectedEvent(stationId, stationName, stationDirection));
	}

	private void setUpStations() {
		setUpStationsIds();
		setUpStationsContent();
	}

	private void setUpStationsIds() {
		stationMarkerIds = new HashMap<String, Long>();
	}

	private void setUpStationsContent() {
		getLoaderManager().initLoader(Loaders.STATIONS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getStationsUri(), null, null, null, null);
	}

	private Uri getStationsUri() {
		return BusTimeContract.Stations.buildStationsUri();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> stationsLoader, Cursor stationsCursor) {
		setUpStationsCursor(stationsCursor);
		setUpStationsMarkers(stationsCursor);
	}

	private void setUpStationsCursor(Cursor stationsCursor) {
		if (stationsCursor.isBeforeFirst()) {
			return;
		}

		stationsCursor.moveToFirst();
		stationsCursor.moveToPrevious();
	}

	private void setUpStationsMarkers(Cursor stationsCursor) {
		GoogleMap map = getMap();

		stationMarkerIds.clear();

		while (stationsCursor.moveToNext()) {
			Marker stationMarker = map.addMarker(buildStationMarkerOptions(stationsCursor));
			stationMarkerIds.put(stationMarker.getId(), getStationId(stationsCursor));
		}
	}

	private MarkerOptions buildStationMarkerOptions(Cursor stationsCursor) {
		String stationName = getStationName(stationsCursor);
		String stationDirection = getStationDirection(stationsCursor);
		double stationLatitude = getStationLatitude(stationsCursor);
		double stationLongitude = getStationLongitude(stationsCursor);

		return new MarkerOptions()
			.title(stationName)
			.snippet(stationDirection)
			.position(new LatLng(stationLatitude, stationLongitude))
			.icon(BitmapDescriptorFactory.defaultMarker(getStationMarkerHue()));
	}

	private String getStationName(Cursor stationsCursor) {
		return stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.NAME));
	}

	private String getStationDirection(Cursor stationsCursor) {
		return stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.DIRECTION));
	}

	private double getStationLatitude(Cursor stationsCursor) {
		return stationsCursor.getDouble(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.LATITUDE));
	}

	private double getStationLongitude(Cursor stationsCursor) {
		return stationsCursor.getDouble(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.LONGITUDE));
	}

	private float getStationMarkerHue() {
		return getResources().getInteger(R.integer.hue_map_marker_station);
	}

	private long getStationId(Cursor stationsCursor) {
		return stationsCursor.getLong(
			stationsCursor.getColumnIndex(BusTimeContract.Stations._ID));
	}

	@Override
	public void onLoaderReset(Loader<Cursor> stationsLoader) {
	}

	private void setUpLocationClient() {
		locationClient = new LocationClient(getActivity(), this, this);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		setUpCurrentLocation();
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

		return location.distanceTo(defaultLocation) >= Defaults.FAR_AWAY_DISTANCE_IN_METERS;
	}

	private Location convertLocation(LatLng latLng) {
		Location location = new Location(StringUtils.EMPTY);

		location.setLatitude(latLng.latitude);
		location.setLongitude(latLng.longitude);

		return location;
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		MapsUtil mapsUtil = MapsUtil.with(getActivity());

		if (mapsUtil.isResolvable(connectionResult)) {
			mapsUtil.resolveError(connectionResult);
		} else {
			mapsUtil.showErrorDialog(getFragmentManager(), connectionResult);
		}
	}

	@Override
	public void onStart() {
		super.onStart();

		locationClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();

		locationClient.disconnect();
	}
}
