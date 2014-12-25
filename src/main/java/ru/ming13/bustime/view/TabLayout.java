package ru.ming13.bustime.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.ming13.bustime.R;
import ru.ming13.bustime.util.Android;
import ru.ming13.bustime.util.Views;

public class TabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener, View.OnClickListener
{
	private static final class Dimensions
	{
		private Dimensions() {
		}

		public static final int TAB_OFFSET_IN_DP = 24;

		public static final int TAB_TEXT_IN_SP = 14;
	}

	private static final class Colors
	{
		private Colors() {
		}

		@ColorRes
		public static final int TAB_TEXT = android.R.color.white;
	}

	private ViewPager tabPager;
	private final TabStrip tabStrip;

	private final float tabOffset;
	private int tabScrollState;

	public TabLayout(Context context, int colorRes) {
		this(context, null);
	}

	public TabLayout(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	public TabLayout(Context context, AttributeSet attributeSet, int style) {
		super(context, attributeSet, style);

		this.tabStrip = getTabStrip(context);

		this.tabOffset = getTabOffset(context);

		setUpView();
	}

	private TabStrip getTabStrip(Context context) {
		return new TabStrip(context);
	}

	private float getTabOffset(Context context) {
		return Views.getPixels(context, Dimensions.TAB_OFFSET_IN_DP);
	}

	private void setUpView() {
		setFillViewport(true);
		setHorizontalScrollBarEnabled(false);

		addView(tabStrip, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}

	public void setUpTabPager(@NonNull Context context, @NonNull ViewPager tabPager) {
		this.tabPager = tabPager;

		tabStrip.removeAllViews();
		tabPager.setOnPageChangeListener(this);

		setUpTabs(context);
	}

	private void setUpTabs(Context context) {
		PagerAdapter tabPagerAdapter = tabPager.getAdapter();

		for (int tabPosition = 0; tabPosition < tabPagerAdapter.getCount(); tabPosition++) {
			TextView tab = buildTab(context);

			tab.setText(tabPagerAdapter.getPageTitle(tabPosition));
			tab.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
			tab.setOnClickListener(this);

			tabStrip.addView(tab);
		}
	}

	@TargetApi(Android.TARGET_VERSION)
	private TextView buildTab(Context context) {
		TextView tab = new TextView(context);

		tab.setTextColor(getResources().getColor(Colors.TAB_TEXT));
		tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, Dimensions.TAB_TEXT_IN_SP);
		tab.setTypeface(Typeface.DEFAULT_BOLD);
		tab.setGravity(Gravity.CENTER);

		if (Android.isHoneycombOrLater()) {
			tab.setBackgroundResource(Views.getDrawableAttribute(context, R.attr.selectableItemBackground));
		}

		if (Android.isIceCreamSandwichOrLater()) {
			tab.setAllCaps(true);
		}

		return tab;
	}

	@Override
	public void onClick(View tab) {
		for (int tabPosition = 0; tabPosition < tabStrip.getChildCount(); tabPosition++) {
			if (tabStrip.getChildAt(tabPosition) == tab) {
				tabPager.setCurrentItem(tabPosition);

				return;
			}
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		if (tabPager != null) {
			changeTab(tabPager.getCurrentItem(), 0);
		}
	}

	private void changeTab(int tabPosition, int tabPositionOffset) {
		if ((tabStrip.getChildCount() == 0) || (tabPosition < 0) || (tabPosition >= tabStrip.getChildCount())) {
			return;
		}

		View tabStrip = this.tabStrip.getChildAt(tabPosition);

		if (tabStrip == null) {
			return;
		}

		int tabScrollPosition = tabStrip.getLeft() + tabPositionOffset;

		if ((tabPosition > 0) || (tabPositionOffset > 0)) {
			tabScrollPosition -= tabOffset;
		}

		scrollTo(tabScrollPosition, 0);
	}

	@Override
	public void onPageScrolled(int tabPosition, float tabPositionOffset, int tabPositionOffsetPixels) {
		if ((tabStrip.getChildCount() == 0) || (tabPosition < 0) || (tabPosition >= tabStrip.getChildCount())) {
			return;
		}

		tabStrip.changeTab(tabPosition, tabPositionOffset);

		int tabOffset = (int) (tabPositionOffset * tabStrip.getChildAt(tabPosition).getWidth());

		changeTab(tabPosition, tabOffset);
	}

	@Override
	public void onPageScrollStateChanged(int tabScrollState) {
		this.tabScrollState = tabScrollState;
	}

	@Override
	public void onPageSelected(int tabPosition) {
		if (tabScrollState == ViewPager.SCROLL_STATE_IDLE) {
			tabStrip.changeTab(tabPosition, 0);

			changeTab(tabPosition, 0);
		}
	}
}