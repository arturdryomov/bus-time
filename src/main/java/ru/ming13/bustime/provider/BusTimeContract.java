package ru.ming13.bustime.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.util.Android;
import ru.ming13.bustime.util.Time;

public final class BusTimeContract
{
	private BusTimeContract() {
	}

	@NonNull
	public static final String AUTHORITY = Android.getApplicationId();

	private interface RoutesColumns
	{
		String NUMBER = DatabaseSchema.RoutesColumns.NUMBER;
		String DESCRIPTION = DatabaseSchema.RoutesColumns.DESCRIPTION;
	}

	public static final class Routes implements BaseColumns, RoutesColumns
	{
		private Routes() {
		}

		@NonNull
		public static Uri getRoutesUri() {
			return BusTimeContract.buildContentUri(getPathsBuilder().buildRoutesPath());
		}

		@NonNull
		public static Uri getRoutesUri(long stopId) {
			return BusTimeContract.buildContentUri(getPathsBuilder().buildStopRoutesPath(String.valueOf(stopId)));
		}

		public static long getRouteId(Uri routeStopsUri) {
			final int routeIdSegmentPosition = 1;

			return BusTimeContract.parseId(routeStopsUri, routeIdSegmentPosition);
		}
	}

	private interface StopsColumns
	{
		String NAME = DatabaseSchema.StopsColumns.NAME;
		String DIRECTION = DatabaseSchema.StopsColumns.DIRECTION;
		String LATITUDE = DatabaseSchema.StopsColumns.LATITUDE;
		String LONGITUDE = DatabaseSchema.StopsColumns.LONGITUDE;
	}

	public static final class Stops implements BaseColumns, StopsColumns
	{
		private Stops() {
		}

		@NonNull
		public static Uri getStopsUri() {
			return BusTimeContract.buildContentUri(getPathsBuilder().buildStopsPath());
		}

		@NonNull
		public static Uri getStopsUri(long routeId) {
			return BusTimeContract.buildContentUri(getPathsBuilder().buildRouteStopsPath(String.valueOf(routeId)));
		}

		public static long getStopId(Uri stopRoutesUri) {
			final int stopIdSegmentPosition = 1;

			return BusTimeContract.parseId(stopRoutesUri, stopIdSegmentPosition);
		}

		public static long getStopsSearchId(Uri stopsSearchUri) {
			return BusTimeContract.parseId(stopsSearchUri);
		}

		@NonNull
		public static String getStopsSearchQuery(Uri stopsSearchUri) {
			return stopsSearchUri.getLastPathSegment();
		}
	}

	private interface TimetableColumns
	{
		String ARRIVAL_TIME = "arrival_time";
		String TYPE = "type";
	}

	public static final class Timetable implements BaseColumns, TimetableColumns
	{
		private Timetable() {
		}

		public static final class Type
		{
			private Type() {
			}

			public static final int FULL_WEEK = DatabaseSchema.TripTypesColumnsValues.FULL_WEEK_ID;
			public static final int WORKDAYS = DatabaseSchema.TripTypesColumnsValues.WORKDAY_ID;
			public static final int WEEKEND = DatabaseSchema.TripTypesColumnsValues.WEEKEND_ID;

			public static int currentWeekPartDependent() {
				if (Time.current().isWeekend()) {
					return WEEKEND;
				} else {
					return WORKDAYS;
				}
			}

			public static boolean isWeekPartDependent(int type) {
				return type != FULL_WEEK;
			}
		}

		@NonNull
		public static Uri getTimetableUri(long routeId, long stopId) {
			return BusTimeContract.buildContentUri(getPathsBuilder().buildTimetablePath(String.valueOf(routeId), String.valueOf(stopId)));
		}

		@NonNull
		public static Uri getTimetableUri(Uri timetableUri, long timetableTypeId) {
			return BusTimeContract.buildContentUri(timetableUri, Timetable.TYPE, String.valueOf(timetableTypeId));
		}

		public static long getRouteId(Uri timetableUri) {
			final int routeIdSegmentPosition = 1;

			return BusTimeContract.parseId(timetableUri, routeIdSegmentPosition);
		}

		public static long getStopId(Uri timetableUri) {
			final int stopIdSegmentPosition = 3;

			return BusTimeContract.parseId(timetableUri, stopIdSegmentPosition);
		}

		public static int getTimetableType(Uri timetableUri) {
			return BusTimeContract.parseParameter(timetableUri, Timetable.TYPE);
		}
	}

	private static Uri buildContentUri() {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build();
	}

	private static Uri buildContentUri(String path) {
		return Uri.withAppendedPath(buildContentUri(), path);
	}

	private static Uri buildContentUri(Uri uri, String key, String value) {
		return uri.buildUpon().appendQueryParameter(key, value).build();
	}

	private static long parseId(Uri uri) {
		return ContentUris.parseId(uri);
	}

	private static long parseId(Uri uri, int segmentPosition) {
		return Long.valueOf(uri.getPathSegments().get(segmentPosition));
	}

	private static int parseParameter(Uri uri, String key) {
		return Integer.valueOf(uri.getQueryParameter(key));
	}

	private static BusTimePathsBuilder getPathsBuilder() {
		return new BusTimePathsBuilder();
	}
}
