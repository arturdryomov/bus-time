package app.android.bustime.ui;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Route;


public class RouteRenamingActivity extends Activity
{
	private final Context activityContext = this;

	private Route route;

	private String routeName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_creation);

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
		setActivityViewsInscriptions();

		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setOnClickListener(confirmListener);
	}

	private void setActivityViewsInscriptions() {
		TextView actionBarTitle = (TextView) findViewById(R.id.text_actionbar);
		actionBarTitle.setText(R.string.title_route_renaming);

		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setText(R.string.button_rename_route);
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
		EditText routeNameEdit = (EditText) findViewById(R.id.edit_route_name);

		routeName = routeNameEdit.getText().toString().trim();
	}

	private String getUserDataErrorMessage() {
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
		EditText routeNameEdit = (EditText) findViewById(R.id.edit_route_name);
		routeNameEdit.setText(route.getName());
	}
}
