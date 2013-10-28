package ru.ming13.bustime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ming13.bustime.R;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public class TimetableAdapter extends CursorAdapter
{
	private static final class TimeViewHolder
	{
		public TextView exactTimeTextView;
		public TextView relativeTimeTextView;
	}

	private final LayoutInflater layoutInflater;

	public TimetableAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor timetableCursor, ViewGroup viewGroup) {
		View timeView = buildTimeView(viewGroup);
		TimeViewHolder timeViewHolder = buildTimeViewHolder(timeView);

		setUpTimeViewHolder(timeView, timeViewHolder);
		setUpTimeInformation(context, timetableCursor, timeViewHolder);

		return timeView;
	}

	private View buildTimeView(ViewGroup viewGroup) {
		return layoutInflater.inflate(R.layout.list_item_time, viewGroup, false);
	}

	private TimeViewHolder buildTimeViewHolder(View timeView) {
		TimeViewHolder timeViewHolder = new TimeViewHolder();

		timeViewHolder.exactTimeTextView = (TextView) timeView.findViewById(R.id.text_time_exact);
		timeViewHolder.relativeTimeTextView = (TextView) timeView.findViewById(R.id.text_time_relative);

		return timeViewHolder;
	}

	private void setUpTimeViewHolder(View timeView, TimeViewHolder timeViewHolder) {
		timeView.setTag(timeViewHolder);
	}

	private void setUpTimeInformation(Context context, Cursor timetableCursor, TimeViewHolder timeViewHolder) {
		Time time = Time.from(getArrivalTime(timetableCursor));

		timeViewHolder.exactTimeTextView.setText(time.toSystemString(context));
		timeViewHolder.relativeTimeTextView.setText(time.toRelativeString(context));
	}

	private String getArrivalTime(Cursor timetableCursor) {
		return timetableCursor.getString(
			timetableCursor.getColumnIndex(BusTimeContract.Timetable.ARRIVAL_TIME));
	}

	@Override
	public void bindView(View timeView, Context context, Cursor timetableCursor) {
		TimeViewHolder timeViewHolder = getTimeViewHolder(timeView);

		setUpTimeInformation(context, timetableCursor, timeViewHolder);
	}

	private TimeViewHolder getTimeViewHolder(View timeView) {
		return (TimeViewHolder) timeView.getTag();
	}
}
