package ru.ming13.bustime.ui.util;


import android.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.actionbarsherlock.app.SherlockFragmentActivity;


public final class FragmentWrapper
{
	private FragmentWrapper() {
	}

	public static void setUpFragment(SherlockFragmentActivity fragmentActivity, Fragment fragment) {
		if (!isFragmentInstalled(fragmentActivity)) {
			installFragment(fragmentActivity, fragment);
		}
	}

	private static boolean isFragmentInstalled(SherlockFragmentActivity fragmentActivity) {
		return fragmentActivity.getSupportFragmentManager().findFragmentById(R.id.content) != null;
	}

	private static void installFragment(SherlockFragmentActivity fragmentActivity, Fragment fragment) {
		FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();

		fragmentTransaction.add(R.id.content, fragment);

		fragmentTransaction.commit();
	}
}
