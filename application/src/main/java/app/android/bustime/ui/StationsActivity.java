package app.android.bustime.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import app.android.bustime.R;
import app.android.bustime.db.Route;


public class StationsActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return FragmentFactory.createStationsFragment(this, extractReceivedRoute());
	}

	private Route extractReceivedRoute() {
		Bundle intentExtras = getIntent().getExtras();

		if (!IntentProcessor.haveMessage(intentExtras)) {
			UserAlerter.alert(this, getString(R.string.error_unspecified));
			finish();
		}

		return (Route) IntentProcessor.extractMessage(intentExtras);
	}
}
