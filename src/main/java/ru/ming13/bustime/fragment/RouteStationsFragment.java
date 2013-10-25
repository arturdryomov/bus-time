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
import ru.ming13.bustime.adapter.StationsAdapter;
import ru.ming13.bustime.bus.BusEvent;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StationSelectedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Loaders;

public class RouteStationsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static RouteStationsFragment newInstance(Uri uri) {
		RouteStationsFragment fragment = new RouteStationsFragment();

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

		setUpStations();
	}

	private void setUpStations() {
		setUpStationsAdapter();
		setUpStationsContent();
	}

	private void setUpStationsAdapter() {
		setListAdapter(buildStationsAdapter());
	}

	private ListAdapter buildStationsAdapter() {
		return new StationsAdapter(getActivity());
	}

	private void setUpStationsContent() {
		getLoaderManager().initLoader(Loaders.ROUTE_STATIONS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		Uri uri = getStationsUri();

		String[] projection = {
			BusTimeContract.Stations._ID,
			BusTimeContract.Stations.NAME,
			BusTimeContract.Stations.DIRECTION
		};

		return new CursorLoader(getActivity(), uri, projection, null, null, null);
	}

	private Uri getStationsUri() {
		return getArguments().getParcelable(Fragments.Arguments.URI);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> stationsLoader, Cursor stationsCursor) {
		getStationsAdapter().swapCursor(stationsCursor);
	}

	private StationsAdapter getStationsAdapter() {
		return (StationsAdapter) getListAdapter();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> stationsLoader) {
		getStationsAdapter().swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		sendStationSelectedEvent(id, position);
	}

	private void sendStationSelectedEvent(long stationId, int stationPosition) {
		BusProvider.getBus().post(buildStationSelectedEvent(stationId, stationPosition));
	}

	private BusEvent buildStationSelectedEvent(long stationId, int stationPosition) {
		String stationName = getStationName(stationPosition);
		String stationDirection = getStationDirection(stationPosition);

		return new StationSelectedEvent(stationId, stationName, stationDirection);
	}

	private String getStationName(int stationPosition) {
		Cursor stationsCursor = getStationsCursor(stationPosition);

		return stationsCursor.getString(stationsCursor.getColumnIndex(BusTimeContract.Stations.NAME));
	}

	private Cursor getStationsCursor(int stationPosition) {
		return (Cursor) getStationsAdapter().getItem(stationPosition);
	}

	private String getStationDirection(int stationPosition) {
		Cursor stationsCursor = getStationsCursor(stationPosition);

		return stationsCursor.getString(stationsCursor.getColumnIndex(BusTimeContract.Stations.DIRECTION));
	}
}
