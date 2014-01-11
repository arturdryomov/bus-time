package ru.ming13.bustime.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;

import ru.ming13.bustime.R;

public final class Bartender
{
	private static final class SystemDimensions
	{
		private SystemDimensions() {
		}

		public static final int DEFAULT_VALUE = 0;
		public static final int INVALID_ID = 0;

		public static final String STATUS_BAR_HEIGHT = "status_bar_height";

		public static final String NAVIGATION_BAR_HEIGHT_PORTRAIT = "navigation_bar_height";
		public static final String NAVIGATION_BAR_HEIGHT_LANDSCAPE = "navigation_bar_height_landscape";
		public static final String NAVIGATION_BAR_WIDTH = "navigation_bar_width";
	}

	private final Context context;

	public static Bartender with(Context context) {
		return new Bartender(context);
	}

	private Bartender(Context context) {
		this.context = context;
	}

	public void showBarsBackground(Activity activity) {
		ViewGroup activityView = (ViewGroup) activity.getWindow().getDecorView();

		activityView.addView(buildBarView(buildStatusBarViewParams()));
		activityView.addView(buildBarView(buildNavigationBarViewParams()));
	}

	private View buildBarView(LayoutParams barViewParams) {
		View barView = new View(context);
		barView.setLayoutParams(barViewParams);
		barView.setBackgroundResource(R.color.background_bar_transparent);

		return barView;
	}

	private LayoutParams buildStatusBarViewParams() {
		LayoutParams statusBarViewParams = new LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());

		statusBarViewParams.gravity = Gravity.TOP;

		if (!isNavigationBarBottom()) {
			statusBarViewParams.rightMargin = getNavigationBarWidth();
		}

		return statusBarViewParams;
	}

	private int getStatusBarHeight() {
		return getSystemDimension(SystemDimensions.STATUS_BAR_HEIGHT);
	}

	private int getSystemDimension(String systemDimension) {
		int systemDimensionId = context.getResources().getIdentifier(systemDimension, "dimen", "android");

		if (systemDimensionId == SystemDimensions.INVALID_ID) {
			return SystemDimensions.DEFAULT_VALUE;
		}

		return context.getResources().getDimensionPixelSize(systemDimensionId);
	}

	private boolean isNavigationBarBottom() {
		return Android.isTablet(context) || Android.isPortrait(context);
	}

	private int getNavigationBarWidth() {
		if (!isNavigationBarAvailable()) {
			return SystemDimensions.DEFAULT_VALUE;
		}

		return getSystemDimension(SystemDimensions.NAVIGATION_BAR_WIDTH);
	}

	private boolean isNavigationBarAvailable() {
		return Android.isIceCreamSandwitch() && !Android.hasMenuKey(context) && !Android.hasBackKey();
	}

	private LayoutParams buildNavigationBarViewParams() {
		LayoutParams navigationBarViewParams;

		if (isNavigationBarBottom()) {
			navigationBarViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, getNavigationBarHeight());
			navigationBarViewParams.gravity = Gravity.BOTTOM;
		} else {
			navigationBarViewParams = new LayoutParams(getNavigationBarWidth(), LayoutParams.MATCH_PARENT);
			navigationBarViewParams.gravity = Gravity.RIGHT;
		}

		return navigationBarViewParams;
	}

	private int getNavigationBarHeight() {
		if (!isNavigationBarAvailable()) {
			return SystemDimensions.DEFAULT_VALUE;
		}

		if (Android.isPortrait(context)) {
			return getSystemDimension(SystemDimensions.NAVIGATION_BAR_HEIGHT_PORTRAIT);
		} else {
			return getSystemDimension(SystemDimensions.NAVIGATION_BAR_HEIGHT_LANDSCAPE);
		}
	}

	public int getBottomUiPadding() {
		if (isNavigationBarBottom()) {
			return getNavigationBarHeight();
		} else {
			return SystemDimensions.DEFAULT_VALUE;
		}
	}

	public int getLeftUiPadding() {
		return SystemDimensions.DEFAULT_VALUE;
	}

	public int getRightUiPadding() {
		if (isNavigationBarBottom()) {
			return SystemDimensions.DEFAULT_VALUE;
		} else {
			return getNavigationBarWidth();
		}
	}

	public int getTopUiPadding() {
		return getStatusBarHeight() + getActionBarHeight();
	}

	private int getActionBarHeight() {
		return context.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_default_height);
	}
}
