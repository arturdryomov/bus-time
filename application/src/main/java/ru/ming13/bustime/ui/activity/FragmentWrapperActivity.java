package ru.ming13.bustime.ui.activity;


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

	private void setUpFragment() {
		if (!isFragmentInstalled()) {
			installFragment();
		}
	}

	private boolean isFragmentInstalled() {
		return getSupportFragmentManager().findFragmentById(R.id.content) != null;
	}

	private void installFragment() {
		getSupportFragmentManager().beginTransaction().add(R.id.content, buildFragment()).commit();
	}

	protected abstract Fragment buildFragment();
}
