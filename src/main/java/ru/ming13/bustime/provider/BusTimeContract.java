package ru.ming13.bustime.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import ru.ming13.bustime.database.DatabaseSchema;
import ru.ming13.bustime.util.Time;

public final class BusTimeContract
{
	private BusTimeContract() {
	}

	public static final String AUTHORITY = "ru.ming13.bustime";

	private static final Uri CONTENT_URI = buildContentUri();

	private static Uri buildContentUri() {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build();
	}

	private static Uri buildContentUri(String path) {
		return Uri.withAppendedPath(CONTENT_URI, path);
	}

	private static Uri buildContentUri(Uri uri, long id) {
		return ContentUris.withAppendedId(uri, id);
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

	private interface RoutesColumns
	{
		String NUMBER = DatabaseSchema.RoutesColumns.NUMBER;
		String DESCRIPTION = DatabaseSchema.RoutesColumns.DESCRIPTION;
	}

	public static final class Routes implements BaseColumns, RoutesColumns
	{
		private Routes() {
		}

		public static Uri buildRoutesUri() {
			return buildContentUri(getPathsBuilder().buildRoutesPath());
		}

		public static Uri buildRouteStopsUri(long routeId) {
			return buildContentUri(getPathsBuilder().buildRouteStopsPath(String.valueOf(routeId)));
		}

		public static Uri buildRouteTimetableUri(Uri routeStopsUri, long stopId) {
			return buildContentUri(routeStopsUri, stopId);
		}

		public static long getStopsRouteId(Uri routeStopsUri) {
			final int routeIdSegmentPosition = 1;

			return parseId(routeStopsUri, routeIdSegmentPosition);
		}

		public static long getTimetableRouteId(Uri routeTimetableUri) {
			final int routeIdSegmentPosition = 1;

			return parseId(routeTimetableUri, routeIdSegmentPosition);
		}

		public static long getTimetableStopId(Uri routeTimetableUri) {
			final int stopIdSegmentPosition = 3;

			return parseId(routeTimetableUri, stopIdSegmentPosition);
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

		public static Uri buildStopsUri() {
			return buildContentUri(getPathsBuilder().buildStopsPath());
		}

		public static Uri buildStopsRoutesUri(long stopId) {
			return buildContentUri(getPathsBuilder().buildStopRoutesPath(String.valueOf(stopId)));
		}

		public static Uri buildStopTimetableUri(Uri stopRoutesUri, long routeId) {
			return buildContentUri(stopRoutesUri, routeId);
		}

		public static long getRoutesStopId(Uri stopRoutesUri) {
			final int stopIdSegmentPosition = 1;

			return parseId(stopRoutesUri, stopIdSegmentPosition);
		}

		public static long getTimetableRouteId(Uri stopTimetableUri) {
			final int routeIdSegmentPosition = 3;

			return parseId(stopTimetableUri, routeIdSegmentPosition);
		}

		public static long getTimetableStopId(Uri stopTimetableUri) {
			final int stopIdSegmentIndex = 1;

			return parseId(stopTimetableUri, stopIdSegmentIndex);
		}

		public static long getSearchStopId(Uri stopUri) {
			return parseId(stopUri);
		}

		public static String getSearchStopsQuery(Uri stopsSearchUri) {
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

			public static long currentWeekPartDependent() {
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

		public static Uri buildTimetableUri(Uri timetableUri, int type) {
			return buildContentUri(timetableUri, Timetable.TYPE, String.valueOf(type));
		}

		public static int getTimetableType(Uri timetableUri) {
			return parseParameter(timetableUri, Timetable.TYPE);
		}
	}
}
