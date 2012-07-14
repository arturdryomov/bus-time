package app.android.bustime.ui;


import android.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;


abstract class FragmentWrapperActivity extends SherlockFragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setUpFragment();
	}

	protected void setUpFragment() {
		if (!isFragmentInstalled()) {
			installFragment();
		}
	}

	protected boolean isFragmentInstalled() {
		return getSupportFragmentManager().findFragmentById(R.id.content) != null;
	}

	protected void installFragment() {
		getSupportFragmentManager().beginTransaction().add(R.id.content, buildFragment()).commit();
	}

	protected abstract Fragment buildFragment();
}
