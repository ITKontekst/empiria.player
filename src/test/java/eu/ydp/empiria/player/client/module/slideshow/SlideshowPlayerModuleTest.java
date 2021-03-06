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

package eu.ydp.empiria.player.client.module.slideshow;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import eu.ydp.empiria.player.client.controller.body.InlineBodyGeneratorSocket;
import eu.ydp.empiria.player.client.module.ModuleSocket;
import eu.ydp.empiria.player.client.module.slideshow.presenter.SlideshowPlayerPresenter;
import eu.ydp.empiria.player.client.module.slideshow.slides.SlideshowController;
import eu.ydp.empiria.player.client.module.slideshow.structure.SlideBean;
import eu.ydp.empiria.player.client.module.slideshow.structure.SlideshowBean;
import eu.ydp.empiria.player.client.module.slideshow.structure.SlideshowModuleStructure;
import eu.ydp.empiria.player.client.module.slideshow.structure.SlideshowPlayerBean;
import eu.ydp.empiria.player.client.util.events.internal.bus.EventsBus;
import eu.ydp.gwtutil.client.service.json.IJSONService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SlideshowPlayerModuleTest {

    @InjectMocks
    private SlideshowPlayerModule testObj;
    @Mock
    private IJSONService ijsonService;
    @Mock
    private SlideshowPlayerPresenter presenter;
    @Mock
    private SlideshowController controller;
    @Mock
    private SlideshowModuleStructure moduleStructure;
    @Mock
    private SlideshowTemplateInterpreter templateInterpreter;
    @Mock
    private ModuleSocket moduleSocket;
    @Mock
    private EventsBus eventsBus;
    @Mock
    private InlineBodyGeneratorSocket inlineBodyGeneratorSocket;

    @Before
    public void init() {
        when(moduleSocket.getInlineBodyGeneratorSocket()).thenReturn(inlineBodyGeneratorSocket);
    }

    @Test
    public void shouldReturnWidget() {
        // given
        Widget widget = mock(Widget.class);
        when(presenter.getView()).thenReturn(widget);

        // when
        Widget result = testObj.getView();

        // then
        assertThat(result).isEqualTo(widget);
    }

    @Test
    public void shouldInitModule_withoutTemplate() {
        // given
        Element element = mock(Element.class);

        SlideshowBean slideshowBean = new SlideshowBean();
        List<SlideBean> slides = Lists.newArrayList();
        slideshowBean.setSlideBeans(slides);

        SlideshowPlayerBean slideshowPlayer = new SlideshowPlayerBean();
        slideshowPlayer.setSlideshowBean(slideshowBean);

        when(moduleStructure.getBean()).thenReturn(slideshowPlayer);

        // when
        testObj.initModule(element, moduleSocket, eventsBus);

        // then
        verify(presenter).init(slideshowBean, inlineBodyGeneratorSocket);
        verify(controller).init(slideshowBean.getSlideBeans(), inlineBodyGeneratorSocket);
        verify(controller, never()).initPager(anyInt());
        verify(presenter, never()).setPager(any(Widget.class));
    }

    @Test
    public void shouldInitModule_withTemplate() {
        // given
        Element element = mock(Element.class);

        SlideshowBean slideshowBean = new SlideshowBean();
        List<SlideBean> slides = Lists.newArrayList();
        slideshowBean.setSlideBeans(slides);

        SlideshowPlayerBean slideshowPlayer = new SlideshowPlayerBean();
        slideshowPlayer.setSlideshowBean(slideshowBean);

        when(moduleStructure.getBean()).thenReturn(slideshowPlayer);
        when(templateInterpreter.isPagerTemplateActivate(slideshowPlayer)).thenReturn(true);

        // when
        testObj.initModule(element, moduleSocket, eventsBus);

        // then
        verify(presenter).init(slideshowBean, inlineBodyGeneratorSocket);
        verify(controller).init(slideshowBean.getSlideBeans(), inlineBodyGeneratorSocket);
        verify(controller).initPager(slides.size());
        verify(presenter).setPager(any(Widget.class));
    }
}
