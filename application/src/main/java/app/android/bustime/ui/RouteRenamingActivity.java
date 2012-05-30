package app.android.bustime.ui;


import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import app.android.bustime.R;
import app.android.bustime.db.AlreadyExistsException;
import app.android.bustime.db.Route;


public class RouteRenamingActivity extends RouteCreationActivity
{
	private Route route;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setActivityViewsInscriptions();

		processReceivedRoute();
		setUpReceivedRouteData();
	}

	private void setActivityViewsInscriptions() {
		TextView actionBarTitle = (TextView) findViewById(R.id.text_action_bar);
		actionBarTitle.setText(R.string.title_route_renaming);

		Button confirmButton = (Button) findViewById(R.id.button_confirm);
		confirmButton.setText(R.string.button_rename_route);
	}

	@Override
	protected void performSubmitAction() {
		new UpdateRouteTask().execute();
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

	private void setUpReceivedRouteData() {
		EditText routeNameEdit = (EditText) findViewById(R.id.edit_route_name);
		routeNameEdit.setText(route.getName());
	}
}
