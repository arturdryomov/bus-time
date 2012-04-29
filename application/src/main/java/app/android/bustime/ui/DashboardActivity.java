package app.android.bustime.ui;


import android.app.Activity;
import android.os.Bundle;
import app.android.bustime.R;


public class DashboardActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.routes);
	}
}
