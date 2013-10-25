package ru.ming13.bustime.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.ming13.bustime.fragment.RoutesFragment;
import ru.ming13.bustime.fragment.StationsFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter
{
	private static final int TABS_COUNT = 2;

	public static final class TabsPositions
	{
		private TabsPositions() {
		}

		public static final int ROUTES = 0;
		public static final int STATIONS = 1;
	}

	public TabsPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case TabsPositions.ROUTES:
				return RoutesFragment.newInstance();

			case TabsPositions.STATIONS:
				return StationsFragment.newInstance();

			default:
				return null;
		}
	}

	@Override
	public int getCount() {
		return TABS_COUNT;
	}
}
