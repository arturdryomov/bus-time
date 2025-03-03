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
import com.squareup.otto.Subscribe;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.StopRoutesAdapter;
import ru.ming13.bustime.animation.ListOrderAnimator;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.RouteSelectedEvent;
import ru.ming13.bustime.bus.TimeChangedEvent;
import ru.ming13.bustime.cursor.StopRoutesCursor;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Frames;
import ru.ming13.bustime.util.Loaders;
import ru.ming13.bustime.util.Timer;

public class StopRoutesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	public static StopRoutesFragment newInstance(@NonNull Stop stop) {
		StopRoutesFragment fragment = new StopRoutesFragment();

		fragment.setArguments(buildArguments(stop));

		return fragment;
	}

	private static Bundle buildArguments(Stop stop) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.STOP, stop);

		return arguments;
	}

	@InjectExtra(Fragments.Arguments.STOP)
	Stop stop;

	private Timer timer;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpInjections();

		setUpRoutes();
	}

	private void setUpInjections() {
		Dart.inject(this, getArguments());
	}

	private void setUpRoutes() {
		setUpRoutesList();
		setUpRoutesAdapter();
		setUpRoutesContent();
	}

	private void setUpRoutesList() {
		if (Frames.at(getActivity()).areAvailable()) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	private void setUpRoutesAdapter() {
		setListAdapter(new StopRoutesAdapter(getActivity()));
	}

	private void setUpRoutesContent() {
		getLoaderManager().initLoader(Loaders.STOP_ROUTES, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getRoutesUri(), null, null, null, null);
	}

	private Uri getRoutesUri() {
		return BusTimeContract.Routes.getRoutesUri(stop.getId());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> routesLoader, Cursor routesCursor) {
		ListOrderAnimator animator = new ListOrderAnimator(getListView());
		animator.saveListState();

		getRoutesAdapter().swapCursor(new StopRoutesCursor(routesCursor));

		animator.animateReorderedListState();
	}

	private StopRoutesAdapter getRoutesAdapter() {
		return (StopRoutesAdapter) getListAdapter();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> routesLoader) {
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		BusProvider.getBus().post(new RouteSelectedEvent(getRoutesAdapter().getItem(position).getRoute()));
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);

		// Ignore first run to avoid double content loading

		if (isTimerAvailable()) {
			setUpRoutesContentForced();
		}

		setUpTimer();
	}

	private boolean isTimerAvailable() {
		return timer != null;
	}

	private void setUpRoutesContentForced() {
		getLoaderManager().initLoader(Loaders.STOP_ROUTES, null, this).forceLoad();
	}

	private void setUpTimer() {
		this.timer = new Timer();

		timer.start();
	}

	@Subscribe
	public void onTimeChanged(TimeChangedEvent event) {
		setUpRoutesContentForced();
	}

	@Override
	public void onPause() {
		super.onPause();

		BusProvider.getBus().unregister(this);

		tearDownTimer();
	}

	private void tearDownTimer() {
		timer.stop();
	}
}
