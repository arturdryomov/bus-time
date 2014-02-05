package ru.ming13.bustime.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import ru.ming13.bustime.R;
import ru.ming13.bustime.activity.RouteStopsActivity;
import ru.ming13.bustime.activity.StopRoutesActivity;
import ru.ming13.bustime.activity.StopsMapActivity;
import ru.ming13.bustime.activity.TimetableActivity;
import ru.ming13.bustime.model.Route;
import ru.ming13.bustime.model.Stop;

public final class Intents
{
	private Intents() {
	}

	public static final class Extras
	{
		private Extras() {
		}

		public static final String ROUTE = "route";
		public static final String STOP = "stop";

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

		public Intent buildStopsMapIntent() {
			return new Intent(context, StopsMapActivity.class);
		}

		public Intent buildRouteStopsIntent(Uri stopsUri, Route route) {
			Intent intent = new Intent(context, RouteStopsActivity.class);
			intent.putExtra(Extras.URI, stopsUri);
			intent.putExtra(Extras.ROUTE, route);

			return intent;
		}

		public Intent buildStopRoutesIntent(Uri routesUri, Stop stop) {
			Intent intent = new Intent(context, StopRoutesActivity.class);
			intent.putExtra(Extras.URI, routesUri);
			intent.putExtra(Extras.STOP, stop);

			return intent;
		}

		public Intent buildTimetableIntent(Uri timetableUri, Route route, Stop stop) {
			Intent intent = new Intent(context, TimetableActivity.class);
			intent.putExtra(Extras.URI, timetableUri);
			intent.putExtra(Extras.ROUTE, route);
			intent.putExtra(Extras.STOP, stop);

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
