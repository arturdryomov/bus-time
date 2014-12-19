package ru.ming13.bustime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.provider.BusTimeContract;

public class StopsAdapter extends CursorAdapter
{
	static final class StopViewHolder
	{
		@InjectView(R.id.text_name)
		public TextView stopName;

		@InjectView(R.id.text_direction)
		public TextView stopDirection;

		public StopViewHolder(View stopView) {
			ButterKnife.inject(this, stopView);
		}
	}

	private final LayoutInflater layoutInflater;

	public StopsAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor stopsCursor, ViewGroup stopViewContainer) {
		View stopView = layoutInflater.inflate(R.layout.view_list_item_stop, stopViewContainer, false);

		stopView.setTag(new StopViewHolder(stopView));

		return stopView;
	}

	@Override
	public void bindView(View stopView, Context context, Cursor stopsCursor) {
		StopViewHolder stopViewHolder = (StopViewHolder) stopView.getTag();

		String stopName = getStopName(stopsCursor);
		String stopDirection = getStopDirection(stopsCursor);

		stopViewHolder.stopName.setText(stopName);
		stopViewHolder.stopDirection.setText(stopDirection);

		if (StringUtils.isBlank(stopDirection)) {
			stopViewHolder.stopDirection.setVisibility(View.GONE);
		} else {
			stopViewHolder.stopDirection.setVisibility(View.VISIBLE);
		}
	}

	private String getStopName(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.NAME));
	}

	private String getStopDirection(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.DIRECTION));
	}
}
