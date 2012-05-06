package app.android.bustime.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import app.android.bustime.local.Route;


public class RouteCreationActivity extends Activity
{
	private final Context activityContext = this;

	private String routeName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_creation);

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
				callRouteCreation();
			}
			else {
				UserAlerter.alert(activityContext, userDataErrorMessage);
			}
		}

		private void callRouteCreation() {
			new CreateRouteTask().execute();
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

	private class CreateRouteTask extends AsyncTask<Void, Void, String>
	{
		private Route route;

		@Override
		protected String doInBackground(Void... params) {
			try {
				route = DbProvider.getInstance().getRoutes().createRoute(routeName);
			}
			catch (AlreadyExistsException e) {
				return getString(R.string.error_route_exists);
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
				callDepartureTimesList();

				finish();
			}
			else {
				UserAlerter.alert(activityContext, errorMessage);
			}
		}

		private void callDepartureTimesList() {
			Intent callIntent = IntentFactory.createDepartureTimetableIntent(activityContext, route);
			startActivity(callIntent);
		}
	}
}
