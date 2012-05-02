package app.android.bustime.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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

	private Station chosenExistingStation;
	private String stationName;
	private int shiftTimeHour;
	private int shiftTimeMinute;

	private boolean isStationExist;

	private final List<HashMap<String, Object>> stationsData;
	private static final String SPINNER_ITEM_TEXT_ID = "text";
	private static final String SPINNER_ITEM_OBJECT_ID = "object";

	public StationCreationActivity() {
		super();

		stationsData = new ArrayList<HashMap<String, Object>>();
		isStationExist = false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_creation);

		processReceivedRoute();

		initializeBodyControls();
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

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(confirmListener);

		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shiftTimePicker);
		shiftTimePicker.setIs24HourView(true);
		setUpNullTimeToShiftTimePicker();

		CheckBox stationWasCreatedCheckbox = (CheckBox) findViewById(R.id.stationWasCreatedCheckbox);
		stationWasCreatedCheckbox.setOnCheckedChangeListener(isStationExistListener);

		fillStationsSpinner();
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
		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shiftTimePicker);
		shiftTimeHour = shiftTimePicker.getCurrentHour();
		shiftTimeMinute = shiftTimePicker.getCurrentMinute();

		if (isStationExist) {
			chosenExistingStation = getChoosedStation();
		}
		else {
			EditText stationNameEdit = (EditText) findViewById(R.id.stationNameEdit);
			stationName = stationNameEdit.getText().toString().trim();
		}
	}

	private Station getChoosedStation() {
		Spinner stationsSpinner = (Spinner) findViewById(R.id.stationsSpinner);

		@SuppressWarnings("unchecked")
		Map<String, Object> stationSpinnerItem = (Map<String, Object>) stationsSpinner
			.getSelectedItem();

		return (Station) stationSpinnerItem.get(SPINNER_ITEM_OBJECT_ID);
	}

	private String getUserDataErrorMessage() {
		if (isStationExist) {
			return new String();
		}
		else {
			return getStationNameErrorMessage();
		}
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
				Station stationToInsertShiftTime;

				if (isStationExist) {
					stationToInsertShiftTime = chosenExistingStation;
				}
				else {
					stationToInsertShiftTime = DbProvider.getInstance().getStations()
						.createStation(stationName);
				}

				stationToInsertShiftTime.insertShiftTimeForRoute(route, new Time(shiftTimeHour,
					shiftTimeMinute));
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

	private void setUpNullTimeToShiftTimePicker() {
		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shiftTimePicker);
		shiftTimePicker.setCurrentHour(0);
		shiftTimePicker.setCurrentMinute(0);
	}

	private final OnCheckedChangeListener isStationExistListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			isStationExist = isChecked;

			updateBodyControls();
		}
	};

	private void updateBodyControls() {
		EditText stationNameEdit = (EditText) findViewById(R.id.stationNameEdit);
		Spinner stationsListSpinner = (Spinner) findViewById(R.id.stationsSpinner);

		if (isStationExist) {
			stationNameEdit.setVisibility(View.GONE);
			stationsListSpinner.setVisibility(View.VISIBLE);
		}
		else {
			stationNameEdit.setVisibility(View.VISIBLE);
			stationsListSpinner.setVisibility(View.GONE);
		}
	}

	private void fillStationsSpinner() {
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
				return;
			}

			if (stations.isEmpty()) {
				hidePossibilityToChooseExistingStation();
				return;
			}

			setUpStationsSpinner();
		}
	}

	private void hidePossibilityToChooseExistingStation() {
		CheckBox stationWasCreateBox = (CheckBox) findViewById(R.id.stationWasCreatedCheckbox);
		stationWasCreateBox.setVisibility(View.GONE);
	}

	private void setUpStationsSpinner() {
		fillStationsData();

		SimpleAdapter stationsAdapter = new SimpleAdapter(activityContext, stationsData,
			android.R.layout.simple_spinner_item, new String[] { SPINNER_ITEM_TEXT_ID },
			new int[] { android.R.id.text1 });
		stationsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner stationsSpinner = (Spinner) findViewById(R.id.stationsSpinner);
		stationsSpinner.setAdapter(stationsAdapter);
	}

	private void fillStationsData() {
		stationsData.clear();

		for (Station station : stations) {
			addItemToStationsData(station);
		}
	}

	private void addItemToStationsData(Station station) {
		HashMap<String, Object> stationItem = new HashMap<String, Object>();

		stationItem.put(SPINNER_ITEM_TEXT_ID, station.getName());
		stationItem.put(SPINNER_ITEM_OBJECT_ID, station);

		stationsData.add(stationItem);
	}
}
