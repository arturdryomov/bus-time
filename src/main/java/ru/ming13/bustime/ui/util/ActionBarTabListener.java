package ru.ming13.bustime.ui.util;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.ActionBar;


public class ActionBarTabListener implements ActionBar.TabListener
{
	private final Fragment fragment;

	public ActionBarTabListener(Fragment fragment) {
		this.fragment = fragment;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		if (fragment.isDetached()) {
			fragmentTransaction.attach(fragment);
		}
		else {
			fragmentTransaction.replace(android.R.id.content, fragment);
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		if (!fragment.isDetached()) {
			fragmentTransaction.detach(fragment);
		}
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
}