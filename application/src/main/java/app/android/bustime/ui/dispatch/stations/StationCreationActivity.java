package app.android.bustime.ui.dispatch.stations;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import app.android.bustime.R;
import app.android.bustime.local.AlreadyExistsException;
import app.android.bustime.local.DbException;
import app.android.bustime.local.DbProvider;
import app.android.bustime.ui.UserAlerter;


public class StationCreationActivity extends Activity
{
	private final Context activityContext = this;

	private String stationName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station_creation);

		initializeBodyControls();
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
		EditText stationNameEdit = (EditText) findViewById(R.id.station_name_edit);

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
				// TODO: Fix this
				DbProvider.getInstance().getStations().createStation(stationName, 0, 0);
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
}
