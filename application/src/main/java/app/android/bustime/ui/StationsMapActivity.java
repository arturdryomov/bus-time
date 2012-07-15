package app.android.bustime.ui;


import android.os.Bundle;
import app.android.bustime.R;
import com.actionbarsherlock.app.SherlockMapActivity;


public class StationsMapActivity extends SherlockMapActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
