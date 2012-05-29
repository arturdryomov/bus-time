package app.android.bustime.ui.dispatch.stations;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.DbException;
import app.android.bustime.db.DbProvider;
import app.android.bustime.ui.IntentFactory;
import app.android.bustime.ui.UserAlerter;


public class StationCreationActivity extends Activity
{
	private final Context activityContext = this;

	private final static int LOCATION_REQUEST_CODE = 42;

	private final static double DEFAULT_LATITUDE = 55.534229;
	private final static double DEFAULT_LONGITUDE = 28.661546;

	private String stationName;
	private double latitude = DEFAULT_LATITUDE;
	private double longitude = DEFAULT_LONGITUDE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_creation);

		initializeBodyControls();
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setOnClickListener(confirmListener);

		Button locationButton = (Button) findViewById(R.id.button_location);
		locationButton.setOnClickListener(locationListener);
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
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
			new CreateStationTask().execute();
		}
	};

	private void readUserDataFromFields() {
		EditText stationNameEdit = (EditText) findViewById(R.id.edit_station_name);

		stationName = stationNameEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
		if (stationName.isEmpty()) {
			return getString(R.string.error_empty_station_name);
		}

		return new String();
	}

	private class CreateStationTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				DbProvider.getInstance().getStations().createStation(stationName, latitude, longitude);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_station_exists);
			}
			catch (DbException e) {
				return getString(R.string.error_unspecified);
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

	private final OnClickListener locationListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			callStationLocationActivity();
		}

		private void callStationLocationActivity() {
			Intent callIntent = IntentFactory.createStationLocationIntent(activityContext, latitude,
				longitude);
			startActivityForResult(callIntent, LOCATION_REQUEST_CODE);
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((resultCode == RESULT_OK) && (requestCode == LOCATION_REQUEST_CODE)) {
			latitude = data.getExtras().getDouble(IntentFactory.MESSAGE_ID);
			longitude = data.getExtras().getDouble(IntentFactory.EXTRA_MESSAGE_ID);
		}
	}
}
