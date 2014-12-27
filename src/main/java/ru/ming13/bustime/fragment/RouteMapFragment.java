package ru.ming13.bustime.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.otto.Subscribe;
import com.venmo.cursor.CursorList;

import java.util.ArrayList;
import java.util.List;

import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RoutePathLoadedEvent;
import ru.ming13.bustime.cursor.StopsCursor;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.task.RoutePathLoadingTask;
import ru.ming13.bustime.util.Bartender;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Loaders;

public class RouteMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<Cursor>, OnMapReadyCallback
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

		public static final int ZOOM = 11;
	}

	public static RouteMapFragment newInstance(@NonNull Route route) {
		RouteMapFragment fragment = new RouteMapFragment();

		fragment.setArguments(buildArguments(route));

		return fragment;
	}

	private static Bundle buildArguments(Route route) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.ROUTE, route);

		return arguments;
	}

	@InjectExtra(Fragments.Arguments.ROUTE)
	Route route;

	private GoogleMap map;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpInjections();

		setUpMap();
	}

	private void setUpInjections() {
		Dart.inject(this, getArguments());
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

	private void setUpCamera() {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(getDefaultLocation(), Defaults.ZOOM));
	}

	private LatLng getDefaultLocation() {
		return new LatLng(Defaults.LOCATION_LATITUDE, Defaults.LOCATION_LONGITUDE);
	}

	private void setUpStops() {
		setUpStopsContent();
	}

	private void setUpStopsContent() {
		getLoaderManager().initLoader(Loaders.ROUTE_STOPS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getStopsUri(), null, null, null, null);
	}

	private Uri getStopsUri() {
		return BusTimeContract.Stops.getStopsUri(route.getId());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> stopsLoader, Cursor stopsCursor) {
		List<Stop> stops = new CursorList<>(new StopsCursor(stopsCursor));

		setUpStopsMarkers(stops);
		setUpRoutePath(stops);
		setUpRouteArea(stops);
	}

	private void setUpStopsMarkers(List<Stop> stops) {
		for (Stop stop : stops) {
			map.addMarker(buildStopMarkerOptions(stop));
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

	private void setUpRoutePath(List<Stop> stops) {
		RoutePathLoadingTask.execute(getStopPositions(stops));
	}

	private List<LatLng> getStopPositions(List<Stop> stops) {
		List<LatLng> stopPositions = new ArrayList<>();

		for (Stop stop : stops) {
			stopPositions.add(new LatLng(stop.getLatitude(), stop.getLongitude()));
		}

		return stopPositions;
	}

	@Subscribe
	public void onRoutePathLoaded(RoutePathLoadedEvent event) {
		setUpRoutePathLine(event.getPathPositions());
	}

	private void setUpRoutePathLine(List<LatLng> routePathPositions) {
		map.addPolyline(buildRoutePathOptions(routePathPositions));
	}

	private PolylineOptions buildRoutePathOptions(List<LatLng> routePathPositions) {
		return new PolylineOptions()
			.addAll(routePathPositions)
			.width(getRoutePathWidth())
			.color(getRoutePathColor());
	}

	private int getRoutePathWidth() {
		return getResources().getDimensionPixelSize(R.dimen.width_route_path);
	}

	private int getRoutePathColor() {
		return getResources().getColor(R.color.background_primary);
	}

	private void setUpRouteArea(List<Stop> stops) {
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(
			buildRouteAreaBounds(stops),
			getMapWidth(),
			getMapHeight(),
			getRouteAreaPadding()));
	}

	private LatLngBounds buildRouteAreaBounds(List<Stop> stops) {
		LatLngBounds.Builder routeAreaBounds = new LatLngBounds.Builder();

		for (LatLng stopPosition : getStopPositions(stops)) {
			routeAreaBounds.include(stopPosition);
		}

		return routeAreaBounds.build();
	}

	private int getMapWidth() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	private int getMapHeight() {
		return getResources().getDisplayMetrics().heightPixels;
	}

	private int getRouteAreaPadding() {
		return getResources().getDimensionPixelSize(R.dimen.padding_route_path);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> stopsLoader) {
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);
	}
}
