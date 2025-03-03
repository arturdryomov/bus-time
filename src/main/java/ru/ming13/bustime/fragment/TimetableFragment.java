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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import icepick.Icepick;
import icepick.Icicle;
import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.TimetableAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.TimeChangedEvent;
import ru.ming13.bustime.bus.TimetableInformationLoadedEvent;
import ru.ming13.bustime.cursor.TimetableCursor;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.task.TimetableInformationLoadingTask;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Loaders;
import ru.ming13.bustime.util.Timer;

public class TimetableFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static final class Defaults
	{
		private Defaults() {
		}

		private static final int PAST_VISIBLE_TRIPS_COUNT = 1;
	}

	public static TimetableFragment newInstance(@NonNull Route route, @NonNull Stop stop) {
		TimetableFragment fragment = new TimetableFragment();

		fragment.setArguments(buildArguments(route, stop));

		return fragment;
	}

	private static Bundle buildArguments(Route route, Stop stop) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.ROUTE, route);
		arguments.putParcelable(Fragments.Arguments.STOP, stop);

		return arguments;
	}

	@InjectView(android.R.id.list)
	View contentLayout;

	@InjectView(R.id.empty)
	ViewGroup emptyLayout;

	@InjectExtra(Fragments.Arguments.ROUTE)
	Route route;

	@InjectExtra(Fragments.Arguments.STOP)
	Stop stop;

	@Icicle
	int timetableType;

	private int timetableClosestTripPosition;

	private Timer timer;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_timetable, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpInjections();

		setUpState(savedInstanceState);

		setUpToolbar();

		setUpTimetableType();
	}

	private void setUpInjections() {
		ButterKnife.inject(this, getView());

		Dart.inject(this, getArguments());
	}

	private void setUpState(Bundle state) {
		Icepick.restoreInstanceState(this, state);
	}

	private void setUpToolbar() {
		setHasOptionsMenu(true);
	}

	private void setUpTimetableType() {
		if (timetableType == 0) {
			TimetableInformationLoadingTask.execute(getActivity(), getTimetableUri());
		} else {
			setUpTimetable(timetableType);
		}
	}

	private Uri getTimetableUri() {
		return BusTimeContract.Timetable.getTimetableUri(route.getId(), stop.getId());
	}

	@Subscribe
	public void onTimetableInformationLoaded(TimetableInformationLoadedEvent event) {
		this.timetableClosestTripPosition = event.getTimetableClosestTripPosition();

		setUpTimetable(event.getTimetableType());
	}

	private void setUpTimetable(int timetableType) {
		this.timetableType = timetableType;

		setUpCurrentActionBar();

		setUpTimetable();
	}

	private void setUpCurrentActionBar() {
		getActivity().supportInvalidateOptionsMenu();
	}

	private void setUpTimetable() {
		setUpTimetableList();
		setUpTimetableAdapter();
		setUpTimetableContent();
	}

	private void setUpTimetableList() {
		getListView().setSelector(android.R.color.transparent);
	}

	private void setUpTimetableAdapter() {
		setListAdapter(new TimetableAdapter(getActivity()));
	}

	private void setUpTimetableContent() {
		getLoaderManager().initLoader(getLoaderId(), null, this);
	}

	private int getLoaderId() {
		switch (timetableType) {
			case BusTimeContract.Timetable.Type.WORKDAYS:
				return Loaders.WORKDAYS_TIMETABLE;

			case BusTimeContract.Timetable.Type.WEEKEND:
				return Loaders.WEEKEND_TIMETABLE;

			default:
				return Loaders.FULL_WEEK_TIMETABLE;
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getCurrentTimetableUri(), null, null, null, null);
	}

	private Uri getCurrentTimetableUri() {
		return BusTimeContract.Timetable.getTimetableUri(getTimetableUri(), timetableType);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> timetableLoader, Cursor timetableCursor) {
		getTimetableAdapter().swapCursor(new TimetableCursor(timetableCursor));

		setUpTimetableLayout(timetableCursor);

		showTimetableClosestTrip();
	}

	private void setUpTimetableLayout(Cursor timetableCursor) {
		if (timetableCursor.getCount() == 0) {
			contentLayout.setVisibility(View.GONE);
			emptyLayout.setVisibility(View.VISIBLE);
		} else {
			contentLayout.setVisibility(View.VISIBLE);
			emptyLayout.setVisibility(View.GONE);
		}

	}

	private TimetableAdapter getTimetableAdapter() {
		return (TimetableAdapter) getListAdapter();
	}

	private void showTimetableClosestTrip() {
		if (timetableClosestTripPosition != 0) {
			setSelection(timetableClosestTripPosition - Defaults.PAST_VISIBLE_TRIPS_COUNT);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> timetableCursor) {
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);

		if (!BusTimeContract.Timetable.Type.isWeekPartDependent(timetableType)) {
			return;
		}

		menuInflater.inflate(R.menu.action_bar_timetable, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		switch (timetableType) {
			case BusTimeContract.Timetable.Type.WORKDAYS:
				menu.findItem(R.id.menu_timetable_workdays).setChecked(true);
				break;

			case BusTimeContract.Timetable.Type.WEEKEND:
				menu.findItem(R.id.menu_timetable_weekend).setChecked(true);
				break;

			default:
				break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		menuItem.setChecked(!menuItem.isChecked());

		switch (menuItem.getItemId()) {
			case R.id.menu_timetable_workdays:
				timetableType = BusTimeContract.Timetable.Type.WORKDAYS;
				setUpTimetableContent();
				return true;

			case R.id.menu_timetable_weekend:
				timetableType = BusTimeContract.Timetable.Type.WEEKEND;
				setUpTimetableContent();
				return true;

			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		BusProvider.getBus().register(this);

		setUpTimer();

		refreshRemainingTime();
	}

	private void setUpTimer() {
		this.timer = new Timer();

		timer.start();
	}

	@Subscribe
	public void onTimeChanged(TimeChangedEvent event) {
		refreshRemainingTime();
	}

	private void refreshRemainingTime() {
		if (getTimetableAdapter() != null) {
			getTimetableAdapter().notifyDataSetChanged();
		}
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		tearDownState(outState);
	}

	private void tearDownState(Bundle state) {
		Icepick.saveInstanceState(this, state);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		ButterKnife.reset(this);
	}
}
