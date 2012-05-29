package app.android.bustime.ui;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import app.android.bustime.R;
import app.android.bustime.ui.dispatch.routes.DispatchRoutesIntentFactory;
import app.android.bustime.ui.dispatch.stations.DispatchStationsIntentFactory;


public class DashboardActivity extends Activity
{
	private final Context activityContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		initializeActionbar();
		initializeButtons();
	}

	private void initializeActionbar() {
		ImageButton syncButton = (ImageButton) findViewById(R.id.button_sync);
		syncButton.setOnClickListener(syncListener);
	}

	private final OnClickListener syncListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			callSynchronizationActivity();
		}

		private void callSynchronizationActivity() {
			Intent callIntent = IntentFactory.createSynchronizationIntent(activityContext);
			startActivity(callIntent);
		}
	};

	private void initializeButtons() {
		Button routesButton = (Button) findViewById(R.id.button_routes);
		routesButton.setOnClickListener(routesListener);

		Button stationsButton = (Button) findViewById(R.id.button_stations);
		stationsButton.setOnClickListener(stationsListener);
	}

	private final OnClickListener routesListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			callRoutesList();
		}

		private void callRoutesList() {
			Intent callIntent = DispatchRoutesIntentFactory.createRoutesListIntent(activityContext);
			startActivity(callIntent);
		}
	};

	private final OnClickListener stationsListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			callStationsList();
		}

		private void callStationsList() {
			Intent callIntent = DispatchStationsIntentFactory.createStationsListIntent(activityContext);
			startActivity(callIntent);
		}
	};
}
