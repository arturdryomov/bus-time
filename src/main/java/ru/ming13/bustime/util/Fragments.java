package ru.ming13.bustime.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public final class Fragments
{
	private Fragments() {
	}

	public static final class Arguments
	{
		private Arguments() {
		}

		public static final String ROUTE = "route";
		public static final String STOP = "stop";

		public static final String MESSAGE = "message";

		public static final String REQUEST_CODE = "request_code";
		public static final String ERROR_CODE = "error_code";
	}

	public static final class States
	{
		private States() {
		}

		public static final String CAMERA_POSITION = "camera_position";
		public static final String TIMETABLE_TYPE = "timetable_type";
	}

	public static final class Operator
	{
		private Operator() {
		}

		public static void set(FragmentActivity activity, Fragment fragment) {
			if (isSet(activity)) {
				return;
			}

			activity.getSupportFragmentManager()
				.beginTransaction()
				.add(android.R.id.content, fragment)
				.commit();
		}

		public static void set(FragmentActivity activity, Fragment fragment, int container) {
			if (isSet(activity, container)) {
				return;
			}

			activity.getSupportFragmentManager()
				.beginTransaction()
				.add(container, fragment)
				.commit();
		}

		public static void reset(FragmentActivity activity, Fragment fragment, int container) {
			activity.getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
				.replace(container, fragment)
				.commit();
		}

		private static boolean isSet(FragmentActivity activity) {
			return activity.getSupportFragmentManager().findFragmentById(android.R.id.content) != null;
		}

		private static boolean isSet(FragmentActivity activity, int container) {
			return activity.getSupportFragmentManager().findFragmentById(container) != null;
		}

		public static Fragment get(FragmentActivity activity, String fragmentTag) {
			return activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
		}
	}
}
