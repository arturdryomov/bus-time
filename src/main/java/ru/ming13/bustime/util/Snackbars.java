package ru.ming13.bustime.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;

import ru.ming13.bustime.R;

public final class Snackbars
{
	private Snackbars() {
	}

	public static void show(@NonNull Activity activity, @StringRes int text, @StringRes int actionText, @NonNull View.OnClickListener actionListener) {
		Snackbar snackbar = Snackbar.make(
			activity.findViewById(android.R.id.content),
			text,
			Snackbar.LENGTH_INDEFINITE
		);

		snackbar.setAction(actionText, actionListener);

		snackbar.show();
	}

	public static void showFullscreen(@NonNull Activity activity, @StringRes int text, @StringRes int actionText, @NonNull View.OnClickListener actionListener) {
		Snackbar snackbar = Snackbar.make(
			activity.findViewById(android.R.id.content),
			text,
			Snackbar.LENGTH_INDEFINITE
		);

		snackbar.setActionTextColor(ContextCompat.getColor(activity, android.R.color.white));
		snackbar.setAction(actionText, actionListener);

		FrameLayout.LayoutParams snackbarParams = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
		snackbarParams.setMargins(0, 0, 0, Bartender.at(activity).getBottomUiPadding());
		snackbar.getView().setLayoutParams(snackbarParams);

		snackbar.getView().setBackgroundResource(R.color.background_transparent);

		snackbar.show();
	}
}
