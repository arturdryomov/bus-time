package ru.ming13.bustime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venmo.cursor.support.IterableCursorAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.ming13.bustime.R;
import ru.ming13.bustime.model.Stop;
import ru.ming13.bustime.util.Strings;

public class StopsAdapter extends IterableCursorAdapter<Stop>
{
	static final class StopViewHolder
	{
		@Bind(R.id.text_name)
		public TextView stopName;

		@Bind(R.id.text_direction)
		public TextView stopDirection;

		public StopViewHolder(View stopView) {
			ButterKnife.bind(this, stopView);
		}
	}

	private final LayoutInflater layoutInflater;

	public StopsAdapter(@NonNull Context context) {
		super(context, null, 0);

		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Stop stop, ViewGroup stopViewContainer) {
		View stopView = layoutInflater.inflate(R.layout.view_list_item_stop, stopViewContainer, false);

		stopView.setTag(new StopViewHolder(stopView));

		return stopView;
	}

	@Override
	public void bindView(View stopView, Context context, Stop stop) {
		StopViewHolder stopViewHolder = (StopViewHolder) stopView.getTag();

		stopViewHolder.stopName.setText(stop.getName());
		stopViewHolder.stopDirection.setText(stop.getDirection());

		if (Strings.isBlank(stop.getDirection())) {
			stopViewHolder.stopDirection.setVisibility(View.GONE);
		} else {
			stopViewHolder.stopDirection.setVisibility(View.VISIBLE);
		}
	}
}
