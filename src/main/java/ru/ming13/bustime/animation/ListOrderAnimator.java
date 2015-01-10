package ru.ming13.bustime.animation;

import android.support.annotation.NonNull;
import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

public final class ListOrderAnimator
{
	private static final class Durations
	{
		private Durations() {
		}

		public static final int ANIMATION_IN_MILLIS = 300;
	}

	private final Interpolator animationInterpolator;

	private final ListView list;
	private final SparseIntArray savedListItemPositionsFromTop;

	public ListOrderAnimator(@NonNull ListView list) {
		this.animationInterpolator = new DecelerateInterpolator();

		this.list = list;
		this.savedListItemPositionsFromTop = new SparseIntArray(list.getCount());
	}

	public void saveListState() {
		for (int listItemPosition = 0; listItemPosition < list.getCount(); listItemPosition++) {
			int listItemId = getListItemId(listItemPosition);
			int listItemPositionFromTop = getListItemPositionFromTop(listItemPosition);

			saveListItemPositionFromTop(listItemId, listItemPositionFromTop);
		}
	}

	private int getListItemId(int listItemPosition) {
		return (int) list.getAdapter().getItemId(listItemPosition);
	}

	private int getListItemPositionFromTop(int listItemPosition) {
		int firstVisibleListItemPosition = list.getFirstVisiblePosition();
		int lastVisibleListItemPosition = list.getLastVisiblePosition();

		if (listItemPosition < firstVisibleListItemPosition) {
			return list.getTop() - list.getHeight() / 2;
		}

		if (listItemPosition > lastVisibleListItemPosition) {
			return list.getBottom() + list.getHeight() / 2;
		}

		return list.getChildAt(listItemPosition - firstVisibleListItemPosition).getTop();
	}

	private void saveListItemPositionFromTop(int listItemId, int listItemPosition) {
		savedListItemPositionsFromTop.put(listItemId, listItemPosition);
	}

	public void animateReorderedListState() {
		for (int listItemVisiblePosition = 0; listItemVisiblePosition < list.getChildCount(); listItemVisiblePosition++) {
			int listItemId = getListItemId(getListItemPosition(listItemVisiblePosition));

			if (!isListItemPositionSaved(listItemId)) {
				continue;
			}

			animateListItem(listItemVisiblePosition, listItemId);
		}
	}

	private int getListItemPosition(int listItemVisiblePosition) {
		return list.getFirstVisiblePosition() + listItemVisiblePosition;
	}

	private boolean isListItemPositionSaved(int listItemId) {
		return savedListItemPositionsFromTop.indexOfKey(listItemId) >= 0;
	}

	private void animateListItem(int listItemVisiblePosition, int listItemId) {
		View listItemView = list.getChildAt(listItemVisiblePosition);

		int savedListItemPositionFromTop = loadListItemPositionFromTop(listItemId);
		int currentListItemPositionFromTop = listItemView.getTop();
		int listItemPositionFromTopDelta = currentListItemPositionFromTop - savedListItemPositionFromTop;

		Animation animation = new TranslateAnimation(0, 0, -listItemPositionFromTopDelta, 0);
		animation.setInterpolator(animationInterpolator);
		animation.setDuration(Durations.ANIMATION_IN_MILLIS);

		listItemView.startAnimation(animation);
	}

	private int loadListItemPositionFromTop(int listItemId) {
		return savedListItemPositionsFromTop.get(listItemId);
	}
}
