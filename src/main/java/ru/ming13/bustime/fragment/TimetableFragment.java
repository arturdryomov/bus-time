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

import com.squareup.otto.Subscribe;

import ru.ming13.bustime.R;
import ru.ming13.bustime.adapter.TimetableAdapter;
import ru.ming13.bustime.bus.BusProvider;
import ru.ming13.bustime.bus.ClosestTimeFoundEvent;
import ru.ming13.bustime.bus.TimeChangedEvent;
import ru.ming13.bustime.task.ClosestTimeSearchTask;
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

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
		return layoutInflater.inflate(R.layout.fragment_timetable, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setUpTimetable();
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
		getLoaderManager().initLoader(Loaders.TIMETABLE, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArguments) {
		return new CursorLoader(getActivity(), getTimetableUri(), null, null, null, null);
	}

	private Uri getTimetableUri() {
		return getArguments().getParcelable(Fragments.Arguments.URI);
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

	private void showTimetable() {
		getView().findViewById(android.R.id.list).setVisibility(View.VISIBLE);
		getView().findViewById(R.id.layout_message).setVisibility(View.GONE);
	}

	private void showMessage() {
		getView().findViewById(android.R.id.list).setVisibility(View.GONE);
		getView().findViewById(R.id.layout_message).setVisibility(View.VISIBLE);
	}

	private void setUpClosestTime() {
		ClosestTimeSearchTask.execute(getActivity(), getTimetableUri());
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
	public void onResume() {
		super.onResume();

		setUpTimer();

		refreshRemainingTime();
	}

	private void setUpTimer() {
		BusProvider.getBus().register(this);

		timer = new Timer();
		timer.start();
	}

	@Subscribe
	public void onTimeChanged(TimeChangedEvent event) {
		refreshRemainingTime();
	}

	private void refreshRemainingTime() {
		getTimetableAdapter().notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();

		tearDownTimer();
	}

	private void tearDownTimer() {
		BusProvider.getBus().unregister(this);

		timer.stop();
	}
}
