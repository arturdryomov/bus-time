package ru.ming13.bustime.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import ru.ming13.bustime.database.DatabaseSchema;

public final class BusTimeContract
{
	private BusTimeContract() {
	}

	public static final String AUTHORITY = "ru.ming13.bustime";
	public static final Uri CONTENT_URI = buildContentUri();

	private static Uri buildContentUri() {
		return new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(AUTHORITY).build();
	}

	private static Uri buildContentUri(String path) {
		return Uri.withAppendedPath(CONTENT_URI, path);
	}

	private static Uri buildContentUri(Uri uri, long id) {
		return ContentUris.withAppendedId(uri, id);
	}

	private static long parseId(Uri uri, int segmentPosition) {
		return Long.parseLong(uri.getPathSegments().get(segmentPosition));
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

		public static Uri buildRouteStationsUri(long routeId) {
			return buildContentUri(getPathsBuilder().buildRouteStationsPath(String.valueOf(routeId)));
		}

		public static Uri buildRouteTimetableUri(Uri routeStationsUri, long stationId) {
			return buildContentUri(routeStationsUri, stationId);
		}

		public static long getStationsRouteId(Uri routeStationsUri) {
			final int routeIdSegmentPosition = 1;

			return parseId(routeStationsUri, routeIdSegmentPosition);
		}

		public static long getTimetableRouteId(Uri routeTimetableUri) {
			final int routeIdSegmentPosition = 1;

			return parseId(routeTimetableUri, routeIdSegmentPosition);
		}

		public static long getTimetableStationId(Uri routeTimetableUri) {
			final int stationIdSegmentPosition = 3;

			return parseId(routeTimetableUri, stationIdSegmentPosition);
		}
	}

	private interface StationsColumns
	{
		String NAME = DatabaseSchema.StationsColumns.NAME;
		String DIRECTION = DatabaseSchema.StationsColumns.DIRECTION;
		String LATITUDE = DatabaseSchema.StationsColumns.LATITUDE;
		String LONGITUDE = DatabaseSchema.StationsColumns.LONGITUDE;
	}

	public static final class Stations implements BaseColumns, StationsColumns
	{
		private Stations() {
		}

		public static Uri buildStationsUri() {
			return buildContentUri(getPathsBuilder().buildStationsPath());
		}

		public static Uri buildStationRoutesUri(long stationId) {
			return buildContentUri(getPathsBuilder().buildStationRoutesPath(String.valueOf(stationId)));
		}

		public static Uri buildStationTimetableUri(Uri stationRoutesUri, long routeId) {
			return buildContentUri(stationRoutesUri, routeId);
		}

		public static long getRoutesStationId(Uri stationRoutesUri) {
			final int stationIdSegmentPosition = 1;

			return parseId(stationRoutesUri, stationIdSegmentPosition);
		}

		public static long getTimetableRouteId(Uri stationTimetableUri) {
			final int routeIdSegmentPosition = 3;

			return parseId(stationTimetableUri, routeIdSegmentPosition);
		}

		public static long getTimetableStationId(Uri stationTimetableUri) {
			final int stationIdSegmentIndex = 1;

			return parseId(stationTimetableUri, stationIdSegmentIndex);
		}

		public static long getSearchStationId(Uri stationUri) {
			return ContentUris.parseId(stationUri);
		}

		public static String getSearchStationQuery(Uri stationsSearchUri) {
			return stationsSearchUri.getLastPathSegment();
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

		public static final class Types
		{
			private Types() {
			}

			public static final int FULL_WEEK = DatabaseSchema.TripTypesColumnsValues.FULL_WEEK_ID;
			public static final int WORKDAYS = DatabaseSchema.TripTypesColumnsValues.WORKDAY_ID;
			public static final int WEEKEND = DatabaseSchema.TripTypesColumnsValues.WEEKEND_ID;
		}
	}
}
