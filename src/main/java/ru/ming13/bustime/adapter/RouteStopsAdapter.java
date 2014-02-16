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

import ru.ming13.bustime.R;
import ru.ming13.bustime.provider.BusTimeContract;

public class RouteStopsAdapter extends CursorAdapter
{
	private static final class StopViewHolder
	{
		public TextView nameTextView;
		public TextView directionTextView;
		public ImageView markerFirstImageView;
		public ImageView markerMiddleImageView;
		public ImageView markerLastImageView;
	}

	private final LayoutInflater layoutInflater;

	public RouteStopsAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor stopsCursor, ViewGroup viewGroup) {
		View stopView = buildStopView(viewGroup);
		StopViewHolder stopViewHolder = buildStopViewHolder(stopView);

		setUpStopViewHolder(stopView, stopViewHolder);

		return stopView;
	}

	private View buildStopView(ViewGroup viewGroup) {
		return layoutInflater.inflate(R.layout.view_list_item_route_stop, viewGroup, false);
	}

	private StopViewHolder buildStopViewHolder(View stopView) {
		StopViewHolder stopViewHolder = new StopViewHolder();

		stopViewHolder.nameTextView = (TextView) stopView.findViewById(R.id.text_name);
		stopViewHolder.directionTextView = (TextView) stopView.findViewById(R.id.text_direction);
		stopViewHolder.markerFirstImageView = (ImageView) stopView.findViewById(R.id.image_marker_first);
		stopViewHolder.markerMiddleImageView = (ImageView) stopView.findViewById(R.id.image_marker_middle);
		stopViewHolder.markerLastImageView = (ImageView) stopView.findViewById(R.id.image_marker_last);

		return stopViewHolder;
	}

	private void setUpStopViewHolder(View stopView, StopViewHolder stopViewHolder) {
		stopView.setTag(stopViewHolder);
	}

	@Override
	public void bindView(View stopView, Context context, Cursor stopsCursor) {
		StopViewHolder stopViewHolder = getStopViewHolder(stopView);

		setUpStopInformation(stopsCursor, stopViewHolder);
		setUpStopInformationVisibility(stopsCursor, stopViewHolder);
	}

	private StopViewHolder getStopViewHolder(View stopView) {
		return (StopViewHolder) stopView.getTag();
	}

	private void setUpStopInformation(Cursor stopsCursor, StopViewHolder stopViewHolder) {
		String stopName = getStopName(stopsCursor);
		String stopDirection = getStopDirection(stopsCursor);

		stopViewHolder.nameTextView.setText(stopName);
		stopViewHolder.directionTextView.setText(stopDirection);
	}

	private String getStopName(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.NAME));
	}

	private String getStopDirection(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.DIRECTION));
	}

	private void setUpStopInformationVisibility(Cursor stopsCursor, StopViewHolder stopViewHolder) {
		setUpStopMarkerVisibility(stopsCursor, stopViewHolder);
		setUpStopDirectionVisibility(stopsCursor, stopViewHolder);
	}

	private void setUpStopMarkerVisibility(Cursor stopsCursor, StopViewHolder stopViewHolder) {
		if (stopsCursor.isFirst()) {
			stopViewHolder.markerFirstImageView.setVisibility(View.VISIBLE);
			stopViewHolder.markerMiddleImageView.setVisibility(View.GONE);
			stopViewHolder.markerLastImageView.setVisibility(View.GONE);

			return;
		}

		if (stopsCursor.isLast()) {
			stopViewHolder.markerFirstImageView.setVisibility(View.GONE);
			stopViewHolder.markerMiddleImageView.setVisibility(View.GONE);
			stopViewHolder.markerLastImageView.setVisibility(View.VISIBLE);

			return;
		}

		stopViewHolder.markerFirstImageView.setVisibility(View.GONE);
		stopViewHolder.markerMiddleImageView.setVisibility(View.VISIBLE);
		stopViewHolder.markerLastImageView.setVisibility(View.GONE);
	}

	private void setUpStopDirectionVisibility(Cursor stopsCursor, StopViewHolder stopViewHolder) {
		if (StringUtils.isBlank(getStopDirection(stopsCursor))) {
			stopViewHolder.directionTextView.setVisibility(View.GONE);
		} else {
			stopViewHolder.directionTextView.setVisibility(View.VISIBLE);
		}
	}
}
