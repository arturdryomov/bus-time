package ru.ming13.bustime.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


import ru.ming13.bustime.database.DatabaseSchema;

public final class BusTimeContract
{
	public static final String AUTHORITY = "ru.ming13.bustime";

	private static final Uri CONTENT_URI;

	static {
		CONTENT_URI = new Uri.Builder()
			.scheme(ContentResolver.SCHEME_CONTENT)
			.authority(AUTHORITY)
			.build();
	}

	private BusTimeContract() {
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
			return CONTENT_URI.buildUpon().appendPath(BusTimeProviderPaths.ROUTES).build();
		}

		public static Uri buildRouteStationsUri(long routeId) {
			return ContentUris.withAppendedId(buildRoutesUri(), routeId)
				.buildUpon()
				.appendPath(BusTimeProviderPaths.STATIONS)
				.build();
		}

		public static Uri buildRouteTimetableUri(Uri routeStationsUri, long stationId) {
			return ContentUris.withAppendedId(routeStationsUri, stationId);
		}

		public static long getRouteId(Uri routeStationsUri) {
			final int routeIdSegmentIndex = 1;

			return Long.parseLong(routeStationsUri.getPathSegments().get(routeIdSegmentIndex));
		}

		public static long getTimetableRouteId(Uri routeTimetableUri) {
			final int routeIdSegmentIndex = 1;

			return Long.parseLong(routeTimetableUri.getPathSegments().get(routeIdSegmentIndex));
		}

		public static long getTimetableStationId(Uri routeTimetableUri) {
			final int stationIdSegmentIndex = 3;

			return Long.parseLong(routeTimetableUri.getPathSegments().get(stationIdSegmentIndex));
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
			return CONTENT_URI.buildUpon().appendPath(BusTimeProviderPaths.STATIONS).build();
		}

		public static Uri buildStationRoutesUri(long stationId) {
			return ContentUris.withAppendedId(buildStationsUri(), stationId)
				.buildUpon()
				.appendPath(BusTimeProviderPaths.ROUTES)
				.build();
		}

		public static Uri buildStationTimetableUri(Uri stationRoutesUri, long routeId) {
			return ContentUris.withAppendedId(stationRoutesUri, routeId);
		}

		public static long getStationId(Uri stationRoutesUri) {
			final int stationIdSegmentIndex = 1;

			return Long.parseLong(stationRoutesUri.getPathSegments().get(stationIdSegmentIndex));
		}

		public static long getTimetableRouteId(Uri stationTimetableUri) {
			final int routeIdSegmentIndex = 3;

			return Long.parseLong(stationTimetableUri.getPathSegments().get(routeIdSegmentIndex));
		}

		public static long getTimetableStationId(Uri stationTimetableUri) {
			final int stationIdSegmentIndex = 1;

			return Long.parseLong(stationTimetableUri.getPathSegments().get(stationIdSegmentIndex));
		}

		public static String getStationSearchQuery(Uri stationsSearchUri) {
			return stationsSearchUri.getLastPathSegment();
		}

		public static long getStationSearchId(Uri stationUri) {
			return ContentUris.parseId(stationUri);
		}
	}

	private interface TimetableColumns
	{
		String ARRIVAL_TIME = "arrival_time";
	}

	public static final class Timetable implements TimetableColumns
	{
		private Timetable() {
		}
	}
}
