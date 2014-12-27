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

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.RouteStopsAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.StopSelectedEvent;
import ru.ming13.bustime.cursor.RouteStopsCursor;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Loaders;

public class RouteStopsFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static RouteStopsFragment newInstance(@NonNull Route route) {
		RouteStopsFragment fragment = new RouteStopsFragment();

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

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpInjections();

		setUpStops();
	}

	private void setUpInjections() {
		Dart.inject(this, getArguments());
	}

	private void setUpStops() {
		setUpStopsList();
		setUpStopsAdapter();
		setUpStopsContent();
	}

	private void setUpStopsList() {
		if (Frames.at(getActivity()).areAvailable()) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
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
		return BusTimeContract.Stops.getStopsUri(route.getId());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> stopsLoader, Cursor stopsCursor) {
		getStopsAdapter().swapCursor(new RouteStopsCursor(stopsCursor));
	}

	private RouteStopsAdapter getStopsAdapter() {
		return (RouteStopsAdapter) getListAdapter();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> stopsLoader) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		BusProvider.getBus().post(new StopSelectedEvent(getStopsAdapter().getItem(position).getStop()));
	}
}
