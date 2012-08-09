package app.android.bustime.ui;


import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.maps.OnSingleTapListener;
import com.readystatesoftware.maps.TapControlledMapView;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


public class StationsOverlay extends BalloonItemizedOverlay<OverlayItem>
{
	private static final boolean SHOW_CLOSE_BUTTON_ON_BALLOON = false;
	private static final boolean SHOW_DISCLOSURE_BUTTON_ON_BALLOON = true;
	private static final boolean DRAW_SHADOW_FOR_ITEMS = false;

	public interface OnBalloonTapListener
	{
		public void onBalloonTap(StationOverlayItem stationOverlayItem);
	}

	private OnBalloonTapListener onBalloonTapListener;
	private final List<OverlayItem> overlays;

	public StationsOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);

		overlays = new ArrayList<OverlayItem>();

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
	protected OverlayItem createItem(int itemIndex) {
		return overlays.get(itemIndex);
	}

	@Override
	public int size() {
		return overlays.size();
	}

	public void addOverlayItem(OverlayItem overlay) {
		overlays.add(overlay);
	}

	public void populate(List<OverlayItem> overlayItems) {
		for (OverlayItem overlayItem : overlayItems) {
			addOverlayItem(overlayItem);
		}

		populate();
	}

	public void setOnBalloonTapListener(OnBalloonTapListener onBalloonTapListener) {
		this.onBalloonTapListener = onBalloonTapListener;
	}

	@Override
	protected boolean onBalloonTap(int itemIndex, OverlayItem overlayItem) {
		if (!isOnBalloonTapListenerSet()) {
			return false;
		}

		hideAllBalloons();
		onBalloonTapListener.onBalloonTap((StationOverlayItem) overlayItem);

		return true;
	}

	private boolean isOnBalloonTapListenerSet() {
		return onBalloonTapListener != null;
	}
}
