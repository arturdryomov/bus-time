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
import ru.ming13.bustime.util.Time;

public class StopRoutesAdapter extends CursorAdapter
{
	static final class RouteViewHolder
	{
		@InjectView(R.id.text_number)
		public TextView routeNumber;

		@InjectView(R.id.text_description)
		public TextView routeDescription;

		@InjectView(R.id.text_time)
		public TextView routeTime;

		public RouteViewHolder(View routeView) {
			ButterKnife.inject(this, routeView);
		}
	}

	private final LayoutInflater layoutInflater;

	public StopRoutesAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor routesCursor, ViewGroup stopViewContainer) {
		View routeView = layoutInflater.inflate(R.layout.view_list_item_stop_route, stopViewContainer, false);

		routeView.setTag(new RouteViewHolder(routeView));

		return routeView;
	}

	@Override
	public void bindView(View routeView, Context context, Cursor routesCursor) {
		RouteViewHolder routeViewHolder = (RouteViewHolder) routeView.getTag();

		String routeNumber = getRouteNumber(routesCursor);
		String routeDescription = getRouteDescription(routesCursor);
		String routeTime = getRouteTime(context, routesCursor);

		routeViewHolder.routeNumber.setText(routeNumber);
		routeViewHolder.routeDescription.setText(routeDescription);
		routeViewHolder.routeTime.setText(routeTime);
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

		if (StringUtils.isBlank(routeTime)) {
			return context.getString(R.string.token_no_trips);
		}

		return Time.from(routeTime).toRelativeString(context);
	}

	private String getRouteTime(Cursor routesCursor) {
		return routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Timetable.ARRIVAL_TIME));
	}
}
