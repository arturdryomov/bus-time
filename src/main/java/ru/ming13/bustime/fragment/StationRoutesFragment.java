package ru.ming13.bustime.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.RoutesAdapter;
import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Loaders;

public class StationRoutesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static StationRoutesFragment newInstance(Uri uri) {
		StationRoutesFragment fragment = new StationRoutesFragment();

		fragment.setArguments(buildArguments(uri));

		return fragment;
	}

	private static Bundle buildArguments(Uri uri) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.URI, uri);

		return arguments;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpRoutes();
	}

	private void setUpRoutes() {
		setUpRoutesAdapter();
		setUpRoutesContent();
	}

	private void setUpRoutesAdapter() {
		setListAdapter(buildRoutesAdapter());
	}

	private ListAdapter buildRoutesAdapter() {
		return new RoutesAdapter(getActivity());
	}

	private void setUpRoutesContent() {
		getLoaderManager().initLoader(Loaders.STATION_ROUTES, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		Uri uri = getRoutesUri();

		String[] projection = {
			BusTimeContract.Routes._ID,
			BusTimeContract.Routes.NUMBER,
			BusTimeContract.Routes.DESCRIPTION
		};

		return new CursorLoader(getActivity(), uri, projection, null, null, null);
	}

	private Uri getRoutesUri() {
		return getArguments().getParcelable(Fragments.Arguments.URI);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> routesLoader, Cursor routesCursor) {
		getRoutesAdapter().swapCursor(routesCursor);
	}

	private RoutesAdapter getRoutesAdapter() {
		return (RoutesAdapter) getListAdapter();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> routesLoader) {
		getRoutesAdapter().swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		sendRouteSelectedEvent(id, position);
	}

	private void sendRouteSelectedEvent(long routeId, int routePosition) {
		BusProvider.getBus().post(buildRouteSelectedEvent(routeId, routePosition));
	}

	private BusEvent buildRouteSelectedEvent(long routeId, int routePosition) {
		Cursor routesCursor = getRoutesCursor(routePosition);

		String routeNumber = routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Routes.NUMBER));
		String routeDescription = routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Routes.DESCRIPTION));

		return new RouteSelectedEvent(routeId, routeNumber, routeDescription);
	}

	private Cursor getRoutesCursor(int routePosition) {
		return (Cursor) getRoutesAdapter().getItem(routePosition);
	}
}
