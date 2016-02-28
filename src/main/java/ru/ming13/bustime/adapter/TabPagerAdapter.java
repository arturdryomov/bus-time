package ru.ming13.bustime.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Arrays;

import ru.ming13.bustime.R;
import ru.ming13.bustime.fragment.RoutesFragment;
import ru.ming13.bustime.fragment.StopsFragment;

public final class TabPagerAdapter extends FragmentPagerAdapter
{
	public static final class TabPositions
	{
		private TabPositions() {
		}

		public static final int ROUTES = 0;
		public static final int STOPS = 1;
	}

	private final Context context;

	public TabPagerAdapter(@NonNull Context context, @NonNull FragmentManager fragmentManager) {
		super(fragmentManager);

		this.context = context.getApplicationContext();
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case TabPositions.ROUTES:
				return RoutesFragment.newInstance();

			case TabPositions.STOPS:
				return StopsFragment.newInstance();

			default:
				throw new RuntimeException("Position is not supported.");
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case TabPositions.ROUTES:
				return context.getString(R.string.title_routes);

			case TabPositions.STOPS:
				return context.getString(R.string.title_stops);

			default:
				throw new RuntimeException("Position is not supported.");
		}
	}

	@Override
	public int getCount() {
		return Arrays.asList(TabPositions.ROUTES, TabPositions.STOPS).size();
	}
}
