package app.android.bustime.ui;


import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import app.android.bustime.R;
import app.android.bustime.local.DbException;
import app.android.bustime.local.Route;
import app.android.bustime.local.Time;


public class DepartureTimesListActivity extends SimpleAdapterListActivity
{
	private final Context activityContext = this;

	private Route route;

	private static final String LIST_ITEM_TEXT_ID = "text";
	private static final String LIST_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departure_times);

		initializeActionbar();
		initializeList();

		processReceivedRoute();
	}

	private void initializeActionbar() {
		ImageButton itemCreationButton = (ImageButton) findViewById(R.id.itemCreationButton);
		itemCreationButton.setOnClickListener(departureTimeCreationListener);
	}

	private final OnClickListener departureTimeCreationListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			callDepartureTimeCreation();
		}

		private void callDepartureTimeCreation() {
			// TODO: Call intent
		}
	};

	@Override
	protected void initializeList() {
		SimpleAdapter departureTimesAdapter = new SimpleAdapter(activityContext, listData,
			R.layout.departure_times_list_item, new String[] { LIST_ITEM_TEXT_ID },
			new int[] { R.id.text });

		setListAdapter(departureTimesAdapter);

		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// TODO: registerForContextMenu(getListView());
	}

	private void processReceivedRoute() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.someError));

			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadDepartureTimes();
	}

	private void loadDepartureTimes() {
		new LoadDepartureTimesTask().execute();
	}

	private class LoadDepartureTimesTask extends AsyncTask<Void, Void, String>
	{
		private List<Time> departureTimes;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			setEmptyListText(getString(R.string.loadingDepartureTimes));
		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				departureTimes = route.getDepartureTimetable();
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (departureTimes.isEmpty()) {
				setEmptyListText(getString(R.string.noDepartureTimes));
			}
			else {
				fillList(departureTimes);
				updateList();
			}

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	@Override
	protected void addItemToList(Object itemData) {
		Time time = (Time) itemData;

		HashMap<String, Object> timeItem = new HashMap<String, Object>();

		timeItem.put(LIST_ITEM_TEXT_ID, time.toString());
		timeItem.put(LIST_ITEM_OBJECT_ID, time);

		listData.add(timeItem);
	}
}
