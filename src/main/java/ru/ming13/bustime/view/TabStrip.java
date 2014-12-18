package ru.ming13.bustime.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import ru.ming13.bustime.util.Views;

class TabStrip extends LinearLayout
{
	private static final class Dimensions
	{
		private Dimensions() {
		}

		public static final int TAB_INDICATOR_HEIGHT_IN_DP = 2;
	}

	private static final class Colors
	{
		private Colors() {
		}

		@ColorRes
		public static final int TAB_INDICATOR = android.R.color.white;
	}

	private final Paint tabIndicatorPaint;
	private final float tabIndicatorHeight;

	private int tabPosition;
	private float tabOffset;

	public TabStrip(Context context) {
		this(context, null);
	}

	public TabStrip(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		this.tabIndicatorPaint = getTabIndicatorPaint();
		this.tabIndicatorHeight = getTabIndicatorHeight();

		setUpView();
	}

	private Paint getTabIndicatorPaint() {
		Paint paint = new Paint();

		paint.setColor(getResources().getColor(Colors.TAB_INDICATOR));

		return paint;
	}

	private float getTabIndicatorHeight() {
		return Views.getPixels(getResources().getDisplayMetrics(), Dimensions.TAB_INDICATOR_HEIGHT_IN_DP);
	}

	private void setUpView() {
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		View tab = getChildAt(tabPosition);

		int tabHeight = getHeight();

		int tabLeft = tab.getLeft();
		int tabRight = tab.getRight();

		if ((tabOffset > 0) && (tabPosition < (getChildCount() - 1))) {
			View nextTab = getChildAt(tabPosition + 1);

			tabLeft = (int) (tabOffset * nextTab.getLeft() + (1 - tabOffset) * tabLeft);
			tabRight = (int) (tabOffset * nextTab.getRight() + (1 - tabOffset) * tabRight);
		}

		canvas.drawRect(tabLeft, tabHeight - tabIndicatorHeight, tabRight, tabHeight, tabIndicatorPaint);
	}

	void changeTab(int tabPosition, float tabPositionOffset) {
		this.tabPosition = tabPosition;
		this.tabOffset = tabPositionOffset;

		invalidate();
	}
}