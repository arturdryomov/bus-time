package ru.ming13.bustime.util;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Views
{
	private Views() {
	}

	@DrawableRes
	public static int getDrawableAttribute(Context context, @AttrRes int attribute) {
		TypedValue attributeValue = new TypedValue();

		context.getTheme().resolveAttribute(attribute, attributeValue, true);

		return attributeValue.resourceId;
	}

	public static float getPixels(DisplayMetrics displayMetrics, float densityIndependentPixels) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, densityIndependentPixels, displayMetrics);
	}
}
