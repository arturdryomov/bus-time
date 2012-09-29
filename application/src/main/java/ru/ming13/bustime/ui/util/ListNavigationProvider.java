package ru.ming13.bustime.ui.util;


import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import ru.ming13.bustime.R;


public class ListNavigationProvider
{
	private static final class SavedInstanceKeys
	{
		private SavedInstanceKeys() {
		}

		public static final String SELECTED_LIST_NAVIGATION_ITEM_INDEX = "list_navigation_item_index";
	}

	private static final int DEFAULT_LIST_NAVIGATION_ITEM_INDEX = 0;

	private SherlockFragmentActivity activity;

	public ListNavigationProvider(SherlockFragmentActivity activity) {
		this.activity = activity;
	}

	public void setUpListNavigation(ActionBar.OnNavigationListener navigationListener, int navigationItemsResourceId) {
		ActionBar actionBar = activity.getSupportActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		actionBar.setListNavigationCallbacks(buildListNavigationAdapter(navigationItemsResourceId),
			navigationListener);
	}

	private ArrayAdapter<CharSequence> buildListNavigationAdapter(int navigationItemsResourceId) {
		Context themedContext = activity.getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> listNavigationAdapter = ArrayAdapter.createFromResource(
			themedContext, navigationItemsResourceId, R.layout.sherlock_spinner_item);

		listNavigationAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		return listNavigationAdapter;
	}

	public void saveSelectedNavigationIndex(Bundle activityOutState) {
		int selectedNavigationItemIndex = getSelectedNavigationItemIndex();

		activityOutState.putInt(SavedInstanceKeys.SELECTED_LIST_NAVIGATION_ITEM_INDEX,
			selectedNavigationItemIndex);
	}

	private int getSelectedNavigationItemIndex() {
		return activity.getSupportActionBar().getSelectedNavigationIndex();
	}

	public void restoreSelectedNavigationIndex(Bundle activityInState) {
		int selectedNavigationItemIndex = getSelectedNavigationItemIndex(activityInState);

		activity.getSupportActionBar().setSelectedNavigationItem(selectedNavigationItemIndex);
	}

	private int getSelectedNavigationItemIndex(Bundle activityInState) {
		if (!isStateValid(activityInState)) {
			return DEFAULT_LIST_NAVIGATION_ITEM_INDEX;
		}

		return activityInState.getInt(SavedInstanceKeys.SELECTED_LIST_NAVIGATION_ITEM_INDEX);
	}

	public boolean isStateValid(Bundle state) {
		if (state == null) {
			return false;
		}

		if (!state.containsKey(SavedInstanceKeys.SELECTED_LIST_NAVIGATION_ITEM_INDEX)) {
			return false;
		}

		return true;
	}
}
