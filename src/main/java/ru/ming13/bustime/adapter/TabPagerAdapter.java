package ru.ming13.bustime.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.ming13.bustime.fragment.RoutesFragment;
import ru.ming13.bustime.fragment.StopsFragment;

public class TabPagerAdapter extends FragmentPagerAdapter
{
	private static final int TAB_COUNT = 2;

	public static final class TabPosition
	{
		private TabPosition() {
		}

		public static final int ROUTES = 0;
		public static final int STOPS = 1;
	}

	public TabPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case TabPosition.ROUTES:
				return RoutesFragment.newInstance();

			case TabPosition.STOPS:
				return StopsFragment.newInstance();

			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		return TAB_COUNT;
	}
}
