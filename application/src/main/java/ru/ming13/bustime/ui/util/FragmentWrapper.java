package ru.ming13.bustime.ui.util;


import android.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


public final class FragmentWrapper
{
	private FragmentWrapper() {
	}

	public static void setUpFragment(FragmentActivity fragmentActivity, Fragment fragment) {
		if (!isFragmentInstalled(fragmentActivity)) {
			installFragment(fragmentActivity, fragment);
		}
	}

	private static boolean isFragmentInstalled(FragmentActivity fragmentActivity) {
		return fragmentActivity.getSupportFragmentManager().findFragmentById(R.id.content) != null;
	}

	private static void installFragment(FragmentActivity fragmentActivity, Fragment fragment) {
		FragmentTransaction fragmentTransaction = fragmentActivity.getSupportFragmentManager().beginTransaction();

		fragmentTransaction.add(R.id.content, fragment);

		fragmentTransaction.commit();
	}
}
