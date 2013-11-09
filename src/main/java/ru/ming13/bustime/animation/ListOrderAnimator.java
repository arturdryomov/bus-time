package ru.ming13.bustime.animation;

import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

public final class ListOrderAnimator
{
	private static final int ANIMATION_DURATION_IN_MILLIS = 300;

	private final ListView list;

	private final Interpolator interpolator;
	private final SparseIntArray savedListItemPositions;

	public ListOrderAnimator(ListView list) {
		this.list = list;

		this.interpolator = new DecelerateInterpolator();
		this.savedListItemPositions = new SparseIntArray(list.getCount());
	}

	public void saveListState() {
		for (int itemIndex = 0; itemIndex < list.getCount(); itemIndex++) {
			int itemId = getListItemId(itemIndex);
			int itemPositionFromTop = getListItemPositionFromTop(itemIndex);

			saveListItemPosition(itemId, itemPositionFromTop);
		}
	}

	private int getListItemId(int itemIndex) {
		return (int) list.getAdapter().getItemId(itemIndex);
	}

	private int getListItemPositionFromTop(int itemIndex) {
		int firstVisibleItemIndex = list.getFirstVisiblePosition();
		int lastVisibleItemIndex = list.getLastVisiblePosition();

		if (itemIndex < firstVisibleItemIndex) {
			return list.getTop() - list.getHeight() / 2;
		}

		if (itemIndex > lastVisibleItemIndex) {
			return list.getBottom() + list.getHeight() / 2;
		}

		return list.getChildAt(itemIndex - firstVisibleItemIndex).getTop();
	}

	private void saveListItemPosition(int itemId, int itemPosition) {
		savedListItemPositions.put(itemId, itemPosition);
	}

	public void animateReorderedListState() {
		for (int itemVisibleIndex = 0; itemVisibleIndex < list.getChildCount(); itemVisibleIndex++) {
			int itemId = getListItemId(getListItemIndex(itemVisibleIndex));

			if (!isListItemPositionSaved(itemId)) {
				continue;
			}

			animateListItem(itemVisibleIndex, itemId);
		}
	}

	private int getListItemIndex(int itemVisibleIndex) {
		return list.getFirstVisiblePosition() + itemVisibleIndex;
	}

	private boolean isListItemPositionSaved(int itemId) {
		return savedListItemPositions.indexOfKey(itemId) >= 0;
	}

	private void animateListItem(int itemVisibleIndex, int itemId) {
		View itemView = list.getChildAt(itemVisibleIndex);

		int previousItemPositionFromTop = savedListItemPositions.get(itemId);
		int currentItemPositionFromTop = itemView.getTop();
		int itemPositionDelta = currentItemPositionFromTop - previousItemPositionFromTop;

		Animation animation = new TranslateAnimation(0, 0, -itemPositionDelta, 0);
		animation.setInterpolator(interpolator);
		animation.setDuration(ANIMATION_DURATION_IN_MILLIS);

		itemView.startAnimation(animation);
	}
}
