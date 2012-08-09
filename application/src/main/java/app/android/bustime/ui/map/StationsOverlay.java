package app.android.bustime.ui.map;


import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.google.android.maps.MapView;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


public class StationsOverlay extends BalloonItemizedOverlay<StationOverlayItem>
{
	private static final boolean SHOW_CLOSE_BUTTON_ON_BALLOON = false;
	private static final boolean SHOW_DISCLOSURE_BUTTON_ON_BALLOON = true;
	private static final boolean DRAW_SHADOW_FOR_ITEMS = false;

	public interface OnBalloonTapListener
	{
		public void onBalloonTap(StationOverlayItem stationOverlayItem);
	}

	private OnBalloonTapListener onBalloonTapListener;

	private final List<StationOverlayItem> stationOverlayItems;

	public StationsOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);

		stationOverlayItems = new ArrayList<StationOverlayItem>();

		setUpOverlay();
	}

	private void setUpOverlay() {
		setShowClose(SHOW_CLOSE_BUTTON_ON_BALLOON);
		setShowDisclosure(SHOW_DISCLOSURE_BUTTON_ON_BALLOON);

		TapControlledMapView mapView = (TapControlledMapView) getMapView();
		mapView.setOnSingleTapListener(new OnSingleTapListener()
		{
			@Override
			public boolean onSingleTap(MotionEvent motionEvent) {
				hideAllBalloons();

				return true;
			}
		});
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean drawShadow) {
		super.draw(canvas, mapView, DRAW_SHADOW_FOR_ITEMS);
	}

	@Override
	protected StationOverlayItem createItem(int itemIndex) {
		return stationOverlayItems.get(itemIndex);
	}

	@Override
	public int size() {
		return stationOverlayItems.size();
	}

	public void populate(List<StationOverlayItem> overlayItems) {
		for (StationOverlayItem overlayItem : overlayItems) {
			addOverlayItem(overlayItem);
		}

		populate();
	}

	private void addOverlayItem(StationOverlayItem overlay) {
		stationOverlayItems.add(overlay);
	}

	public void setOnBalloonTapListener(OnBalloonTapListener onBalloonTapListener) {
		this.onBalloonTapListener = onBalloonTapListener;
	}

	@Override
	protected boolean onBalloonTap(int itemIndex, StationOverlayItem stationOverlayItem) {
		if (!isOnBalloonTapListenerSet()) {
			return false;
		}

		hideAllBalloons();
		onBalloonTapListener.onBalloonTap(stationOverlayItem);

		return true;
	}

	private boolean isOnBalloonTapListenerSet() {
		return onBalloonTapListener != null;
	}
}
