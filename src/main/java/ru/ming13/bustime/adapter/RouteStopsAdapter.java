package ru.ming13.bustime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.venmo.cursor.support.IterableCursorAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.ming13.bustime.R;
import ru.ming13.bustime.model.RouteStop;
import ru.ming13.bustime.util.Strings;

public class RouteStopsAdapter extends IterableCursorAdapter<RouteStop>
{
	static final class StopViewHolder
	{
		@Bind(R.id.text_name)
		public TextView stopName;

		@Bind(R.id.text_direction)
		public TextView stopDirection;

		@Bind(R.id.image_marker_first)
		public ImageView stopMarkerFirst;

		@Bind(R.id.image_marker_middle)
		public ImageView stopMarkerMiddle;

		@Bind(R.id.image_marker_last)
		public ImageView stopMarkerLast;

		public StopViewHolder(View stopView) {
			ButterKnife.bind(this, stopView);
		}
	}

	private final LayoutInflater layoutInflater;

	public RouteStopsAdapter(@NonNull Context context) {
		super(context, null, 0);

		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, RouteStop routeStop, ViewGroup stopViewContainer) {
		View stopView = layoutInflater.inflate(R.layout.view_list_item_route_stop, stopViewContainer, false);

		stopView.setTag(new StopViewHolder(stopView));

		return stopView;
	}

	@Override
	public void bindView(View stopView, Context context, RouteStop routeStop) {
		StopViewHolder stopViewHolder = (StopViewHolder) stopView.getTag();

		stopViewHolder.stopName.setText(routeStop.getStop().getName());
		stopViewHolder.stopDirection.setText(routeStop.getStop().getDirection());

		if (Strings.isBlank(routeStop.getStop().getDirection())) {
			stopViewHolder.stopDirection.setVisibility(View.GONE);
		} else {
			stopViewHolder.stopDirection.setVisibility(View.VISIBLE);
		}

		if (getCursor().isFirst()) {
			stopViewHolder.stopMarkerFirst.setVisibility(View.VISIBLE);
			stopViewHolder.stopMarkerMiddle.setVisibility(View.GONE);
			stopViewHolder.stopMarkerLast.setVisibility(View.GONE);

			return;
		}

		if (getCursor().isLast()) {
			stopViewHolder.stopMarkerFirst.setVisibility(View.GONE);
			stopViewHolder.stopMarkerMiddle.setVisibility(View.GONE);
			stopViewHolder.stopMarkerLast.setVisibility(View.VISIBLE);

			return;
		}

		stopViewHolder.stopMarkerFirst.setVisibility(View.GONE);
		stopViewHolder.stopMarkerMiddle.setVisibility(View.VISIBLE);
		stopViewHolder.stopMarkerLast.setVisibility(View.GONE);
	}
}
