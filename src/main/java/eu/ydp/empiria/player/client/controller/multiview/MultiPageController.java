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

package eu.ydp.empiria.player.client.controller.multiview;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.HandlerRegistration;
import eu.ydp.empiria.player.client.controller.Page;
import eu.ydp.empiria.player.client.controller.extensions.internal.InternalExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.FlowRequestSocketUserExtension;
import eu.ydp.empiria.player.client.controller.flow.request.FlowRequestInvoker;
import eu.ydp.empiria.player.client.controller.multiview.swipe.SwipeType;
import eu.ydp.empiria.player.client.controller.multiview.touch.MultiPageTouchHandler;
import eu.ydp.empiria.player.client.gin.factory.TouchRecognitionFactory;
import eu.ydp.empiria.player.client.module.button.NavigationButtonDirection;
import eu.ydp.empiria.player.client.util.events.internal.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.internal.emulate.TouchEvent;
import eu.ydp.empiria.player.client.util.events.internal.emulate.TouchTypes;
import eu.ydp.empiria.player.client.util.events.internal.emulate.handlers.HasTouchHandlers;
import eu.ydp.empiria.player.client.util.events.internal.player.PlayerEvent;
import eu.ydp.empiria.player.client.util.events.internal.player.PlayerEventTypes;
import eu.ydp.gwtutil.client.NumberUtils;
import eu.ydp.gwtutil.client.collections.KeyValue;
import eu.ydp.gwtutil.client.proxy.RootPanelDelegate;
import eu.ydp.gwtutil.client.proxy.WindowDelegate;
import eu.ydp.gwtutil.client.util.UserAgentChecker;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
public class MultiPageController extends InternalExtension implements FlowRequestSocketUserExtension, IMultiPageController {

    @Inject
    private EventsBus eventsBus;
    @Inject
    private PanelCache panelsCache;
    @Inject
    private Page page;
    @Inject
    private MultiPageView view;
    @Inject
    private TouchRecognitionFactory touchRecognitionFactory;
    @Inject
    private MultiPageTouchHandler multiPageTouchHandler;
    @Inject
    private VisiblePagesManager visiblePagesManager;
    @Inject
    @Named("multiPageControllerMainPanel")
    private FlowPanel mainPanel;
    @Inject
    private Provider<SwipeType> swipeType;
    @Inject
    private MultiPageControllerStyleManager multiPageControllerStyleManager;
    @Inject
    private WindowDelegate windowDelegate;
    @Inject
    private RootPanelDelegate rootPanelDelegate;
    @Inject
    private PageSwitchAnimation pageSwitchAnimation;
    @Inject
    private PanelAttacher panelAttacher;
    @Inject
    private PanelDetacher panelDetacher;
    @Inject
    private Provider<ResizeTimer> resizeTimer;

    private int currentVisiblePage = -1;
    private final static int WIDTH = 100;
    private final static int DEFAULT_ANIMATION_TIME = 500;
    private final static int QUICK_ANIMATION_TIME = 150;
    public final static int TOUCH_END_TIMER_TIME = 800;
    private float currentPosition;

    private FlowRequestInvoker flowRequestInvoker;
    private int swipeLength = 220;

    private final Set<Integer> loadedPages = new HashSet<>();
    private int pageProgressBar = -1;


    private final Set<HandlerRegistration> touchHandlers = new HashSet<>();
    private boolean focusDropped;

    private static final Function<KeyValue<FlowPanel, FlowPanel>, FlowPanel> keyPanelExtractor = new Function<KeyValue<FlowPanel, FlowPanel>, FlowPanel>() {
        @Override
        public FlowPanel apply(KeyValue<FlowPanel, FlowPanel> keyValue) {
            return keyValue.getKey();
        }
    };

    private void clearPagesStyles() {
        Collection<KeyValue<FlowPanel, FlowPanel>> cacheValues = panelsCache.getCache().values();
        Collection<FlowPanel> keyPanels = Collections2.transform(cacheValues, keyPanelExtractor);
        multiPageControllerStyleManager.clearPagesStyles(keyPanels);
    }

    protected int getHeightForPage(int pageNumber) {
        FlowPanel flowPanel = (FlowPanel) getViewForPage(pageNumber).getParent();
        return flowPanel == null ? 0 : flowPanel.getOffsetHeight();
    }

    protected void setHeight(int height) {
        if (isSwipeDisabled()) {
            view.setHeight("auto");
        } else {
            view.setHeight(height + "px");
        }
    }

    @Override
    public void setVisiblePage(int pageNumber) {
        showProgressBarForPage(pageNumber);
        ResizeTimer resizeTimer = this.resizeTimer.get();
        resizeTimer.cancelAndReset();
        resizeTimer.schedule(350);
        if (currentVisiblePage != pageNumber && pageNumber >= 0) {
            this.currentVisiblePage = pageNumber;
            pageSwitchAnimation.animatePageSwitch(this, getPositionLeft(), -pageNumber * WIDTH, null, DEFAULT_ANIMATION_TIME, false);
        } else {
            eventsBus.fireEvent(new PlayerEvent(PlayerEventTypes.PAGE_VIEW_LOADED));
        }
        hideProgressBarForPage(pageNumber);
    }

    public FlowPanel getViewForPage(Integer pageNumber) {
        boolean pageWasCreated = panelsCache.isPresent(pageNumber);
        KeyValue<FlowPanel, FlowPanel> panel = panelsCache.getOrCreateAndPut(pageNumber);

        if (!pageWasCreated) {
            mainPanel.add(panel.getKey());
            applyStylesToPanelOnIndex(pageNumber);
            showProgressBarForPage(pageNumber);
        }
        loadedPages.add(pageNumber);
        return panel.getValue();
    }

    private void applyStylesToPanelOnIndex(Integer pageNumber) {
        KeyValue<FlowPanel, FlowPanel> panelsPair = panelsCache.getOrCreateAndPut(pageNumber);
        FlowPanel panel = panelsPair.getKey();

        boolean isChangeToNextPage = isChangeToNextPage(pageNumber);
        multiPageControllerStyleManager.setPageStyles(panel, isChangeToNextPage);
    }

    private boolean isChangeToNextPage(Integer pageNumber) {
        return pageNumber > currentVisiblePage;
    }

    @Override
    public void detachAttachPanels() {
        Set<Integer> pagesToDetach = visiblePagesManager.getPagesToDetach(currentVisiblePage);
        panelDetacher.detach(pagesToDetach);

        List<Integer> pagesToAttache = visiblePagesManager.getPagesToAttache(currentVisiblePage);
        panelAttacher.attach(this, pagesToAttache);
    }

    @Override
    public void animatePageSwitch() {
        pageSwitchAnimation.animatePageSwitch(this, getPositionLeft(), getCurrentPosition(), null, QUICK_ANIMATION_TIME, true);
    }

    private float getPositionLeft() {
        Style style = getStyle();
        return NumberUtils.tryParseFloat(style.getLeft().replaceAll("[a-z%]+$", ""));
    }

    /**
     * Na androidzie podczas swipe gapy zle sie rysowaly nachodzac na siebie.
     * Pozbycie sie z nich focusa na czas swipe rozwiazalo problem
     */
    private void dropFocus() {
        focusDropped = true;
        NodeList<Element> elementsByTagName = rootPanelDelegate.getBodyElement().getElementsByTagName("input");
        for (int x = 0; x < elementsByTagName.getLength(); ++x) {
            elementsByTagName.getItem(x).blur();
        }
    }

    @Override
    public void move(boolean swipeRight, float length) {
        if (!focusDropped && UserAgentChecker.isStackAndroidBrowser()) {
            dropFocus();
        }
        if (swipeRight) {
            showProgressBarForPage(currentVisiblePage + 1);
        } else {
            showProgressBarForPage(currentVisiblePage - 1);
        }
        Style style = getStyle();
        float position = getPositionLeft();
        if (swipeRight) {
            style.setLeft(position - length, Unit.PCT);
        } else {
            style.setLeft(position + length, Unit.PCT);
        }
    }

    public Style getStyle() {
        return mainPanel.getElement().getStyle();
    }

    private void showProgressBarForPage(int pageIndex) {
        if (!loadedPages.contains(pageIndex) && isValidPageIndex(pageIndex)) {
            Panel panel = getViewForPage(pageIndex);
            panel.add(new ProgressPanel());
            pageProgressBar = pageIndex;
        }
    }

    private boolean isValidPageIndex(int pageIndex) {
        return pageProgressBar != pageIndex && page.isNotLastPage(pageIndex) && pageIndex >= 0;
    }

    public void hideCurrentPageProgressBar(){
        hideProgressBarForPage(currentVisiblePage);
    }

    private void hideProgressBarForPage(int pageIndex) {
        if (page.isNotLastPage(pageIndex)) {
            FlowPanel panel = getViewForPage(pageIndex);
            for (int x = 0; x < panel.getWidgetCount(); ++x) {
                if (panel.getWidget(x) instanceof ProgressPanel) {
                    panel.getWidget(x).removeFromParent();
                    break;
                }
            }
        }
    }

    @Override
    public void reset() {
        multiPageTouchHandler.resetModelAndTimer();
        resetFocusAndStyles();
        multiPageTouchHandler.setTouchReservation(true);
    }

    @Override
    public void resetFocusAndStyles() {
        focusDropped = false;
        clearPagesStyles();
    }

    @Override
    public boolean isZoomed() {
        int clientWidth = windowDelegate.getClientWidth();
        int innerWidth = windowDelegate.getInnerWidth();
        return innerWidth < clientWidth;
    }

    @Override
    public void switchPage() {
        if (multiPageTouchHandler.getTouchReservation()) {
            pageSwitchAnimation.animatePageSwitch(this, getPositionLeft(), currentPosition, null, QUICK_ANIMATION_TIME, true);
            return;
        }

        NavigationButtonDirection direction = multiPageTouchHandler.getDirection();
        float percent = (float) swipeLength / rootPanelDelegate.getOffsetWidth() * 100;
        if (direction != null) {
            if (direction == NavigationButtonDirection.PREVIOUS && page.getCurrentPageNumber() > 0) {
                pageSwitchAnimation.animatePageSwitch(this, getPositionLeft(), getPositionLeft() + (WIDTH - percent), direction, DEFAULT_ANIMATION_TIME, true);
                eventsBus.fireEvent(new PlayerEvent(PlayerEventTypes.PAGE_CHANGE_STARTED));
            } else if (direction == NavigationButtonDirection.NEXT && page.isNotLastPage(page.getCurrentPageNumber())) {
                pageSwitchAnimation.animatePageSwitch(this, getPositionLeft(), getPositionLeft() - (WIDTH - percent), direction, DEFAULT_ANIMATION_TIME, true);
                eventsBus.fireEvent(new PlayerEvent(PlayerEventTypes.PAGE_CHANGE_STARTED));
            } else {
                pageSwitchAnimation.animatePageSwitch(this, getPositionLeft(), currentPosition, null, QUICK_ANIMATION_TIME, true);
            }
        }
    }

    @Override
    public void setFlowRequestsInvoker(FlowRequestInvoker fri) {
        flowRequestInvoker = fri;
    }

    @Override
    public void init() {
        multiPageTouchHandler.setMultiPageController(this);
        view.setController(this);
        configureSwipe();

        LoadPageViewHandler loadPageViewHandler = new LoadPageViewHandler(this);
        TouchReservationHandler touchReservationHandler = new TouchReservationHandler(this);
        PageViewLoadedHandler pageViewLoadedHandler = new PageViewLoadedHandler(this);
        eventsBus.addHandler(PlayerEvent.getType(PlayerEventTypes.LOAD_PAGE_VIEW), loadPageViewHandler);
        eventsBus.addHandler(PlayerEvent.getType(PlayerEventTypes.TOUCH_EVENT_RESERVATION), touchReservationHandler);
        eventsBus.addHandler(PlayerEvent.getType(PlayerEventTypes.PAGE_VIEW_LOADED), pageViewLoadedHandler);

        view.add(mainPanel);
    }

    public boolean isSwipeDisabled() {
        return swipeType.get() == SwipeType.DISABLED;
    }

    private void configureSwipe() {
        if (isSwipeDisabled()) {
            for (HandlerRegistration registration : touchHandlers) {
                registration.removeHandler();
            }
            touchHandlers.clear();
            setVisiblePageCount(1);
        } else {
            RootPanel rootPanel = rootPanelDelegate.getRootPanel();
            HasTouchHandlers touchHandler = touchRecognitionFactory.getTouchRecognition(rootPanel, false);
            touchHandlers.add(touchHandler.addTouchHandler(multiPageTouchHandler, TouchEvent.getType(TouchTypes.TOUCH_START)));
            touchHandlers.add(touchHandler.addTouchHandler(multiPageTouchHandler, TouchEvent.getType(TouchTypes.TOUCH_MOVE)));
            touchHandlers.add(touchHandler.addTouchHandler(multiPageTouchHandler, TouchEvent.getType(TouchTypes.TOUCH_END)));
            setVisiblePageCount(3);
        }
        panelsCache.setSwipeType(swipeType.get());
    }

    private void setVisiblePageCount(int count) {
        visiblePagesManager.setVisiblePageCount(count);
    }

    public int getCurrentVisiblePage() {
        return currentVisiblePage;
    }

    public FlowPanel getMainPanel() {
        return mainPanel;
    }

    public int getWidth() {
        return WIDTH;
    }

    public MultiPageView getView() {
        return view;
    }

    @Override
    public boolean isAnimationRunning() {
        return pageSwitchAnimation.isAnimationRunning();
    }

    private float getCurrentPosition() {
        return currentPosition;
    }

    public void setSwipeLength(int swipeLength) {
        this.swipeLength = swipeLength;
    }

    public void setCurrentPosition(float currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void invokeNavigationRequest(NavigationButtonDirection direction) {
        flowRequestInvoker.invokeRequest(direction.getRequest());
    }

    public void setCurrentPageHeight() {
        int height = getHeightForPage(currentVisiblePage);
        setHeight(height);
    }

    public int getCurrentPageHeight() {
        return getHeightForPage(currentVisiblePage);
    }

    public boolean isCurrentPage(int pageNumber) {
        return pageNumber == currentVisiblePage;
    }
}
