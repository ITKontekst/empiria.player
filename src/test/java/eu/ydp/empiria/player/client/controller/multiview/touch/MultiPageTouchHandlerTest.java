/*
 * Copyright 2017 Young Digital Planet S.A.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.ydp.empiria.player.client.controller.multiview.touch;

import com.google.gwt.dom.client.NativeEvent;
import eu.ydp.empiria.player.client.controller.multiview.IMultiPageController;
import eu.ydp.empiria.player.client.controller.multiview.MultiPageController;
import eu.ydp.empiria.player.client.util.events.internal.emulate.TouchEvent;
import eu.ydp.empiria.player.client.util.events.internal.emulate.TouchTypes;
import eu.ydp.gwtutil.client.event.TouchEventReader;
import eu.ydp.gwtutil.client.util.UserAgentUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MultiPageTouchHandlerTest {

    private MultiPageTouchHandler testObj;

    @Mock
    private UserAgentUtil userAgentUtil;
    @Mock
    private TouchEventReader touchEventReader;
    @Mock
    private TouchController touchController;
    @Mock
    private TouchEndTimerFactory touchEndTimerFactory;
    @Mock
    private ITouchEndTimer touchEndTimer;
    @Mock
    private TouchEvent touchEvent;
    @Mock
    private IMultiPageController multiPageController;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(touchEndTimerFactory.createTimer(any(MultiPageTouchHandler.class))).thenReturn(touchEndTimer);

        testObj = new MultiPageTouchHandler(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory);
        testObj.setMultiPageController(multiPageController);
        verify(touchEndTimerFactory).createTimer(testObj);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);
    }

    @Test
    public void touchOnEndTimerTest_isAndroid() {
        when(userAgentUtil.isStackAndroidBrowser()).thenReturn(true);
        testObj.touchOnEndTimer();

        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);
        inOrder.verify(userAgentUtil).isStackAndroidBrowser();
        inOrder.verify(multiPageController).animatePageSwitch();
        inOrder.verify(touchController).resetTouchModel();
    }

    @Test
    public void touchOnEndTimerTest_isNotAndroid() {
        when(userAgentUtil.isStackAndroidBrowser()).thenReturn(false);
        testObj.touchOnEndTimer();

        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);
        inOrder.verify(userAgentUtil).isStackAndroidBrowser();
    }

    @Test
    public void resetModelAndTimer() {

        testObj.resetModelAndTimer();
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);
        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(touchController).resetTouchModel();
    }

    @Test
    public void onTouchEvent_TOUCH_START_canSwypeTest() {
        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_START);

        // when(multiPageController.isAnimationRunning()).thenReturn(true);
        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        when(touchController.canSwype(multiPageController)).thenReturn(false);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchController).canSwype(multiPageController);
    }

    @Test
    public void onTouchEventTest_TOUCH_START_isSecondFingerAdd_test() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_START);
        when(touchController.canSwype(multiPageController)).thenReturn(true);
        when(touchController.isSwypeStarted()).thenReturn(true);
        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchController).canSwype(multiPageController);
        inOrder.verify(touchController).isSwypeStarted();
    }

    @Test
    public void onTouchEventTest_TOUCH_START_complete_test() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_START);

        when(touchController.canSwype(multiPageController)).thenReturn(true);
        when(touchController.isSwypeStarted()).thenReturn(false);
        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchController).canSwype(multiPageController);
        inOrder.verify(touchController).isSwypeStarted();

        inOrder.verify(touchController).updateOnTouchStart(nativeEvent);

        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(multiPageController).resetFocusAndStyles();
    }

    @Test
    public void onTouchEventTest_TOUCH_MOVE_canMoveTest() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_MOVE);

        when(touchController.canMove(multiPageController)).thenReturn(false);
        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(touchController).canMove(multiPageController);
    }

    @Test
    public void onTouchEventTest_TOUCH_MOVE_isNotHorizontalSwipeTest() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_MOVE);

        when(touchController.canMove(multiPageController)).thenReturn(true);
        when(touchController.isReadyToStartAnimation()).thenReturn(false);
        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(touchController).canMove(multiPageController);
        inOrder.verify(touchController).updateEndPoint(nativeEvent);
        inOrder.verify(touchController).isReadyToStartAnimation();
        inOrder.verify(touchController).setVerticalSwipeDetected(true);
    }

    @Test
    public void onTouchEventTest_TOUCH_MOVE_isReadyToStartAnnimationTest() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_MOVE);

        when(touchController.canMove(multiPageController)).thenReturn(true);
        when(touchController.isReadyToStartAnimation()).thenReturn(true);
        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        float swypeLength = 200f;
        when(touchController.getSwypePercentLength()).thenReturn(swypeLength);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(touchController).canMove(multiPageController);
        inOrder.verify(touchController).updateEndPoint(nativeEvent);
        inOrder.verify(touchController).isReadyToStartAnimation();
        inOrder.verify(touchEventReader).preventDefault(nativeEvent);
        inOrder.verify(touchEndTimer).schedule(MultiPageController.TOUCH_END_TIMER_TIME);
        inOrder.verify(touchController).isSwipeRight();
        inOrder.verify(touchController).getSwypePercentLength();
        inOrder.verify(multiPageController).move(false, swypeLength);
        inOrder.verify(touchController).updateAfterSwypeDetected();
    }

    @Test
    public void onTouchEventTest_TOUCH_END_isNotSwipeStartedTest() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_END);

        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(touchController).isSwipeStarted();
        inOrder.verify(touchController).resetTouchModel();
        inOrder.verify(multiPageController).resetFocusAndStyles();
    }

    @Test
    public void onTouchEventTest_TOUCH_END_isSwipeStartedAndCanNotSwitchPageTest() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_END);

        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        when(touchController.isSwipeStarted()).thenReturn(true);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(touchController).isSwipeStarted();
        inOrder.verify(touchEventReader).preventDefault(nativeEvent);
        inOrder.verify(touchController).updateOnTouchEnd(nativeEvent);
        inOrder.verify(touchController).canSwitchPage();
        inOrder.verify(multiPageController).animatePageSwitch();
        inOrder.verify(touchController).setSwypeStarted(false);
        inOrder.verify(touchController).resetTouchModel();
        inOrder.verify(multiPageController).resetFocusAndStyles();
    }

    @Test
    public void onTouchEventTest_TOUCH_END_isSwipeStartedAndCanSwitchPageTest() {

        // when
        when(touchEvent.getType()).thenReturn(TouchTypes.TOUCH_END);

        NativeEvent nativeEvent = mock(NativeEvent.class);
        when(touchEvent.getNativeEvent()).thenReturn(nativeEvent);
        when(touchController.isSwipeStarted()).thenReturn(true);
        when(touchController.canSwitchPage()).thenReturn(true);
        // given
        testObj.onTouchEvent(touchEvent);

        // then
        InOrder inOrder = inOrder(userAgentUtil, touchEventReader, touchController, touchEndTimerFactory, touchEndTimer, touchEvent, multiPageController);

        inOrder.verify(touchEvent).getType();
        inOrder.verify(touchEvent).getNativeEvent();
        inOrder.verify(touchEndTimer).cancel();
        inOrder.verify(touchController).isSwipeStarted();
        inOrder.verify(touchEventReader).preventDefault(nativeEvent);
        inOrder.verify(touchController).updateOnTouchEnd(nativeEvent);
        inOrder.verify(touchController).canSwitchPage();
        inOrder.verify(multiPageController).switchPage();
        inOrder.verify(touchController).setSwypeStarted(false);
        inOrder.verify(touchController).resetTouchModel();
        inOrder.verify(multiPageController).resetFocusAndStyles();
    }

    @Test
    public void setTouchReservationTest() {
        boolean touchReservation = true;
        testObj.setTouchReservation(touchReservation);

        verify(touchController).setTouchReservation(touchReservation);
    }

    @Test
    public void getTouchReservationTest() {
        testObj.getTouchReservation();

        verify(touchController).isTouchReservation();
    }

    @Test
    public void getDirectionTest() {
        testObj.getDirection();
        verify(touchController).getDirection();

    }

    public void resetModelAndTimerTest() {
        testObj.resetModelAndTimer();
        verify(touchEndTimer).cancel();
        verify(touchController).resetTouchModel();
    }

}
