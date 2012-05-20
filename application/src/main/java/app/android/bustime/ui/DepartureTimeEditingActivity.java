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
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Route;
import app.android.bustime.db.Time;


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
		setContentView(R.layout.activity_departure_time_editing);

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

		UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

		finish();
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirmListener);

		setSystemTimeFormatForTimePicker();
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
		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departure_time_picker);

		departureTimeHour = departureTimePicker.getCurrentHour();
		departureTimeMinute = departureTimePicker.getCurrentMinute();
	}

	private class UpdateDepartureTimeTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				route.removeDepartureTime(time);
				route.insertDepartureTime(new Time(departureTimeHour, departureTimeMinute));
			}
			catch (AlreadyExistsException e) {
				route.insertDepartureTime(time);

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

	private void setSystemTimeFormatForTimePicker() {
		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departure_time_picker);
		departureTimePicker.setIs24HourView(DateFormat.is24HourFormat(activityContext));
	}

	private void setUpReceivedTime() {
		departureTimeHour = time.getHours();
		departureTimeMinute = time.getMinutes();

		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.departure_time_picker);
		departureTimePicker.setCurrentHour(departureTimeHour);
		departureTimePicker.setCurrentMinute(departureTimeMinute);
	}
}
