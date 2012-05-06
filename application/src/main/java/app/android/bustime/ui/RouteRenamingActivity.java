package app.android.bustime.ui;


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
import app.android.bustime.local.Route;


public class RouteRenamingActivity extends Activity
{
	private final Context activityContext = this;

	private Route route;
	private String routeName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_renaming);

		processReceivedRoute();

		initializeBodyControls();
		setUpReceivedRouteData();
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

	private void initializeBodyControls() {
		Button confirmButton = (Button) findViewById(R.id.confirm_button);
		confirmButton.setOnClickListener(confirmListener);
	}

	private final OnClickListener confirmListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			readUserDataFromFields();

			String userDataErrorMessage = getUserDataErrorMessage();

			if (userDataErrorMessage.isEmpty()) {
				callRouteUpdating();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}

		private void callRouteUpdating() {
			new UpdateRouteTask().execute();
		}
	};

	private void readUserDataFromFields() {
		EditText routeNameEdit = (EditText) findViewById(R.id.route_name_edit);

		routeName = routeNameEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
		return getRouteNameErrorMessage();
	}

	private String getRouteNameErrorMessage() {
		if (routeName.isEmpty()) {
			return getString(R.string.error_empty_route_name);
		}

		return new String();
	}

	private class UpdateRouteTask extends AsyncTask<Void, Void, String>
	{
		@Override
		protected String doInBackground(Void... params) {
			try {
				route.setName(routeName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_route_exists);
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

	private void setUpReceivedRouteData() {
		EditText routeNameEdit = (EditText) findViewById(R.id.route_name_edit);
		routeNameEdit.setText(route.getName());
	}
}
