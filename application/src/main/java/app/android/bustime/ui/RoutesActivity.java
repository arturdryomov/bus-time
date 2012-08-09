package app.android.bustime.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import app.android.bustime.R;
import app.android.bustime.db.Station;
import app.android.bustime.ui.fragment.RoutesFragment;


public class RoutesActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return RoutesFragment.newInstance(extractReceivedStation());
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
