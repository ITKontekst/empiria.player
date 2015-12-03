package eu.ydp.empiria.player.client.util.events.internal.emulate;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.HandlerRegistration;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.HasTouchHandlers;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.ITouchHandlerInitializer;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.TouchHandler;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.TouchHandlerProvider;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.touchon.TouchOnCancelHandler;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.touchon.TouchOnEndHandler;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.touchon.TouchOnMoveHandler;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.touchon.TouchOnStartHandler;
import eu.ydp.empiria.player.client.util.events.internal.AbstractEventHandler;
import eu.ydp.empiria.player.client.util.events.internal.EventType;

//TODO dopisac rozpoznawanie gestow
public class TouchRecognition extends AbstractEventHandler<TouchHandler, TouchTypes, TouchEvent> implements HasTouchHandlers, MouseDownHandler, MouseUpHandler,
        MouseMoveHandler {
    private final Widget listenOn;
    private boolean touchMoveHandlers = false;
    private boolean emulateClickAsTouch = true;
    private boolean globalTouchEnd;
    private final ITouchHandlerInitializer touchHandlerInitializer;

    @AssistedInject
    public TouchRecognition(@Assisted("listenOn") Widget listenOn, TouchHandlerProvider touchHandlersProvider) {
        this.listenOn = listenOn;
        this.touchHandlerInitializer = touchHandlersProvider.getTouchHandlersInitializer();
    }

    @AssistedInject
    public TouchRecognition(@Assisted("listenOn") Widget listenOn, @Assisted("emulateClickAsTouch") Boolean emulateClickAsTouch,
                            TouchHandlerProvider touchHandlersInitializer) {
        this.listenOn = listenOn;
        this.emulateClickAsTouch = emulateClickAsTouch.booleanValue();
        this.touchHandlerInitializer = touchHandlersInitializer.getTouchHandlersInitializer();
    }

    @AssistedInject
    public TouchRecognition(@Assisted("listenOn") Widget listenOn, @Assisted("emulateClickAsTouch") Boolean emulateClickAsTouch,
                            @Assisted("globalTouchEnd") Boolean globalTouchEnd, TouchHandlerProvider touchHandlersInitializer) {
        this.listenOn = listenOn;
        this.emulateClickAsTouch = emulateClickAsTouch.booleanValue();
        this.globalTouchEnd = globalTouchEnd.booleanValue();
        this.touchHandlerInitializer = touchHandlersInitializer.getTouchHandlersInitializer();
    }

    private void addTouchMoveHandlers() {
        if (!touchMoveHandlers) {
            touchHandlerInitializer.addTouchMoveHandler(createTouchMoveHandler(), listenOn);
            touchMoveHandlers = true;
        }
    }

    private void addTouchEndHandlers() {
        touchHandlerInitializer.addTouchEndHandler(createTouchEndHandler(), listenOn);

        if (emulateClickAsTouch) {
            ((globalTouchEnd) ? RootPanel.get() : listenOn).addDomHandler(this, MouseUpEvent.getType());
        }
    }

    private void addTouchStartHandlers() {
        touchHandlerInitializer.addTouchStartHandler(createTouchStartHandler(), listenOn);

        if (emulateClickAsTouch) {
            listenOn.addDomHandler(this, MouseDownEvent.getType());
        }
    }

    private void addTouchCancelHandlers() {
        touchHandlerInitializer.addTouchCancelHandler(createTouchCancelHandler(), listenOn);
    }

    private void touchStart(NativeEvent event) {
        fireEvent(new TouchEvent(TouchTypes.TOUCH_START, event));
    }

    private void touchEnd(NativeEvent event) {
        fireEvent(new TouchEvent(TouchTypes.TOUCH_END, event));
    }

    private void touchMove(NativeEvent event) {
        fireEvent(new TouchEvent(TouchTypes.TOUCH_MOVE, event));
    }

    private void touchCancel(NativeEvent event) {
        fireEvent(new TouchEvent(TouchTypes.TOUCH_CANCEL, event));
    }

    @Override
    protected void dispatchEvent(TouchHandler handler, TouchEvent event) {
        handler.onTouchEvent(event);
    }

    private void addTouchHandlers(TouchTypes type) {
        switch (type) {
            case TOUCH_END:
                addTouchEndHandlers();
                break;
            case TOUCH_START:
                addTouchStartHandlers();
                break;
            case TOUCH_MOVE:
                addTouchMoveHandlers();
                break;
            case TOUCH_CANCEL:
                addTouchCancelHandlers();
                break;
            default:
                break;
        }
    }

    @Override
    public HandlerRegistration addTouchHandler(TouchHandler handler, EventType<TouchHandler, TouchTypes> event) {
        addTouchHandlers((TouchTypes) event.getType());
        return addHandler(handler, event);
    }

    @Override
    public HandlerRegistration[] addTouchHandlers(TouchHandler handler, EventType<TouchHandler, TouchTypes>... events) {
        for (EventType<TouchHandler, TouchTypes> event : events) {
            addTouchHandlers((TouchTypes) event.getType());
        }
        return addHandlers(handler, events);
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        touchMove(event.getNativeEvent());
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        touchEnd(event.getNativeEvent());
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        touchStart(event.getNativeEvent());
    }

    private TouchOnMoveHandler createTouchMoveHandler() {
        return new TouchOnMoveHandler() {

            @Override
            public void onMove(NativeEvent event) {
                touchMove(event);

            }
        };
    }

    private TouchOnEndHandler createTouchEndHandler() {
        return new TouchOnEndHandler() {

            @Override
            public void onEnd(NativeEvent event) {
                touchEnd(event);
            }
        };
    }

    private TouchOnStartHandler createTouchStartHandler() {
        return new TouchOnStartHandler() {

            @Override
            public void onStart(NativeEvent event) {
                touchStart(event);
            }
        };
    }

    private TouchOnCancelHandler createTouchCancelHandler() {
        return new TouchOnCancelHandler() {

            @Override
            public void onCancel(NativeEvent event) {
                touchCancel(event);
            }
        };
    }
}