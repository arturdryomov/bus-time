package ru.ming13.bustime.util;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ViewAnimator;

public final class ViewDirector
{
	private final Activity activity;
	private final Fragment fragment;

	@IdRes
	private final int animatorId;

	public static ViewDirector of(@NonNull Activity activity, @IdRes int animatorId) {
		return new ViewDirector(activity, animatorId);
	}

	public static ViewDirector of(@NonNull Fragment fragment, @IdRes int animatorId) {
		return new ViewDirector(fragment, animatorId);
	}

	private ViewDirector(@NonNull Activity activity, @IdRes int animatorId) {
		this.activity = activity;
		this.fragment = null;

		this.animatorId = animatorId;
	}

	private ViewDirector(@NonNull Fragment fragment, @IdRes int animatorId) {
		this.activity = null;
		this.fragment = fragment;

		this.animatorId = animatorId;
	}

	public void show(@IdRes int viewId) {
		ViewAnimator animator = (ViewAnimator) findView(animatorId);
		View view = findView(viewId);

		if (animator.getDisplayedChild() != animator.indexOfChild(view)) {
			animator.setDisplayedChild(animator.indexOfChild(view));
		}
	}

	private View findView(@IdRes int viewId) {
		if (activity != null) {
			return activity.findViewById(viewId);
		} else {
			return fragment.getView().findViewById(viewId);
		}
	}
}