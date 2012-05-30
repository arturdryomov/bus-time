package app.android.bustime.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TimePicker;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;
import app.android.bustime.db.Time;


public class ShiftTimeEditingActivity extends FormActivity
{
	private Route route;
	private Station station;
	private Time time;

	private int shiftTimeHour;
	private int shiftTimeMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_shift_time_editing);

		super.onCreate(savedInstanceState);

		processReceivedRouteAndStation();

		initializeTimePicker();
		setUpReceivedData();
	}

	@Override
	protected void readUserDataFromFields() {
		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.picker_shift_time);

		shiftTimeHour = shiftTimePicker.getCurrentHour();
		shiftTimeMinute = shiftTimePicker.getCurrentMinute();
	}

	@Override
	protected String getUserDataErrorMessage() {
		return new String();
	}

	@Override
	protected void performSubmitAction() {
		new UpdateShiftTimeTask().execute();
	}

	private class UpdateShiftTimeTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				station.removeShiftTimeForRoute(route);
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
				UserAlerter.alert(activityContext, errorMessage);
			}
		}
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

		UserAlerter.alert(activityContext, getString(R.string.error_unspecified));

		finish();
	}

	private void initializeTimePicker() {
		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.picker_shift_time);
		shiftTimePicker.setIs24HourView(true);
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

		TimePicker shiftTimePicker = (TimePicker) findViewById(R.id.picker_shift_time);
		shiftTimePicker.setCurrentHour(shiftTimeHour);
		shiftTimePicker.setCurrentMinute(shiftTimeMinute);
	}
}
