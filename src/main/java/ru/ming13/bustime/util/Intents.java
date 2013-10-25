package ru.ming13.bustime.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ru.ming13.bustime.activity.RouteStationsActivity;
import ru.ming13.bustime.activity.StationRoutesActivity;
import ru.ming13.bustime.activity.TimetableActivity;

public final class Intents
{
	public static final class Extras
	{
		private Extras() {
		}

		public static final String ROUTE_DESCRIPTION = "route_description";
		public static final String ROUTE_NUMBER = "route_number";

		public static final String STATION_DIRECTION = "station_direction";
		public static final String STATION_NAME = "station_name";

		public static final String URI = "uri";
	}

	public static final class Builder
	{
		private final Context context;

		private Builder(Context context) {
			this.context = context;
		}

		public Intent buildRouteStationsIntent(Uri stationsUri, String routeNumber, String routeDescription) {
			Intent intent = new Intent(context, RouteStationsActivity.class);
			intent.putExtra(Extras.URI, stationsUri);
			intent.putExtra(Extras.ROUTE_NUMBER, routeNumber);
			intent.putExtra(Extras.ROUTE_DESCRIPTION, routeDescription);

			return intent;
		}

		public Intent buildStationRoutesIntent(Uri routesUri, String stationName, String stationDirection) {
			Intent intent = new Intent(context, StationRoutesActivity.class);
			intent.putExtra(Extras.URI, routesUri);
			intent.putExtra(Extras.STATION_NAME, stationName);
			intent.putExtra(Extras.STATION_DIRECTION, stationDirection);

			return intent;
		}

		public Intent buildTimetableIntent(Uri timetableUri, String routeNumber, String stationName, String stationDirection) {
			Intent intent = new Intent(context, TimetableActivity.class);
			intent.putExtra(Extras.URI, timetableUri);
			intent.putExtra(Extras.ROUTE_NUMBER, routeNumber);
			intent.putExtra(Extras.STATION_NAME, stationName);
			intent.putExtra(Extras.STATION_DIRECTION, stationDirection);

			return intent;
		}
	}

	private Intents() {
	}

	public static Builder getBuilder(Context context) {
		return new Builder(context);
	}
}
