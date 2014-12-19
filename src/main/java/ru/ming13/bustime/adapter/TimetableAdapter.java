package ru.ming13.bustime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public class TimetableAdapter extends CursorAdapter
{
	static final class TimeViewHolder
	{
		@InjectView(R.id.text_time_exact)
		public TextView exactTime;

		@InjectView(R.id.text_time_relative)
		public TextView relativeTime;

		public TimeViewHolder(View timeView) {
			ButterKnife.inject(this, timeView);
		}
	}

	private final LayoutInflater layoutInflater;

	public TimetableAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor timetableCursor, ViewGroup timeViewContainer) {
		View timeView = layoutInflater.inflate(R.layout.view_list_item_time, timeViewContainer, false);

		timeView.setTag(new TimeViewHolder(timeView));

		return timeView;
	}

	@Override
	public void bindView(View timeView, Context context, Cursor timetableCursor) {
		TimeViewHolder timeViewHolder = (TimeViewHolder) timeView.getTag();

		Time time = Time.from(getArrivalTime(timetableCursor));

		timeViewHolder.exactTime.setText(time.toSystemString(context));
		timeViewHolder.relativeTime.setText(time.toRelativeString(context));
	}

	private String getArrivalTime(Cursor timetableCursor) {
		return timetableCursor.getString(
			timetableCursor.getColumnIndex(BusTimeContract.Timetable.ARRIVAL_TIME));
	}
}
