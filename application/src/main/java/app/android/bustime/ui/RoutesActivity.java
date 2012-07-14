package app.android.bustime.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import app.android.bustime.R;
import app.android.bustime.db.Station;
import com.actionbarsherlock.app.SherlockFragmentActivity;


public class RoutesActivity extends SherlockFragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpFragment();
	}

	private void setUpFragment() {
		if (!isFragmentInstalled()) {
			installFragment();
		}
	}

	private boolean isFragmentInstalled() {
		return getSupportFragmentManager().findFragmentById(android.R.id.content) != null;
	}

	private void installFragment() {
		Fragment fragment = FragmentFactory.createRoutesFragment(this, extractReceivedStation());
		getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment).commit();
	}

	private Station extractReceivedStation() {
		Bundle intentExtras = getIntent().getExtras();

		if (!IntentProcessor.haveMessage(intentExtras)) {
			UserAlerter.alert(this, getString(R.string.error_unspecified));
			finish();
		}

		return (Station) IntentProcessor.extractMessage(intentExtras);
	}
}
