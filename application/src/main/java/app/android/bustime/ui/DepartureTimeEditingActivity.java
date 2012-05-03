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


public class DepartureTimeEditingActivity extends Activity
{
	private final Context activityContext = this;

	private Route route;
	private Time time;
	private int departureTimeHour;
	private int departureTimeMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departure_time_editing);

		processReceivedRouteAndTime();

		initializeBodyControls();
	}

	private void processReceivedRouteAndTime() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			if (receivedData.containsKey(IntentFactory.EXTRA_MESSAGE_ID)) {
				route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
				time = receivedData.getParcelable(IntentFactory.EXTRA_MESSAGE_ID);

				return;
			}
		}

		UserAlerter.alert(activityContext, getString(R.string.someError));

		finish();
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(confirmListener);

		if (DateFormat.is24HourFormat(activityContext)) {
			TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);
			departureTimePicker.setIs24HourView(true);
		}
		setUpReceivedTime();
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			readUserDataFromTimePicker();
			callDepartureTimeUpdating();
		}

		private void callDepartureTimeUpdating() {
			new UpdateDepartureTimeTask().execute();
		}
	};

	private void readUserDataFromTimePicker() {
		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);

		departureTimeHour = departureTimePicker.getCurrentHour();
		departureTimeMinute = departureTimePicker.getCurrentMinute();
	}

	private class UpdateDepartureTimeTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			route.removeDepartureTime(time);
			route.insertDepartureTime(new Time(departureTimeHour, departureTimeMinute));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			finish();
		}
	}

	private void setUpReceivedTime() {
		departureTimeHour = time.getHours();
		departureTimeMinute = time.getMinutes();

		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);
		departureTimePicker.setCurrentHour(departureTimeHour);
		departureTimePicker.setCurrentMinute(departureTimeMinute);
	}
}
