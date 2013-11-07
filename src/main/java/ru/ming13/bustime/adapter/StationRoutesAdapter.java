package ru.ming13.bustime.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ming13.bustime.R;
import ru.ming13.bustime.provider.BusTimeContract;
import ru.ming13.bustime.util.Time;

public class StationRoutesAdapter extends CursorAdapter
{
	private static final class RouteViewHolder
	{
		public TextView numberTextView;
		public TextView descriptionTextView;
		public TextView timeTextView;
	}

	private final LayoutInflater layoutInflater;

	public StationRoutesAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor routesCursor, ViewGroup viewGroup) {
		View routeView = buildRouteView(viewGroup);
		RouteViewHolder routeViewHolder = buildRouteViewHolder(routeView);

		setUpRouteViewHolder(routeView, routeViewHolder);
		setUpRouteInformation(context, routesCursor, routeViewHolder);

		return routeView;
	}

	private View buildRouteView(ViewGroup viewGroup) {
		return layoutInflater.inflate(R.layout.list_item_station_route, viewGroup, false);
	}

	private RouteViewHolder buildRouteViewHolder(View routeView) {
		RouteViewHolder routeViewHolder = new RouteViewHolder();

		routeViewHolder.numberTextView = (TextView) routeView.findViewById(R.id.text_number);
		routeViewHolder.descriptionTextView = (TextView) routeView.findViewById(R.id.text_description);
		routeViewHolder.timeTextView = (TextView) routeView.findViewById(R.id.text_time);

		return routeViewHolder;
	}

	private void setUpRouteViewHolder(View routeView, RouteViewHolder routeViewHolder) {
		routeView.setTag(routeViewHolder);
	}

	private void setUpRouteInformation(Context context, Cursor routesCursor, RouteViewHolder routeViewHolder) {
		String routeNumber = getRouteNumber(routesCursor);
		String routeDescription = getRouteDescription(routesCursor);
		String routeTime = getRouteTime(context, routesCursor);

		routeViewHolder.numberTextView.setText(routeNumber);
		routeViewHolder.descriptionTextView.setText(routeDescription);
		routeViewHolder.timeTextView.setText(routeTime);
	}

	private String getRouteNumber(Cursor routesCursor) {
		return routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Routes.NUMBER));
	}

	private String getRouteDescription(Cursor routesCursor) {
		return routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Routes.DESCRIPTION));
	}

	private String getRouteTime(Context context, Cursor routesCursor) {
		String routeTime = getRouteTime(routesCursor);

		if (TextUtils.isEmpty(routeTime)) {
			return context.getString(R.string.token_no_trips);
		}

		return Time.from(routeTime).toRelativeString(context);
	}

	private String getRouteTime(Cursor routesCursor) {
		return routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Timetable.ARRIVAL_TIME));
	}

	@Override
	public void bindView(View routeView, Context context, Cursor routesCursor) {
		RouteViewHolder routeViewHolder = getRouteViewHolder(routeView);

		setUpRouteInformation(context, routesCursor, routeViewHolder);
	}

	private RouteViewHolder getRouteViewHolder(View routeView) {
		return (RouteViewHolder) routeView.getTag();
	}
}
