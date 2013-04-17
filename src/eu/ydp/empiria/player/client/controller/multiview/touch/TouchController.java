package eu.ydp.empiria.player.client.controller.multiview.touch;

import com.google.gwt.dom.client.NativeEvent;
import com.google.inject.Inject;

import eu.ydp.empiria.player.client.controller.multiview.IMultiPageController;
import eu.ydp.empiria.player.client.module.button.NavigationButtonDirection;
import eu.ydp.empiria.player.client.util.events.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.player.PlayerEvent;
import eu.ydp.empiria.player.client.util.events.player.PlayerEventTypes;
import eu.ydp.gwtutil.client.event.TouchEventReader;
import eu.ydp.gwtutil.client.proxy.RootPanelDelegate;
import eu.ydp.gwtutil.client.proxy.WindowDelegate;

public class TouchController {

	private static final int PERCENT_MAX = 100;
	private static final int SWYPE_WIDTH_TO_HEIGHT_LIMIT_RATE = 5;
	private static final int MINIMAL_LENGTH_LIMIT_RATE = 4;
	private final WindowDelegate windowDelegate;
	private final TouchEventReader touchEventReader;
	private final EventsBus eventsBus;
	private final TouchModel touchModel;
	private final RootPanelDelegate rootPanelDelegate;

	@Inject
	public TouchController(WindowDelegate windowDelegate, TouchEventReader touchEventReader, EventsBus eventsBus, TouchModel touchModel,
			RootPanelDelegate rootPanelDelegate) {

		this.windowDelegate = windowDelegate;
		this.touchEventReader = touchEventReader;
		this.eventsBus = eventsBus;
		this.touchModel = touchModel;
		this.rootPanelDelegate = rootPanelDelegate;
	}

	public void updateOnTouchStart(NativeEvent onTouchStartEvent) {

		int y = touchEventReader.getScreenY(onTouchStartEvent);
		int x = touchEventReader.getX(onTouchStartEvent);
		boolean multiTouch = touchEventReader.isMoreThenOneFingerTouch(onTouchStartEvent);

		touchModel.setStartScrollTopPossition(windowDelegate.getScrollTop());
		touchModel.setStartY(y);
		touchModel.setStartX(x);
		touchModel.setLastEndX(x);
		touchModel.setEndX(-1);
		touchModel.setMultiTouch(multiTouch);
		touchModel.setSwipeStarted(false);
		touchModel.setTouchReservation(false);
	}

	public boolean isSwipeStarted() {
		return touchModel.isSwipeStarted();
	}

	public boolean canSwitchPage() {
		int swipeWidth = Math.abs(touchModel.getStartX() - touchModel.getEndX());
		int swipeHeight = Math.abs(touchModel.getStartY() - touchModel.getEndY());

		return touchModel.getEndX() > 0 && isCorrectSwypeAngle(swipeWidth, swipeHeight) && isCorrectSwypeWidth(swipeWidth);
	}

	private boolean isCorrectSwypeWidth(int swipeWidth) {
		return swipeWidth > windowDelegate.getClientWidth() / TouchController.MINIMAL_LENGTH_LIMIT_RATE;
	}

	private boolean isCorrectSwypeAngle(int swipeWidth, int swipeHeight) {
		return swipeWidth / swipeHeight > SWYPE_WIDTH_TO_HEIGHT_LIMIT_RATE;
	}

	public boolean isHorizontalSwipe() {
		int swipeWidth = Math.abs(touchModel.getStartX() - touchModel.getEndX());
		int swipeHeight = Math.abs(touchModel.getStartY() - touchModel.getEndY());
		return swipeHeight < swipeWidth;
	}

	public boolean isTouchReservation() {
		return touchModel.isTouchReservation();
	}

	public boolean isSwypeStarted() {
		return touchModel.getLastEndX() != touchModel.getStartX();
	}

	public void updateEndPoint(NativeEvent onTouchMoveEvent) {
		int y = touchEventReader.getScreenY(onTouchMoveEvent);
		int x = touchEventReader.getX(onTouchMoveEvent);

		touchModel.setEndX(x);
		touchModel.setEndY(y);
	}

	public void updateAfterSwypeDetected() {
		if (!touchModel.isSwipeStarted()) {
			eventsBus.fireEvent(new PlayerEvent(PlayerEventTypes.PAGE_SWIPE_STARTED));
		}
		touchModel.setLastEndX(touchModel.getEndX());
		touchModel.setSwipeStarted(true);
	}

	public float getSwypePercentLength() {
		int swypeWidth = Math.abs(touchModel.getLastEndX() - touchModel.getEndX());
		return ((float) swypeWidth / rootPanelDelegate.getOffsetWidth()) * PERCENT_MAX;
	}

	public boolean isSwypeDetected() {
		return touchModel.getLastEndX() != touchModel.getEndX() && touchModel.getLastEndX() > 0;
	}

	public boolean isSwipeRight() {
		return touchModel.getLastEndX() > touchModel.getEndX();
	}

	private boolean isVerticalSwipe() {
		return Math.abs(windowDelegate.getScrollTop() - touchModel.getStartScrollTopPossition()) > 1;
	}

	public void resetTouchModel() {
		touchModel.setStartX(touchModel.getEndX());
		touchModel.setLastEndX(touchModel.getEndX());
		touchModel.setTouchReservation(false);

	}

	public void updateOnTouchEnd(NativeEvent event) {
		int y = touchEventReader.getFromChangedTouchesScreenY(event);
		int x = touchEventReader.getFromChangedTouchesX(event);

		touchModel.setEndX(x);
		touchModel.setEndY(y);

	}

	public void setTouchReservation(boolean touchReservation) {
		touchModel.setTouchReservation(touchReservation);
	}

	public NavigationButtonDirection getDirection() {
		NavigationButtonDirection direction = null;
		if (touchModel.getEndX() > touchModel.getStartX()) {
			direction = NavigationButtonDirection.PREVIOUS;
		} else if (touchModel.getStartX() > touchModel.getEndX()) {
			direction = NavigationButtonDirection.NEXT;
		}
		return direction;
	}

	public boolean canSwype(IMultiPageController multiPageController) {
		return !multiPageController.isZoomed() && !multiPageController.isAnimationRunning() && !touchModel.isTouchReservation() && !touchModel.isSwypeLock();
	}

	public boolean canMove(IMultiPageController multiPageController) {
		return canSwype(multiPageController) && !isVerticalSwipe() && !touchModel.isMultiTouch();
	}

	public void setSwypeLock(boolean swypeLock) {
		touchModel.setSwypeLock(swypeLock);
	}

	public void setSwypeStarted(boolean b) {
		touchModel.setSwipeStarted(b);

	}

}