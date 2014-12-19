package ru.ming13.bustime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.provider.BusTimeContract;

public class RouteStopsAdapter extends CursorAdapter
{
	static final class StopViewHolder
	{
		@InjectView(R.id.text_name)
		public TextView stopName;

		@InjectView(R.id.text_direction)
		public TextView stopDirection;

		@InjectView(R.id.image_marker_first)
		public ImageView stopMarkerFirst;

		@InjectView(R.id.image_marker_middle)
		public ImageView stopMarkerMiddle;

		@InjectView(R.id.image_marker_last)
		public ImageView stopMarkerLast;

		public StopViewHolder(View stopView) {
			ButterKnife.inject(this, stopView);
		}
	}

	private final LayoutInflater layoutInflater;

	public RouteStopsAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor stopsCursor, ViewGroup stopViewContainer) {
		View stopView = layoutInflater.inflate(R.layout.view_list_item_route_stop, stopViewContainer, false);

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

		if (stopsCursor.isFirst()) {
			stopViewHolder.stopMarkerFirst.setVisibility(View.VISIBLE);
			stopViewHolder.stopMarkerMiddle.setVisibility(View.GONE);
			stopViewHolder.stopMarkerLast.setVisibility(View.GONE);

			return;
		}

		if (stopsCursor.isLast()) {
			stopViewHolder.stopMarkerFirst.setVisibility(View.GONE);
			stopViewHolder.stopMarkerMiddle.setVisibility(View.GONE);
			stopViewHolder.stopMarkerLast.setVisibility(View.VISIBLE);

			return;
		}

		stopViewHolder.stopMarkerFirst.setVisibility(View.GONE);
		stopViewHolder.stopMarkerMiddle.setVisibility(View.VISIBLE);
		stopViewHolder.stopMarkerLast.setVisibility(View.GONE);
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
