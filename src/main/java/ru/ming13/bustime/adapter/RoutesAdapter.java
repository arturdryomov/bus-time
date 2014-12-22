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
import ru.ming13.bustime.model.Route;

public class RoutesAdapter extends IterableCursorAdapter<Route>
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

	public RoutesAdapter(@NonNull Context context) {
		super(context, null, 0);

		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Route route, ViewGroup routeViewContainer) {
		View routeView = layoutInflater.inflate(R.layout.view_list_item_route, routeViewContainer, false);

		routeView.setTag(new RouteViewHolder(routeView));

		return routeView;
	}

	@Override
	public void bindView(View routeView, Context context, Route route) {
		RouteViewHolder routeViewHolder = (RouteViewHolder) routeView.getTag();

		routeViewHolder.routeNumber.setText(route.getNumber());
		routeViewHolder.routeDescription.setText(route.getDescription());
	}
}
