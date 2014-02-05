package ru.ming13.bustime.util;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import ru.ming13.bustime.R;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;

public final class TitleBuilder
{
	private final Context context;

	public static TitleBuilder with(Context context) {
		return new TitleBuilder(context);
	}

	private TitleBuilder(Context context) {
		this.context = context;
	}

	public String buildRouteTitle(Route route) {
		return context.getString(R.string.mask_route_title, route.getNumber(), route.getDescription());
	}

	public String buildStopTitle(Stop stop) {
		if (StringUtils.isEmpty(stop.getDirection())) {
			return stop.getName();
		}

		return context.getString(R.string.mask_stop_title, stop.getName(), stop.getDirection());
	}
}
