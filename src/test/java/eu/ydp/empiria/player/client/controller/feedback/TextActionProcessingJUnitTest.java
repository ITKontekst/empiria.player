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

package eu.ydp.empiria.player.client.controller.feedback;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.peterfranza.gwt.jaxb.client.parser.utils.XMLContent;
import eu.ydp.empiria.player.client.controller.feedback.structure.action.ActionType;
import eu.ydp.empiria.player.client.controller.feedback.structure.action.FeedbackAction;
import eu.ydp.empiria.player.client.controller.feedback.structure.action.ShowTextAction;
import eu.ydp.empiria.player.client.jaxb.XmlContentMock;
import eu.ydp.empiria.player.client.module.core.base.HasChildren;
import eu.ydp.empiria.player.client.module.core.base.IModule;
import eu.ydp.empiria.player.client.controller.feedback.processor.TextActionProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class TextActionProcessingJUnitTest extends ProcessingFeedbackActionTestBase {

    private TextActionProcessor textProcessor;

    @Test
    public void shouldProcessSingleTextAction() {
        // given
        List<FeedbackAction> actions = ActionListBuilder.create()
                .addUrlAction(ActionType.NARRATION, "good.mp3")
                .addTextAction(new XmlContentMock("Good"))
                .getList();

        initializeWithActions(actions);
        initializeModuleHierarchyWithTextProcessor();

        // when
        processor.processActions(source);

        // then
        ArgumentCaptor<FeedbackAction> argument = ArgumentCaptor.forClass(FeedbackAction.class);
        verify(textProcessor).processSingleAction(argument.capture(), eq(FeedbackMark.OK));
        FeedbackAction processedAction = argument.getValue();

        assertThat(argument.getAllValues().size(), is(equalTo(1)));
        assertThat(processedAction, is(instanceOf(ShowTextAction.class)));
        assertThat(((ShowTextAction) processedAction).getContent().toString(), is(equalTo("Good")));
        assertThat(collector.getActions().size(), is(equalTo(0)));

        verify(mathJaxNative).renderMath(Mockito.isA(JavaScriptObject.class));
    }

    @Test
    public void shouldProcessManyTextActions() {
        // given
        XMLContent[] actionTexts = new XMLContent[]{new XmlContentMock("Good"), new XmlContentMock("Very good!!!")};

        List<FeedbackAction> actions = ActionListBuilder.create()
                .addUrlAction(ActionType.NARRATION, "good.mp3")
                .addTextAction(actionTexts[0])
                .addUrlAction(ActionType.NARRATION, "allok.mp3")
                .addTextAction(actionTexts[1])
                .getList();

        initializeWithActions(actions);
        initializeModuleHierarchyWithTextProcessor();

        // when
        processor.processActions(source);

        // then
        ArgumentCaptor<FeedbackAction> argument = ArgumentCaptor.forClass(FeedbackAction.class);
        verify(textProcessor, times(2)).processSingleAction(argument.capture(), eq(FeedbackMark.OK));

        verify(mathJaxNative, times(2)).renderMath(Mockito.isA(JavaScriptObject.class));

        assertThat(collector.getActions().size(), is(equalTo(0)));
        List<FeedbackAction> processedActions = argument.getAllValues();

        for (int i = 0; i < actionTexts.length; i++) {
            FeedbackAction processedAction = processedActions.get(i);
            String actionText = actionTexts[i].toString();

            assertThat(processedAction, is(instanceOf(ShowTextAction.class)));
            assertThat(((ShowTextAction) processedAction).getContent().toString(), is(equalTo(actionText)));
        }
    }

    private void initializeModuleHierarchyWithTextProcessor() {
        HasChildren parentModule = mock(HasChildren.class);
        textProcessor = spy(injector.getInstance(TextActionProcessor.class));

        when(source.getParentModule()).thenReturn(parentModule);
        when(parentModule.getChildren()).thenReturn(Lists.newArrayList(mock(IModule.class), mock(IModule.class), source, textProcessor));
    }

}
