package eu.ydp.empiria.player.client.module.connection.presenter.view;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import eu.ydp.empiria.player.client.gin.factory.TouchRecognitionFactory;
import eu.ydp.empiria.player.client.module.connection.item.ConnectionItem;
import eu.ydp.empiria.player.client.module.connection.view.event.*;
import eu.ydp.empiria.player.client.module.img.events.coordinates.PointerEventsCoordinates;
import eu.ydp.empiria.player.client.util.events.internal.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.internal.emulate.TouchEvent;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.TouchHandler;
import eu.ydp.empiria.player.client.util.position.PositionHelper;
import eu.ydp.gwtutil.client.debug.log.ConsoleAppender;
import eu.ydp.gwtutil.client.debug.log.Logger;
import eu.ydp.gwtutil.client.event.factory.TouchEventChecker;
import eu.ydp.gwtutil.client.util.UserAgentChecker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractConnectionView extends Composite implements ConnectionView, TouchHandler {
    private final Set<ConnectionMoveHandler> handlers = new HashSet<ConnectionMoveHandler>();
    private final Set<ConnectionMoveEndHandler> endMoveHandlers = new HashSet<ConnectionMoveEndHandler>();
    private final Set<ConnectionMoveStartHandler> startMoveHandlers = new HashSet<ConnectionMoveStartHandler>();
    private final Set<ConnectionMoveCancelHandler> moveCancelHandlers = new HashSet<ConnectionMoveCancelHandler>();
    @Inject
    private TouchEventChecker touchEventChecker;
    @Inject
    protected EventsBus eventsBus;
    @Inject
    private PositionHelper positionHelper;
    @Inject
    private PointerEventsCoordinates pointerEventsCoordinates;

    private final static boolean NOT_MOBILE_BROWSER = !UserAgentChecker.isMobileUserAgent();

    @Inject
    protected TouchRecognitionFactory touchRecognitionFactory;

    private boolean drawFollowTouch;

    @Override
    public void setDrawFollowTouch(boolean followTouch) {
        this.drawFollowTouch = followTouch;
    }

    public boolean isDrawFollowTouch() {
        return drawFollowTouch;
    }

    @Override
    public void addElementToMainView(IsWidget widget) {
        getView().add(widget);
    }

    @Override
    public void addConnectionMoveHandler(ConnectionMoveHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void addConnectionMoveEndHandler(ConnectionMoveEndHandler handler) {
        endMoveHandlers.add(handler);
    }

    @Override
    public void addConnectionMoveStartHandler(ConnectionMoveStartHandler handler) {
        startMoveHandlers.add(handler);
    }

    @Override
    public void addConnectionMoveCancelHandler(ConnectionMoveCancelHandler handler) {
        moveCancelHandlers.add(handler);
    }

    protected void callOnMoveHandlers(ConnectionMoveEvent event) {
        for (ConnectionMoveHandler handler : handlers) {
            handler.onConnectionMove(event);
        }
    }

    protected void callOnMoveEndHandlers(ConnectionMoveEndEvent event) {
        for (ConnectionMoveEndHandler handler : endMoveHandlers) {
            handler.onConnectionMoveEnd(event);
        }
    }

    protected void callOnMoveStartHandlers(ConnectionMoveStartEvent event) {
        for (ConnectionMoveStartHandler handler : startMoveHandlers) {
            handler.onConnectionStart(event);
        }
    }

    protected void callOnMoveCancelHandlers() {
        for (ConnectionMoveCancelHandler handler : moveCancelHandlers) {
            handler.onConnectionMoveCancel();
        }
    }

    public void onTouchMove(NativeEvent event) {
        if (getView() != null) {
            if (NOT_MOBILE_BROWSER) {
                event.preventDefault();
            }
            callOnMoveHandlers(new ConnectionMoveEvent(getPositionX(event), getPositionY(event), event));
        }
    }

    public void addFirstColumnItems(Collection<ConnectionItem> items) {
        for (ConnectionItem item : items) {
            addFirstColumnItem(item);
        }
    }

    public void addSecondColumnItems(Collection<ConnectionItem> items) {
        for (ConnectionItem item : items) {
            addSecondColumnItem(item);
        }
    }

    protected int getPositionX(NativeEvent event) {
        return positionHelper.getXPositionRelativeToTarget(event, getView().getElement());
    }

    protected int getPositionY(NativeEvent event) {
        return positionHelper.getYPositionRelativeToTarget(event, getView().getElement());
    }



    @Override
    public void onTouchEvent(TouchEvent event) {
        new Logger(new ConsoleAppender()).info("onTouchEvent + " + event.getNativeEvent());
        NativeEvent nativeEvent = event.getNativeEvent();

        JsArray<Touch> touches = nativeEvent.getTouches();
        boolean isMouseClick = isMouseClick(touches); // method is also called
        // when MouseEvents
        // occur
        boolean isOneFingerTouch = isOneFingerTouch(touches);

        switch (event.getType()) {
            case TOUCH_START:
                if (isMouseClick || isOneFingerTouch) {
                    new Logger(new ConsoleAppender()).info("touchStart");
                    onTouchStart(nativeEvent);
                } else {
                    new Logger(new ConsoleAppender()).info("touchCancel");
                    onTouchCancel(nativeEvent);
                }
                break;
            case TOUCH_CANCEL:
            case TOUCH_END:
                new Logger(new ConsoleAppender()).info("touchCancelEnd");
                onTouchEnd(nativeEvent);
                break;
            case TOUCH_MOVE:
                if (isMouseClick || isOneFingerTouch) {
                    new Logger(new ConsoleAppender()).info("touchMove");
                    onTouchMove(nativeEvent);
                } else {
                    new Logger(new ConsoleAppender()).info("touchMoveCancel");
                    onTouchCancel(nativeEvent);
                }
                break;
            default:
                new Logger(new ConsoleAppender()).info("break");
                break;
        }

    }

    private boolean isMouseClick(JsArray<Touch> touches) {
        return (touches == null) && pointerEventsCoordinates.isEmpty();
    }

    private boolean isOneFingerTouch(JsArray<Touch> touches) {
        return touchEventChecker.isOnlyOneFinger(touches) || pointerEventsCoordinates.isOnePointer();
    }

    @Override
    public int getHeight() {
        return getOffsetHeight();
    }

    @Override
    public int getWidth() {
        return getOffsetWidth();
    }

    protected abstract FlowPanel getView();

    @Override
    public abstract void addFirstColumnItem(ConnectionItem item);

    @Override
    public abstract void addSecondColumnItem(ConnectionItem item);

    public abstract void onTouchStart(NativeEvent event);

    public abstract void onTouchEnd(NativeEvent event);

    public abstract void onTouchCancel(NativeEvent event);
}
