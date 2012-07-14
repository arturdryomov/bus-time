package app.android.bustime.ui;


import android.os.Bundle;
import app.android.bustime.R;
import com.actionbarsherlock.app.SherlockMapActivity;
import com.google.android.maps.MapView;


public class MapActivity extends SherlockMapActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_map);

		MapView mapView = (MapView) findViewById(R.id.map);
		mapView.setBuiltInZoomControls(true);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
