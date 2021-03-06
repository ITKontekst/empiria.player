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

package eu.ydp.empiria.player.client.module.connection;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import eu.ydp.empiria.player.client.util.position.Point;
import eu.ydp.empiria.player.client.util.style.StyleToPropertyMappingHelper;
import eu.ydp.gwtutil.client.util.UserAgentChecker;
import eu.ydp.gwtutil.client.util.UserAgentChecker.MobileUserAgent;
import eu.ydp.gwtutil.client.util.geom.HasDimensions;

import java.util.HashMap;
import java.util.Map;

public class ConnectionSurfaceViewImpl extends Composite implements ConnectionSurfaceView {

    private final Context2d context;
    private final Canvas canvas;
    private final StyleToPropertyMappingHelper styleHelper;
    private final LineSegmentChecker lineSegmentChecker;
    private final Map<String, String> propertiesToClear = new HashMap<String, String>();

    private final boolean IS_ANDROID_4 = UserAgentChecker.isMobileUserAgent(MobileUserAgent.ANDROID4);

    private Map<String, String> lastSetStyles = new HashMap<String, String>();
    private LineSegment lineSegment;

    @Inject
    public ConnectionSurfaceViewImpl(@Assisted HasDimensions dimensions, StyleToPropertyMappingHelper styleHelper, LineSegmentChecker lineSegmentChecker) {
        this.styleHelper = styleHelper;
        this.lineSegmentChecker = lineSegmentChecker;
        this.canvas = Canvas.createIfSupported();
        setupCanvas(dimensions);
        this.context = canvas.getContext2d();
        initWidget(canvas);
        // ma znajdowac sie pod caloscia
        setStylesForSurface();
    }

    private void setupCanvas(HasDimensions point) {
        canvas.setWidth(point.getWidth() + "px");
        canvas.setHeight(point.getHeight() + "px");
        canvas.setCoordinateSpaceWidth(point.getWidth());
        canvas.setCoordinateSpaceHeight(point.getHeight());
    }

    private void setStylesForSurface() {
        canvas.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        canvas.getElement().getStyle().setLeft(0, Unit.PX);
        canvas.getElement().getStyle().setTop(0, Unit.PX);
    }

    @Override
    public void drawLine(Point start, Point end) {
        context.save();
        lineSegment = new LineSegment(start, end);
        clear();

        context.beginPath();
        context.moveTo(start.getX(), start.getY());
        context.lineTo(end.getX(), end.getY());
        context.stroke();
        context.restore();
    }

    public void updateStyles(Map<String, String> styles) {
        styleHelper.applyStyles((JavaScriptObject) context, styles);
    }

    @Override
    public void clear() {
        context.clearRect(0, 0, canvas.getCoordinateSpaceWidth(), canvas.getCoordinateSpaceHeight());
        applyHackForCanvasInAndroid4();
    }

    /**
     * @see <a href="http://code.google.com/p/android/issues/detail?id=35474#c25" >android bug trucker</a>
     */
    private void applyHackForCanvasInAndroid4() {
        if (IS_ANDROID_4) {
            canvas.setWidth(canvas.getCoordinateSpaceWidth() + "px");
            applyStyles(lastSetStyles);
        }
    }

    public Widget getView() {
        return canvas;
    }

    @Override
    public boolean isPointOnPath(Point point) {
        return lineSegmentChecker.isLineSegmentNearPoint(lineSegment, point);
    }

    @Override
    public void applyStyles(Map<String, String> styles) {
        styleHelper.applyStyles(context, propertiesToClear);
        styleHelper.applyStyles(context, styles);
        lastSetStyles = styles;
        propertiesToClear.clear();
        for (String property : styles.keySet()) {
            propertiesToClear.put(property, "");
        }
    }

    @Override
    public void setOffsetLeft(int offsetLeft) {
        getElement().getStyle().setLeft(offsetLeft, Unit.PX);
    }

    @Override
    public int getOffsetLeft() {
        return getElement().getOffsetLeft();
    }

    @Override
    public void setOffsetTop(int offsetTop) {
        getElement().getStyle().setTop(offsetTop, Unit.PX);
    }
}
