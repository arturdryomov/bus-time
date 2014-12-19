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

public class RoutesAdapter extends CursorAdapter
{
	static final class RouteViewHolder
	{
		@InjectView(R.id.text_number)
		public TextView routeNumber;

		@InjectView(R.id.text_description)
		public TextView routeDescription;

		public RouteViewHolder(View routeView) {
			ButterKnife.inject(this, routeView);
		}
	}

	private final LayoutInflater layoutInflater;

	public RoutesAdapter(Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor routesCursor, ViewGroup routeViewContainer) {
		View routeView = layoutInflater.inflate(R.layout.view_list_item_route, routeViewContainer, false);

		routeView.setTag(new RouteViewHolder(routeView));

		return routeView;
	}

	@Override
	public void bindView(View routeView, Context context, Cursor routesCursor) {
		RouteViewHolder routeViewHolder = (RouteViewHolder) routeView.getTag();

		String routeNumber = getRouteNumber(routesCursor);
		String routeDescription = getRouteDescription(routesCursor);

		routeViewHolder.routeNumber.setText(routeNumber);
		routeViewHolder.routeDescription.setText(routeDescription);
	}

	private String getRouteNumber(Cursor routesCursor) {
		return routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Routes.NUMBER));
	}

	private String getRouteDescription(Cursor routesCursor) {
		return routesCursor.getString(
			routesCursor.getColumnIndex(BusTimeContract.Routes.DESCRIPTION));
	}
}
