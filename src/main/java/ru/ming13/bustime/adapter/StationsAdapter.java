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

public class StationsAdapter extends CursorAdapter
{
	private static final class StationViewHolder
	{
		public TextView nameTextView;
		public TextView directionTextView;
	}

	private final LayoutInflater layoutInflater;

	private final String placeholderEmptyStationDirection;

	public StationsAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);

		placeholderEmptyStationDirection = context.getString(R.string.placeholder_empty_direction);
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
		return layoutInflater.inflate(R.layout.list_item_station, viewGroup, false);
	}

	private StationViewHolder buildStationViewHolder(View stationView) {
		StationViewHolder stationViewHolder = new StationViewHolder();

		stationViewHolder.nameTextView = (TextView) stationView.findViewById(R.id.text_name);
		stationViewHolder.directionTextView = (TextView) stationView.findViewById(R.id.text_direction);

		return stationViewHolder;
	}

	private void setUpStationViewHolder(View stationView, StationViewHolder stationViewHolder) {
		stationView.setTag(stationViewHolder);
	}

	private void setUpStationInformation(Cursor stationsCursor, StationViewHolder stationViewHolder) {
		String stationName = getStationName(stationsCursor);
		String stationDirection = getStationDirection(stationsCursor);

		stationViewHolder.nameTextView.setText(stationName);
		stationViewHolder.directionTextView.setText(stationDirection);
	}

	private String getStationName(Cursor stationsCursor) {
		return stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.NAME));
	}

	private String getStationDirection(Cursor stationsCursor) {
		String direction = stationsCursor.getString(
			stationsCursor.getColumnIndex(BusTimeContract.Stations.DIRECTION));

		if (direction == null) {
			return placeholderEmptyStationDirection;
		}

		return direction;
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
