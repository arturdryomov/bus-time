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
		public ImageView indicatorImageView;
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
		return layoutInflater.inflate(R.layout.list_item_route_stop, viewGroup, false);
	}

	private StopViewHolder buildStopViewHolder(View stopView) {
		StopViewHolder stopViewHolder = new StopViewHolder();

		stopViewHolder.nameTextView = (TextView) stopView.findViewById(R.id.text_name);
		stopViewHolder.directionTextView = (TextView) stopView.findViewById(R.id.text_direction);
		stopViewHolder.indicatorImageView = (ImageView) stopView.findViewById(R.id.indicator_stop);

		return stopViewHolder;
	}

	private void setUpStopViewHolder(View stopView, StopViewHolder stopViewHolder) {
		stopView.setTag(stopViewHolder);
	}

	@Override
	public void bindView(View stopView, Context context, Cursor stopsCursor) {
		StopViewHolder stopViewHolder = getStopViewHolder(stopView);

		setUpStopInformation(stopsCursor, stopViewHolder);
	}

	private StopViewHolder getStopViewHolder(View stopView) {
		return (StopViewHolder) stopView.getTag();
	}

	private void setUpStopInformation(Cursor stopsCursor, StopViewHolder stopViewHolder) {
		String stopName = getStopName(stopsCursor);
		String stopDirection = getStopDirection(stopsCursor);
		int stopIndicator = getStopIndicator(stopsCursor);

		stopViewHolder.nameTextView.setText(stopName);
		stopViewHolder.directionTextView.setText(stopDirection);
		stopViewHolder.indicatorImageView.setBackgroundResource(stopIndicator);

		stopViewHolder.directionTextView.setVisibility(getStopDirectionVisibility(stopDirection));
	}

	private String getStopName(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.NAME));
	}

	private String getStopDirection(Cursor stopsCursor) {
		return stopsCursor.getString(
			stopsCursor.getColumnIndex(BusTimeContract.Stops.DIRECTION));
	}

	private int getStopIndicator(Cursor stopsCursor) {
		if (stopsCursor.getPosition() == 0) {
			return R.drawable.ic_indicator_stop_line_first;
		}

		if (stopsCursor.getPosition() == stopsCursor.getCount() - 1) {
			return R.drawable.ic_indicator_stop_line_last;
		}

		return R.drawable.ic_indicator_stop_line_middle;
	}

	private int getStopDirectionVisibility(String stopDirection) {
		if (StringUtils.isEmpty(stopDirection)) {
			return View.GONE;
		} else {
			return View.VISIBLE;
		}
	}
}
