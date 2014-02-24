package ru.ming13.bustime.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

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
		private final FragmentActivity activity;

		public static Operator at(FragmentActivity activity) {
			return new Operator(activity);
		}

		private Operator(FragmentActivity activity) {
			this.activity = activity;
		}

		public Fragment get(int fragmentContainerId) {
			return getFragmentManager().findFragmentById(fragmentContainerId);
		}

		private FragmentManager getFragmentManager() {
			return activity.getSupportFragmentManager();
		}

		public Fragment get(String fragmentTag) {
			return getFragmentManager().findFragmentByTag(fragmentTag);
		}

		public void set(Fragment fragment, int fragmentContainerId) {
			if (!isSet(fragmentContainerId)) {
				getFragmentManager()
					.beginTransaction()
					.add(fragmentContainerId, fragment)
					.commit();
			}
		}

		private boolean isSet(int fragmentContainerId) {
			return getFragmentManager().findFragmentById(fragmentContainerId) != null;
		}


		public void resetFading(Fragment fragment, int fragmentContainerId) {
			getFragmentManager()
				.beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
				.replace(fragmentContainerId, fragment)
				.commit();
		}

		public void resetSliding(Fragment fragment, int fragmentContainerId) {
			getFragmentManager()
				.beginTransaction()
				.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
				.replace(fragmentContainerId, fragment)
				.commit();
		}
	}
}
