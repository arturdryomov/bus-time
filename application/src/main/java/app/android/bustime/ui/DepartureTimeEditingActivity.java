package app.android.bustime.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Route;
import app.android.bustime.db.Time;


public class DepartureTimeEditingActivity extends DepartureTimeCreationActivity
{
	private Route route;
	private Time time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		processReceivedRouteAndTime();

		super.onCreate(savedInstanceState);

		setActivityViewsInscriptions();
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

	@Override
	protected void performSubmitAction() {
		new UpdateDepartureTimeTask().execute();
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

	@Override
	protected void setUpCurrentTime() {
		departureTimeHour = time.getHours();
		departureTimeMinute = time.getMinutes();

		TimePicker departureTimePicker = (TimePicker) findViewById(R.id.picker_departure_time);
		departureTimePicker.setCurrentHour(departureTimeHour);
		departureTimePicker.setCurrentMinute(departureTimeMinute);
	}

	private void setActivityViewsInscriptions() {
		TextView actionBarTitle = (TextView) findViewById(R.id.text_action_bar);
		actionBarTitle.setText(R.string.title_departure_time_editing);

		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setText(R.string.button_update_departure_time);
	}
}
