package app.android.bustime.ui.fragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import app.android.bustime.R;
import com.actionbarsherlock.app.SherlockListFragment;


abstract class AdaptedListFragment extends SherlockListFragment
{
	protected static final String LIST_ITEM_OBJECT_ID = "object";

	protected final List<Map<String, Object>> list;

	public AdaptedListFragment() {
		super();

		list = new ArrayList<Map<String, Object>>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragmentView = inflateFragment(inflater, container);

		setUpListAdapter();

		return fragmentView;
	}

	protected View inflateFragment(LayoutInflater layoutInflater, ViewGroup fragmentContainer) {
		return layoutInflater.inflate(R.layout.fragment_list, fragmentContainer, false);
	}

	protected void setUpListAdapter() {
		setListAdapter(buildListAdapter());
	}

	protected abstract SimpleAdapter buildListAdapter();

	protected void populateList(List<?> listContent) {
		list.clear();

		for (Object listItemContent : listContent) {
			list.add(buildListItem(listItemContent));
		}

		refreshListContent();
	}

	protected abstract Map<String, Object> buildListItem(Object itemObject);

	protected void refreshListContent() {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();
		listAdapter.notifyDataSetChanged();
	}

	protected void setEmptyListText(String text) {
		TextView emptyListTextView = (TextView) getListView().getEmptyView();
		emptyListTextView.setText(text);
	}

	protected Object getListItemObject(int listPosition) {
		SimpleAdapter listAdapter = (SimpleAdapter) getListAdapter();

		@SuppressWarnings("unchecked")
		Map<String, Object> listItem = (Map<String, Object>) listAdapter.getItem(listPosition);

		return listItem.get(LIST_ITEM_OBJECT_ID);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (list.isEmpty()) {
			callListPopulation();
		}
	}

	protected abstract void callListPopulation();
}
