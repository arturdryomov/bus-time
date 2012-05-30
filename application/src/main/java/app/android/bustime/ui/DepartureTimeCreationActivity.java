package app.android.bustime.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Route;
import app.android.bustime.db.Time;


public class DepartureTimeCreationActivity extends FormActivity
{
	private Route route;

	protected int departureTimeHour;
	protected int departureTimeMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_departure_time_creation);
		super.onCreate(savedInstanceState);

		processReceivedRoute();

		initializeTimePicker();
	}

	@Override
	protected void readUserDataFromFields() {
		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.picker_departure_time);

		departureTimeHour = departureTimePicker.getCurrentHour();
		departureTimeMinute = departureTimePicker.getCurrentMinute();
	}

	@Override
	protected String getUserDataErrorMessage() {
		return new String();
	}

	@Override
	protected void performSubmitAction() {
		new CreateDepartureTimeTask().execute();
	}

	private class CreateDepartureTimeTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				route.insertDepartureTime(new Time(departureTimeHour, departureTimeMinute));
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_departure_time_exists);
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
			UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

			finish();
		}
	}

	private void initializeTimePicker() {
		setSystemTimeFormatForTimePicker();
		setUpCurrentTime();
	}

	private void setSystemTimeFormatForTimePicker() {
		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.picker_departure_time);
		departureTimePicker.setIs24HourView(DateFormat.is24HourFormat(activityContext));
	}

	protected void setUpCurrentTime() {
		Time currentTime = Time.getCurrentTime();
		departureTimeHour = currentTime.getHours();
		departureTimeMinute = currentTime.getMinutes();

		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.picker_departure_time);
		departureTimePicker.setCurrentHour(departureTimeHour);
		departureTimePicker.setCurrentMinute(departureTimeMinute);
	}
}
