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
import android.widget.ListView;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.RouteStopsAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Loaders;

public class RouteStopsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static RouteStopsFragment newInstance(Route route) {
		RouteStopsFragment fragment = new RouteStopsFragment();

		fragment.setArguments(buildArguments(route));

		return fragment;
	}

	private static Bundle buildArguments(Route route) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.ROUTE, route);

		return arguments;
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpStops();
	}

	private void setUpStops() {
		setUpStopsAdapter();
		setUpStopsContent();
	}

	private void setUpStopsAdapter() {
		setListAdapter(new RouteStopsAdapter(getActivity()));
	}

	private void setUpStopsContent() {
		getLoaderManager().initLoader(Loaders.ROUTE_STOPS, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getStopsUri(), null, null, null, null);
	}

	private Uri getStopsUri() {
		return BusTimeContract.Stops.getStopsUri(getRoute().getId());
	}

	private Route getRoute() {
		return getArguments().getParcelable(Fragments.Arguments.ROUTE);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> stopsLoader, Cursor stopsCursor) {
		getStopsAdapter().swapCursor(stopsCursor);
	}

	private RouteStopsAdapter getStopsAdapter() {
		return (RouteStopsAdapter) getListAdapter();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> stopsLoader) {
		getStopsAdapter().swapCursor(null);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		BusProvider.getBus().post(new StopSelectedEvent(getStop(id, position)));
	}

	private Stop getStop(long stopId, int stopPosition) {
		Cursor stopsCursor = getStopsCursor(stopPosition);

		String stopName = stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.NAME));
		String stopDirection = stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.DIRECTION));

		return new Stop(stopId, stopName, stopDirection);
	}

	private Cursor getStopsCursor(int stopPosition) {
		return (Cursor) getStopsAdapter().getItem(stopPosition);
	}
}