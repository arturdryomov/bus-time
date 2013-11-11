package ru.ming13.bustime.util;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;

import ru.ming13.bustime.R;

public final class TitleBuilder
{
	private final Context context;

	public static TitleBuilder with(Context context) {
		return new TitleBuilder(context);
	}

	private TitleBuilder(Context context) {
		this.context = context;
	}

	public String buildRouteTitle(String routeNumber, String routeDescription) {
		return context.getString(R.string.mask_route_title, routeNumber, routeDescription);
	}

	public String buildStationTitle(String stationName, String stationDirection) {
		if (StringUtils.isEmpty(stationDirection)) {
			return stationName;
		}

		return context.getString(R.string.mask_station_title, stationName, stationDirection);
	}
}
