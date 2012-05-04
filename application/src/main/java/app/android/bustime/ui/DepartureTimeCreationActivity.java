package app.android.bustime.ui;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import app.android.bustime.R;
import app.android.bustime.local.Route;
import app.android.bustime.local.Time;


public class DepartureTimeCreationActivity extends Activity
{
	private final Context activityContext = this;

	private Route route;
	private int departureTimeHour;
	private int departureTimeMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departure_time_creation);

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

		if (DateFormat.is24HourFormat(activityContext)) {
			TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);
			departureTimePicker.setIs24HourView(true);
		}
		setUpCurrentTime();
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			readUserDataFromTimePicker();
			callDepartureTimeCreation();
		}

		private void callDepartureTimeCreation() {
			new CreateDepartureTimeTask().execute();
		}
	};

	private void readUserDataFromTimePicker() {
		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);

		departureTimeHour = departureTimePicker.getCurrentHour();
		departureTimeMinute = departureTimePicker.getCurrentMinute();
	}

	private class CreateDepartureTimeTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			route.insertDepartureTime(new Time(departureTimeHour, departureTimeMinute));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			finish();
		}
	}

	private void setUpCurrentTime() {
		Time currentTime = Time.getCurrentTime();
		departureTimeHour = currentTime.getHours();
		departureTimeMinute = currentTime.getMinutes();

		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);
		departureTimePicker.setCurrentHour(departureTimeHour);
		departureTimePicker.setCurrentMinute(departureTimeMinute);
	}
}
