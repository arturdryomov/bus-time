package ru.ming13.bustime.util;

import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import ru.ming13.bustime.R;

public final class Frames
{
	private final FragmentActivity activity;

	public static Frames at(FragmentActivity activity) {
		return new Frames(activity);
	}

	private Frames(FragmentActivity activity) {
		this.activity = activity;
	}

	public boolean areAvailable() {
		return Android.isTablet(activity) && !Android.isPortrait(activity);
	}

	public void setLeftFrameTitle(String title) {
		setFrameTitle(title, R.id.text_left_frame);
	}

	private void setFrameTitle(String title, int titleViewId) {
		TextView titleView = (TextView) activity.findViewById(titleViewId);
		titleView.setText(title);
	}

	public void setRightFrameTitle(String title) {
		setFrameTitle(title, R.id.text_right_frame);
	}
}
