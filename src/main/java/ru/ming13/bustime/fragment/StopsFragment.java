package ru.ming13.bustime.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.ming13.bustime.R;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.adapter.StopsAdapter;
import ru.ming13.bustime.cursor.StopsCursor;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Loaders;

public final class StopsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	@NonNull
	public static StopsFragment newInstance() {
		return new StopsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle state) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);

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
		getStopsAdapter().swapCursor(new StopsCursor(stopsCursor));
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

		BusProvider.getBus().post(new StopSelectedEvent(getStopsAdapter().getItem(position)));
	}
}
