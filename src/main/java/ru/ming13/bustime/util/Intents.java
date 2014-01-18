package ru.ming13.bustime.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ru.ming13.bustime.R;
import ru.ming13.bustime.activity.RouteStationsActivity;
import ru.ming13.bustime.activity.StationRoutesActivity;
import ru.ming13.bustime.activity.StationsMapActivity;
import ru.ming13.bustime.activity.TimetableActivity;

public final class Intents
{
	private Intents() {
	}

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

		public static Builder with(Context context) {
			return new Builder(context);
		}

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

		public Intent buildStationsMapIntent() {
			return new Intent(context, StationsMapActivity.class);
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

		public Intent buildGooglePlayAppIntent() {
			String packageName = context.getPackageName();
			String googlePlayUri = context.getString(R.string.uri_app_google_play, packageName);

			return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
		}

		public Intent buildGooglePlayWebIntent() {
			String packageName = context.getPackageName();
			String googlePlayUri = context.getString(R.string.uri_web_google_play, packageName);

			return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
		}

		public Intent buildFeedbackIntent() {
			String feedbackAddress = context.getString(R.string.email_feedback_address);
			String feedbackSubject = context.getString(R.string.email_feedback_subject);

			String feedbackUri = String.format("mailto:%s?subject=%s", feedbackAddress, feedbackSubject);

			return new Intent(Intent.ACTION_SENDTO, Uri.parse(feedbackUri));
		}
	}
}
