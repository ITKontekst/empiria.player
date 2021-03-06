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

package eu.ydp.empiria.player.client.module.video;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ElementScalerTest {

    @InjectMocks
    private ElementScaler testObj;
    @Mock
    private Element element;
    @Mock
    private Element parentElement;
    @Mock
    private Style style;
    @Mock
    private Style parentStyle;

    private final double DELTA = 0.01;
    private final int HEIGHT = 9;
    private final int WIDTH = 16;
    private final double EXPECTED_RATIO = 56.25;

    @Before
    public void setUp() throws Exception {
        when(element.getClientWidth()).thenReturn(WIDTH);
        when(element.getClientHeight()).thenReturn(HEIGHT);
        when(element.getStyle()).thenReturn(style);
        when(element.getParentElement()).thenReturn(parentElement);
        when(parentElement.getStyle()).thenReturn(parentStyle);
        testObj = new ElementScaler(element);
    }

    @Test
    public void shouldClearOldSizesAndSetTopPadding() {
        // when
        testObj.setRatio();

        // then
        verify(style).clearWidth();
        verify(style).clearHeight();

        verify(style).setPaddingTop(eq(EXPECTED_RATIO, DELTA), eq(Unit.PCT));
    }

    @Test
    public void shouldClearTopPadding() {
        // when
        testObj.clearRatio();

        // then
        verify(style).clearPaddingTop();
    }

    @Test
    public void shouldSetMaxWidth() {
        // when
        testObj.setMaxWidth(WIDTH);

        // then
        verify(parentStyle).setProperty("maxWidth", WIDTH, Unit.PX);
    }

    @Test
    public void shouldClearMaxWidth() {
        // when
        testObj.clearMaxWidth();

        // then
        verify(parentStyle).clearProperty("maxWidth");
    }
}
