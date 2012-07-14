package app.android.bustime.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import app.android.bustime.R;
import app.android.bustime.db.Route;
import app.android.bustime.db.Station;


public class TimetableActivity extends FragmentWrapperActivity
{
	@Override
	protected Fragment buildFragment() {
		return FragmentFactory.createTimetableFragment(this, extractReceivedRoute(),
			extractReceivedStation());
	}

	private Route extractReceivedRoute() {
		Bundle intentExtras = getIntent().getExtras();

		if (!IntentProcessor.haveMessage(intentExtras)) {
			UserAlerter.alert(this, getString(R.string.error_unspecified));
			finish();
		}

		return (Route) IntentProcessor.extractMessage(intentExtras);
	}

	private Station extractReceivedStation() {
		Bundle intentExtras = getIntent().getExtras();

		if (!IntentProcessor.haveExtraMessage(intentExtras)) {
			UserAlerter.alert(this, getString(R.string.error_unspecified));
			finish();
		}

		return (Station) IntentProcessor.extractExtraMessage(intentExtras);
	}
}
