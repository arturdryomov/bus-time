package ru.ming13.bustime.util;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.ming13.bustime.R;

public final class Frames
{
	private final Activity activity;

	public static Frames at(@NonNull Activity activity) {
		return new Frames(activity);
	}

	private Frames(Activity activity) {
		this.activity = activity;
	}

	public boolean areAvailable() {
		return Android.isTablet(activity) && !Android.isPortrait(activity);
	}

	public void setLeftFrameTitle(@StringRes int title) {
		setFrameTitle(title, R.id.text_left_frame);
	}

	public void setRightFrameTitle(@StringRes int title) {
		setFrameTitle(title, R.id.text_right_frame);
	}

	private void setFrameTitle(@StringRes int title, @IdRes int titleViewId) {
		TextView titleView = ButterKnife.findById(activity, titleViewId);

		titleView.setText(title);
	}
}
