package ru.ming13.bustime.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import ru.ming13.bustime.R;
import ru.ming13.bustime.activity.RouteMapActivity;
import ru.ming13.bustime.activity.RouteStopsActivity;
import ru.ming13.bustime.activity.StopMapActivity;
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
	}

	private static final class UriMasks
	{
		private UriMasks() {
		}

		public static final String EMAIL = "mailto:%s?subject=%s";

		public static final String GOOGLE_PLAY_APP = "market://details?id=%s";
		public static final String GOOGLE_PLAY_WEB = "https://play.google.com/store/apps/details?id=%s";
	}

	public static final class Builder
	{
		private final Context context;

		public static Builder with(@NonNull Context context) {
			return new Builder(context);
		}

		private Builder(Context context) {
			this.context = context.getApplicationContext();
		}

		public Intent buildStopsMapIntent() {
			return new Intent(context, StopsMapActivity.class);
		}

		public Intent buildRouteMapIntent(@NonNull Route route) {
			Intent intent = new Intent(context, RouteMapActivity.class);
			intent.putExtra(Extras.ROUTE, route);

			return intent;
		}

		public Intent buildStopMapIntent(@NonNull Stop stop) {
			Intent intent = new Intent(context, StopMapActivity.class);
			intent.putExtra(Extras.STOP, stop);

			return intent;
		}

		public Intent buildRouteStopsIntent(@NonNull Route route) {
			Intent intent = new Intent(context, RouteStopsActivity.class);
			intent.putExtra(Extras.ROUTE, route);

			return intent;
		}

		public Intent buildStopRoutesIntent(@NonNull Stop stop) {
			Intent intent = new Intent(context, StopRoutesActivity.class);
			intent.putExtra(Extras.STOP, stop);

			return intent;
		}

		public Intent buildTimetableIntent(@NonNull Route route, @NonNull Stop stop) {
			Intent intent = new Intent(context, TimetableActivity.class);
			intent.putExtra(Extras.ROUTE, route);
			intent.putExtra(Extras.STOP, stop);

			return intent;
		}

		public Intent buildFeedbackIntent() {
			String feedbackAddress = context.getString(R.string.email_feedback_address);
			String feedbackSubject = context.getString(R.string.email_feedback_subject);

			String feedbackUri = String.format(UriMasks.EMAIL, feedbackAddress, feedbackSubject);

			return new Intent(Intent.ACTION_SENDTO, Uri.parse(feedbackUri));
		}

		public Intent buildGooglePlayAppIntent() {
			String googlePlayUri = String.format(UriMasks.GOOGLE_PLAY_APP, Android.getApplicationId());

			return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
		}

		public Intent buildGooglePlayWebIntent() {
			String googlePlayUri = String.format(UriMasks.GOOGLE_PLAY_WEB, Android.getApplicationId());

			return new Intent(Intent.ACTION_VIEW, Uri.parse(googlePlayUri));
		}
	}
}
