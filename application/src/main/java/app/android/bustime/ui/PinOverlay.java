package app.android.bustime.ui;


import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;


public class PinOverlay extends ItemizedOverlay<OverlayItem>
{
	private final MapView map;

	private final Drawable pinImage;
	private OverlayItem pinAsMapElement;
	private final ImageView pinAsView;

	private final int pinImageHorizontalOffset;
	private final int pinImageVerticalOffset;

	private List<OverlayItem> mapItems;

	private boolean isDraggingInProgress;
	private Point startOfDragPosition;

	private static final double DEFAULT_LATITUDE;
	private static final double DEFAULT_LONGITUDE;
	private static final String DEFAULT_PIN_TITLE;
	private static final String DEFAULT_PIN_SNIPPET;

	static {
		DEFAULT_LATITUDE = 55.534229;
		DEFAULT_LONGITUDE = 28.661546;

		DEFAULT_PIN_TITLE = new String();
		DEFAULT_PIN_SNIPPET = new String();
	}

	public PinOverlay(Drawable pinImage, ImageView pinView, MapView map) {
		super(pinImage);

		this.pinImage = pinImage;
		this.pinAsView = pinView;
		this.map = map;

		pinImageHorizontalOffset = pinAsView.getDrawable().getIntrinsicWidth() / 2;
		pinImageVerticalOffset = pinAsView.getDrawable().getIntrinsicHeight();

		pinAsMapElement = constructPinAsMapElementAtStartPosition();
		showPinAsMapElement();

		isDraggingInProgress = false;
	}

	private OverlayItem constructPinAsMapElementAtStartPosition() {
		GeoPoint startPinPosition = getGeoPoint(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
		return new OverlayItem(startPinPosition, DEFAULT_PIN_SNIPPET, DEFAULT_PIN_TITLE);
	}

	private GeoPoint getGeoPoint(double latitude, double longitude) {
		final int microdegreesInDegrees = 1000000;

		return (new GeoPoint((int) (latitude * microdegreesInDegrees),
			(int) (longitude * microdegreesInDegrees)));
	}

	private void showPinAsMapElement() {
		if (mapItems == null) {
			mapItems = new ArrayList<OverlayItem>();
		}

		mapItems.add(pinAsMapElement);

		populate();
	}

	@Override
	protected OverlayItem createItem(int position) {
		return (mapItems.get(position));
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		boundCenterBottom(pinImage);

		super.draw(canvas, mapView, shadow);
	}

	@Override
	public int size() {
		return (mapItems.size());
	}

	@Override
	public boolean onTouchEvent(MotionEvent motionEvent, MapView mapView) {
		Point motionEventPosition = getMotionEventPosition(motionEvent);

		switch (motionEvent.getAction()) {
			case MotionEvent.ACTION_DOWN:
				storeStartOfDragPosition(motionEventPosition);

				if (!isEventOnPin()) {
					return super.onTouchEvent(motionEvent, mapView);
				}

				startDraggingProcess();

				return true;
			case MotionEvent.ACTION_MOVE:
				if (!isDraggingInProgress) {
					return super.onTouchEvent(motionEvent, mapView);
				}

				setPinAsViewPosition(new Point(motionEventPosition.x - pinImageHorizontalOffset
					- startOfDragPosition.x, motionEventPosition.y - pinImageVerticalOffset
					- startOfDragPosition.y));

				return true;
			case MotionEvent.ACTION_UP:
				if (!isDraggingInProgress) {
					return super.onTouchEvent(motionEvent, mapView);
				}

				hidePinAsView();

				setPinAsMapElementPosition(new Point(motionEventPosition.x - startOfDragPosition.x,
					motionEventPosition.y - startOfDragPosition.y));

				showPinAsMapElement();

				isDraggingInProgress = false;

				return true;
			default:
				return super.onTouchEvent(motionEvent, mapView);
		}
	}

	private Point getMotionEventPosition(MotionEvent motionEvent) {
		Point position = new Point();

		position.x = (int) motionEvent.getX();
		position.y = (int) motionEvent.getY();

		return position;
	}

	private void storeStartOfDragPosition(Point motionEventPosition) {
		Point pinPosition = getCurrentPinAsMapElementPosition();

		if (startOfDragPosition == null) {
			startOfDragPosition = new Point();
		}

		startOfDragPosition.x = motionEventPosition.x - pinPosition.x;
		startOfDragPosition.y = motionEventPosition.y - pinPosition.y;
	}

	private Point getCurrentPinAsMapElementPosition() {
		Point currentPosition = new Point();

		map.getProjection().toPixels(pinAsMapElement.getPoint(), currentPosition);

		return currentPosition;
	}

	private boolean isEventOnPin() {
		return hitTest(pinAsMapElement, pinImage, startOfDragPosition.x, startOfDragPosition.y);
	}

	private void startDraggingProcess() {
		Point currentPinAsMapElementPosition = getCurrentPinAsMapElementPosition();
		hidePinAsMapElement();

		setPinAsViewPosition(getRealPinPosition(currentPinAsMapElementPosition));
		showPinAsView();

		isDraggingInProgress = true;
	}

	private void hidePinAsMapElement() {
		mapItems.remove(pinAsMapElement);

		populate();
	}

	private Point getRealPinPosition(Point pinAsMapElementPosition) {
		Point realPinPosition = pinAsMapElementPosition;

		realPinPosition.x -= pinImageHorizontalOffset;
		realPinPosition.y -= pinImageVerticalOffset;

		return realPinPosition;
	}

	private void setPinAsViewPosition(Point position) {
		RelativeLayout.LayoutParams pinAsViewParams = (RelativeLayout.LayoutParams) pinAsView
			.getLayoutParams();

		pinAsViewParams.setMargins(position.x, position.y, 0, 0);

		pinAsView.setLayoutParams(pinAsViewParams);
	}

	private void showPinAsView() {
		pinAsView.setVisibility(View.VISIBLE);
	}

	private void hidePinAsView() {
		pinAsView.setVisibility(View.GONE);
	}

	private void setPinAsMapElementPosition(Point position) {
		GeoPoint geoPosition = map.getProjection().fromPixels(position.x, position.y);
		pinAsMapElement = new OverlayItem(geoPosition, DEFAULT_PIN_TITLE, DEFAULT_PIN_SNIPPET);
	}

	public GeoPoint getPosition() {
		return pinAsMapElement.getPoint();
	}

	public void setPosition(GeoPoint position) {
		hidePinAsMapElement();

		pinAsMapElement = new OverlayItem(position, DEFAULT_PIN_TITLE, DEFAULT_PIN_SNIPPET);

		showPinAsMapElement();
	}
}