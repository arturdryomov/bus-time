package app.android.bustime.ui;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Station;


public class StationRenamingActivity extends Activity
{
	private final Context activityContext = this;

	private Station station;

	private String stationName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_renaming);

		processReceivedStation();

		initializeBodyControls();
		setUpReceivedStationData();
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

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirmListener);
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			readUserDataFromFields();

			String userDataErrorMessage = getUserDataErrorMessage();

			if (userDataErrorMessage.isEmpty()) {
				callStationUpdating();
			}
		}

		private void callStationUpdating() {
			new UpdateStationTask().execute();
		}
	};

	private void readUserDataFromFields() {
		EditText stationNameEdit = (EditText) findViewById(R.id.station_name_edit);

		stationName = stationNameEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
		if (stationName.isEmpty()) {
			return getString(R.string.error_empty_station_name);
		}

		return new String();
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

	private void setUpReceivedStationData() {
		EditText stationNameEdit = (EditText) findViewById(R.id.station_name_edit);
		stationNameEdit.setText(station.getName());
	}
}
