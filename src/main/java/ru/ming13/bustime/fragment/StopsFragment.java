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
import ru.ming13.bustime.adapter.StopsAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Loaders;

public class StopsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static StopsFragment newInstance() {
		return new StopsFragment();
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
		setListAdapter(new StopsAdapter(getActivity()));
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
		getStopsAdapter().swapCursor(stopsCursor);
	}

	private StopsAdapter getStopsAdapter() {
		return (StopsAdapter) getListAdapter();
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
		double stopLatitude = stopsCursor.getDouble(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.LATITUDE));
		double stopLongitude = stopsCursor.getDouble(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.LONGITUDE));

		return new Stop(stopId, stopName, stopDirection, stopLatitude, stopLongitude);
	}

	private Cursor getStopsCursor(int stopPosition) {
		return (Cursor) getStopsAdapter().getItem(stopPosition);
	}
}
