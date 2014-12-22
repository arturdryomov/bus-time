package ru.ming13.bustime.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venmo.cursor.support.IterableCursorAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.model.TimetableTime;

public class TimetableAdapter extends IterableCursorAdapter<TimetableTime>
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
	public View newView(Context context, TimetableTime timetableTime, ViewGroup timeViewContainer) {
		View timeView = layoutInflater.inflate(R.layout.view_list_item_time, timeViewContainer, false);

		timeView.setTag(new TimeViewHolder(timeView));

		return timeView;
	}

	@Override
	public void bindView(View timeView, Context context, TimetableTime timetableTime) {
		TimeViewHolder timeViewHolder = (TimeViewHolder) timeView.getTag();

		timeViewHolder.exactTime.setText(timetableTime.getTime().toSystemString(context));
		timeViewHolder.relativeTime.setText(timetableTime.getTime().toRelativeString(context));
	}
}
