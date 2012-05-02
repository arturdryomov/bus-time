package app.android.bustime.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;
import app.android.bustime.R;
import app.android.bustime.local.DbException;
import app.android.bustime.local.DbProvider;
import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;


public class StationCreationActivity extends Activity
{
	private final Context activityContext = this;

	private Route route;
	private List<Station> stations;
	private String stationName;
	private int shiftTimeHour;
	private int shiftTimeMinute;

	private final List<HashMap<String, Object>> stationsList = new ArrayList<HashMap<String, Object>>();
	private static final String SPINNER_ITEM_TEXT_ID = "text";
	private static final String SPINNER_ITEM_OBJECT_ID = "object";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_creation);

		initializeBodyControls();

		processReceivedRoute();
		setUpNullTime();

		loadStations();
		// initializeStationsSpinner();
	}

	private void fillStationsList() {

	}


	private void initializeStationsSpinner() {
		if (stations.isEmpty()) {
			CheckBox stationWasCreateBox = (CheckBox) findViewById(R.id.stationWasCreatedCheckbox);
			stationWasCreateBox.setVisibility(View.GONE);

			return;
		}

	}

	private void loadStations() {
		new LoadStationsTask().execute();
	}

	private class LoadStationsTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				stations = DbProvider.getInstance().getStations().getStationsList();
			}
			catch (DbException e) {
				return getString(R.string.noStations);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (!errorMessage.isEmpty()) {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(confirmListener);

		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shiftTimePicker);
		shiftTimePicker.setIs24HourView(true);
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			readUserDataFromFields();

			String userDataErrorMessage = getUserDataErrorMessage();

			if (userDataErrorMessage.isEmpty()) {
				callStationCreation();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}

		private void callStationCreation() {
			new StationCreationTask().execute();
		}
	};

	private void readUserDataFromFields() {
		EditText stationNameEdit = (EditText) findViewById(R.id.stationNameEdit);
		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shiftTimePicker);

		stationName = stationNameEdit.getText().toString().trim();
		shiftTimeHour = shiftTimePicker.getCurrentHour();
		shiftTimeMinute = shiftTimePicker.getCurrentMinute();
	}

	private String getUserDataErrorMessage() {
		return getStationNameErrorMessage();
	}

	private String getStationNameErrorMessage() {
		if (stationName.isEmpty()) {
			return getString(R.string.enterStationName);
		}

		return new String();
	}

	private class StationCreationTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				Station station = DbProvider.getInstance().getStations().createStation(stationName);
				station.insertShiftTimeForRoute(route, new Time(shiftTimeHour, shiftTimeMinute));
			}
			catch (DbException e) {
				return getString(R.string.someError);
			}

			return new String();
		}

		@Override
		protected void onPostExecute(String errorMessage) {
			super.onPostExecute(errorMessage);

			if (errorMessage.isEmpty()) {
				finish();
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
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

	private void setUpNullTime() {
		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shiftTimePicker);
		shiftTimePicker.setCurrentHour(0);
		shiftTimePicker.setCurrentMinute(0);
	}
}
