package app.android.bustime.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Station;


public class StationRenamingActivity extends FormActivity
{
	private Station station;

	private String stationName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_station_renaming);
		super.onCreate(savedInstanceState);

		processReceivedStation();

		setUpReceivedStationData();
	}

	@Override
	protected void readUserDataFromFields() {
		EditText stationNameEdit = (EditText) findViewById(R.id.edit_station_name);

		stationName = stationNameEdit.getText().toString().trim();
	}

	@Override
	protected String getUserDataErrorMessage() {
		if (stationName.isEmpty()) {
			return getString(R.string.error_empty_station_name);
		}

		return new String();
	}

	@Override
	protected void performSubmitAction() {
		new UpdateStationTask().execute();
	}

	private class UpdateStationTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				station.setName(stationName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_station_exists);
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

	private void processReceivedStation() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			station = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
		}
		else {
			UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

			finish();
		}
	}

	private void setUpReceivedStationData() {
		EditText stationNameEdit = (EditText) findViewById(R.id.edit_station_name);
		stationNameEdit.setText(station.getName());
	}
}
