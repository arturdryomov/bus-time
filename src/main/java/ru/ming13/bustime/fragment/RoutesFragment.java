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
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.adapter.RoutesAdapter;
import ru.ming13.bustime.cursor.RoutesCursor;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Loaders;

public final class RoutesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static RoutesFragment newInstance() {
		return new RoutesFragment();
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
		setListAdapter(new RoutesAdapter(getActivity()));
	}

	private void setUpRoutesContent() {
		getLoaderManager().initLoader(Loaders.ROUTES, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getRoutesUri(), null, null, null, null);
	}

	private Uri getRoutesUri() {
		return BusTimeContract.Routes.getRoutesUri();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> routesLoader, Cursor routesCursor) {
		getRoutesAdapter().swapCursor(new RoutesCursor(routesCursor));
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

		BusProvider.getBus().post(new RouteSelectedEvent(getRoutesAdapter().getItem(position)));
	}
}
