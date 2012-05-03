package app.android.bustime.ui;


import java.util.Calendar;

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
import app.android.bustime.local.DbException;
import app.android.bustime.local.Route;
import app.android.bustime.local.Time;
import app.android.bustime.local.TimeException;


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

		initializeBodyControls();

		processReceivedRoute();
		setUpCurrentTime();
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(confirmListener);

		if (DateFormat.is24HourFormat(activityContext)) {
			TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);
			departureTimePicker.setIs24HourView(true);
		}
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			readUserDataFromTimePicker();
			callDepartureTimeCreation();
		}

		private void callDepartureTimeCreation() {
			new DepartureTimeCreateTask().execute();
		}
	};

	private void readUserDataFromTimePicker() {
		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);

		departureTimeHour = departureTimePicker.getCurrentHour();
		departureTimeMinute = departureTimePicker.getCurrentMinute();
	}

	private class DepartureTimeCreateTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				route.insertDepartureTime(new Time(departureTimeHour, departureTimeMinute));
			}
			catch (TimeException e) {
				return getString(R.string.someError);
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

	private void setUpCurrentTime() {
		final Calendar calendar = Calendar.getInstance();
		departureTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
		departureTimeMinute = calendar.get(Calendar.MINUTE);

		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departureTimePicker);
		departureTimePicker.setCurrentHour(departureTimeHour);
		departureTimePicker.setCurrentMinute(departureTimeMinute);
	}
}
