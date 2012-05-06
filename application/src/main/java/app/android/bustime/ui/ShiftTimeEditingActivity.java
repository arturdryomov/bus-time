package app.android.bustime.ui;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import app.android.bustime.R;
import app.android.bustime.local.AlreadyExistsException;
import app.android.bustime.local.Route;
import app.android.bustime.local.Station;
import app.android.bustime.local.Time;


public class ShiftTimeEditingActivity extends Activity
{
	private final Context acvitityContext = this;

	private Route route;
	private Station station;
	private Time time;

	private int shiftTimeHour;
	private int shiftTimeMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shift_time_editing);

		processReceivedRouteAndStation();

		initializeBodyControls();
		setUpReceivedData();
	}

	private void processReceivedRouteAndStation() {
		Bundle receivedData = this.getIntent().getExtras();

		if (receivedData.containsKey(IntentFactory.MESSAGE_ID)) {
			if (receivedData.containsKey(IntentFactory.EXTRA_MESSAGE_ID)) {
				route = receivedData.getParcelable(IntentFactory.MESSAGE_ID);
				station = receivedData.getParcelable(IntentFactory.EXTRA_MESSAGE_ID);

				return;
			}
		}

		UserAlerter.alert(acvitityContext, getString(R.string.error_unspecified));

		finish();
	}

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirmListener);

		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shift_time_picker);
		shiftTimePicker.setIs24HourView(true);
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			readUserdataFromTimePicker();
			callShiftTimeUpdating();
		}

		private void callShiftTimeUpdating() {
			new UpdateShiftTimeTask().execute();
		}
	};

	private void readUserdataFromTimePicker() {
		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shift_time_picker);

		shiftTimeHour = shiftTimePicker.getCurrentHour();
		shiftTimeMinute = shiftTimePicker.getCurrentMinute();
	}

	private class UpdateShiftTimeTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				station.removeShiftTimeForRoute(route, time);
				station.insertShiftTimeForRoute(route, new Time(shiftTimeHour, shiftTimeMinute));
			}
			catch (AlreadyExistsException e) {
				station.insertShiftTimeForRoute(route, time);

				return getString(R.string.error_shift_time_exists);
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
				UserAlerter.alert(acvitityContext, errorMessage);
			}
		}
	}

	private void setUpReceivedData() {
		new LoadReceivedTimeTask().execute();
	}

	private class LoadReceivedTimeTask extends AsyncTask<Void, Void, Void>
	{
		@Override
		protected Void doInBackground(Void... params) {
			time = station.getShiftTimeForRoute(route);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			setUpReceivedTime();
		}
	}

	private void setUpReceivedTime() {
		shiftTimeHour = time.getHours();
		shiftTimeMinute = time.getMinutes();

		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.shift_time_picker);
		shiftTimePicker.setCurrentHour(shiftTimeHour);
		shiftTimePicker.setCurrentMinute(shiftTimeMinute);
	}
}
