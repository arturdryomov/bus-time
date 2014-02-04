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

public class RouteStationsAdapter extends CursorAdapter
{
	private static final class StationViewHolder
	{
		public TextView nameTextView;
		public TextView directionTextView;
		public ImageView indicatorImageView;
	}

	private final LayoutInflater layoutInflater;

	public RouteStationsAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor stationsCursor, ViewGroup viewGroup) {
		View stationView = buildStationView(viewGroup);
		StationViewHolder stationViewHolder = buildStationViewHolder(stationView);

		setUpStationViewHolder(stationView, stationViewHolder);
		setUpStationInformation(stationsCursor, stationViewHolder);

		return stationView;
	}

	private View buildStationView(ViewGroup viewGroup) {
		return layoutInflater.inflate(R.layout.list_item_route_station, viewGroup, false);
	}

	private StationViewHolder buildStationViewHolder(View stationView) {
		StationViewHolder stationViewHolder = new StationViewHolder();

		stationViewHolder.nameTextView = (TextView) stationView.findViewById(R.id.text_name);
		stationViewHolder.directionTextView = (TextView) stationView.findViewById(R.id.text_direction);
		stationViewHolder.indicatorImageView = (ImageView) stationView.findViewById(R.id.indicator_station);

		return stationViewHolder;
	}

	private void setUpStationViewHolder(View stationView, StationViewHolder stationViewHolder) {
		stationView.setTag(stationViewHolder);
	}

	private void setUpStationInformation(Cursor stationsCursor, StationViewHolder stationViewHolder) {
		String stationName = getStationName(stationsCursor);
		String stationDirection = getStationDirection(stationsCursor);
		int stationIndicator = getStationIndicator(stationsCursor);

		stationViewHolder.nameTextView.setText(stationName);
		stationViewHolder.directionTextView.setText(stationDirection);
		stationViewHolder.indicatorImageView.setBackgroundResource(stationIndicator);

		stationViewHolder.directionTextView.setVisibility(getStationDirectionVisibility(stationDirection));
	}

	private String getStationName(Cursor stationsCursor) {
		return stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.NAME));
	}

	private String getStationDirection(Cursor stationsCursor) {
		return stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.DIRECTION));
	}

	private int getStationIndicator(Cursor stationsCursor) {
		if (stationsCursor.getPosition() == 0) {
			return R.drawable.ic_indicator_station_line_first;
		}

		if (stationsCursor.getPosition() == stationsCursor.getCount() - 1) {
			return R.drawable.ic_indicator_station_line_last;
		}

		return R.drawable.ic_indicator_station_line_middle;
	}

	private int getStationDirectionVisibility(String stationDirection) {
		if (StringUtils.isEmpty(stationDirection)) {
			return View.GONE;
		} else {
			return View.VISIBLE;
		}
	}

	@Override
	public void bindView(View stationView, Context context, Cursor stationsCursor) {
		StationViewHolder stationViewHolder = getStationViewHolder(stationView);

		setUpStationInformation(stationsCursor, stationViewHolder);
	}

	private StationViewHolder getStationViewHolder(View stationView) {
		return (StationViewHolder) stationView.getTag();
	}
}
