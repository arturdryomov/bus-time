package ru.ming13.bustime.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ListAdapter;

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.TimetableAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.ClosestTimeFoundEvent;
import ru.ming13.bustime.bus.TimeChangedEvent;
import ru.ming13.bustime.bus.TimetableTypeQueriedEvent;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.task.ClosestTimeSearchTask;
import ru.ming13.bustime.task.TimetableTypeQueryingTask;
import ru.ming13.bustime.util.Fragments;
import ru.ming13.bustime.util.Loaders;
import ru.ming13.bustime.util.Timer;

public class TimetableFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
	private static final int PREVIOUS_VISIBLE_TIMES_COUNT = 1;

	public static TimetableFragment newInstance(Uri uri) {
		TimetableFragment fragment = new TimetableFragment();

		fragment.setArguments(buildArguments(uri));

		return fragment;
	}

	private static Bundle buildArguments(Uri uri) {
		Bundle arguments = new Bundle();

		arguments.putParcelable(Fragments.Arguments.URI, uri);

		return arguments;
	}

	private Timer timer;
	private int type;

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_timetable, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpActionBar();

		setUpTimetableType();
	}

	private void setUpActionBar() {
		setHasOptionsMenu(true);
	}

	private void setUpTimetableType() {
		TimetableTypeQueryingTask.execute(getActivity(), getTimetableUri());
	}

	private Uri getTimetableUri() {
		return getArguments().getParcelable(Fragments.Arguments.URI);
	}

	@Subscribe
	public void onTimetableTypeQueried(TimetableTypeQueriedEvent event) {
		type = event.getTimetableType();

		setUpCurrentActionBar();

		setUpTimetable();
	}

	private void setUpCurrentActionBar() {
		getActivity().supportInvalidateOptionsMenu();
	}

	private void setUpTimetable() {
		setUpTimetableAdapter();
		setUpTimetableContent();
	}

	private void setUpTimetableAdapter() {
		setListAdapter(buildTimetableAdapter());
	}

	private ListAdapter buildTimetableAdapter() {
		return new TimetableAdapter(getActivity());
	}

	private void setUpTimetableContent() {
		getLoaderManager().initLoader(getLoaderId(), null, this);
	}

	private int getLoaderId() {
		switch (type) {
			case BusTimeContract.Timetable.Type.FULL_WEEK:
				return Loaders.FULL_WEEK_TIMETABLE;

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
		return BusTimeContract.Timetable.buildTimetableUri(getTimetableUri(), type);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> timetableLoader, Cursor timetableCursor) {
		getTimetableAdapter().swapCursor(timetableCursor);

		if (isTimetableEmpty(timetableCursor)) {
			showMessage();
		} else {
			showTimetable();
			setUpClosestTime();
		}
	}

	private boolean isTimetableEmpty(Cursor timetableCursor) {
		return timetableCursor.getCount() == 0;
	}

	private void showMessage() {
		getView().findViewById(android.R.id.list).setVisibility(View.GONE);
		getView().findViewById(R.id.layout_message).setVisibility(View.VISIBLE);
	}

	private void showTimetable() {
		getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.layout_message).setVisibility(View.GONE);
	}

	private void setUpClosestTime() {
		ClosestTimeSearchTask.execute(getActivity(), getCurrentTimetableUri());
	}

	@Subscribe
	public void onClosestTimeFound(ClosestTimeFoundEvent event) {
		setUpClosestTime(event.getClosestTimePosition());
	}

	private void setUpClosestTime(int closestTimePosition) {
		setSelection(closestTimePosition - PREVIOUS_VISIBLE_TIMES_COUNT);
	}

	private TimetableAdapter getTimetableAdapter() {
		return (TimetableAdapter) getListAdapter();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> timetableCursor) {
		getTimetableAdapter().swapCursor(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
		super.onCreateOptionsMenu(menu, menuInflater);

		if (!BusTimeContract.Timetable.Type.isWeekPartDependent(type)) {
			return;
		}

		menuInflater.inflate(R.menu.action_bar_timetable, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		switch (type) {
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
				type = BusTimeContract.Timetable.Type.WORKDAYS;
				setUpTimetableContent();
				return true;

			case R.id.menu_timetable_weekend:
				type = BusTimeContract.Timetable.Type.WEEKEND;
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
		timer = new Timer();
		timer.start();
	}

	@Subscribe
	public void onTimeChanged(TimeChangedEvent event) {
		refreshRemainingTime();
	}

	private void refreshRemainingTime() {
		if (isTimetableAdapterSet()) {
			getTimetableAdapter().notifyDataSetChanged();
		}
	}

	private boolean isTimetableAdapterSet() {
		return getTimetableAdapter() != null;
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
