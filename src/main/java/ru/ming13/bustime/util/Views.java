package ru.ming13.bustime.util;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;

public class Views
{
	private Views() {
	}

	@DrawableRes
	public static int getDrawableAttribute(@NonNull Context context, @AttrRes int attribute) {
		TypedValue attributeValue = new TypedValue();

		context.getTheme().resolveAttribute(attribute, attributeValue, true);

		return attributeValue.resourceId;
	}

	public static float getPixels(@NonNull Context context, float densityIndependentPixels) {
		return TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_DIP, densityIndependentPixels, context.getResources().getDisplayMetrics());
	}
}
