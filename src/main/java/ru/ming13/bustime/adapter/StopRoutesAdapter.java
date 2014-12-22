package ru.ming13.bustime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venmo.cursor.support.IterableCursorAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ru.ming13.bustime.R;
import ru.ming13.bustime.model.StopRoute;

public class StopRoutesAdapter extends IterableCursorAdapter<StopRoute>
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

	public StopRoutesAdapter(@NonNull Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, StopRoute stopRoute, ViewGroup routeViewContainer) {
		View routeView = layoutInflater.inflate(R.layout.view_list_item_stop_route, routeViewContainer, false);

		routeView.setTag(new RouteViewHolder(routeView));

		return routeView;
	}

	@Override
	public void bindView(View routeView, Context context, StopRoute stopRoute) {
		RouteViewHolder routeViewHolder = (RouteViewHolder) routeView.getTag();

		routeViewHolder.routeNumber.setText(stopRoute.getRoute().getNumber());
		routeViewHolder.routeDescription.setText(stopRoute.getRoute().getDescription());

		if (stopRoute.getRouteTime().isEmpty()) {
			routeViewHolder.routeTime.setText(R.string.token_no_trips);
		} else {
			routeViewHolder.routeTime.setText(stopRoute.getRouteTime().toRelativeString(context));
		}
	}
}
