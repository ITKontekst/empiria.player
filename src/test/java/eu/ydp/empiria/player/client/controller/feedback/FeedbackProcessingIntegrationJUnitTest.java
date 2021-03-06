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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import eu.ydp.empiria.player.client.AbstractTestBaseWithoutAutoInjectorInit;
import eu.ydp.empiria.player.client.controller.body.InlineBodyGeneratorSocket;
import eu.ydp.empiria.player.client.controller.feedback.FeedbackPropertiesCollectorTestHelper.ModuleInfo;
import eu.ydp.empiria.player.client.controller.feedback.processor.SoundActionProcessor;
import eu.ydp.empiria.player.client.controller.feedback.structure.Feedback;
import eu.ydp.empiria.player.client.controller.feedback.structure.action.ActionType;
import eu.ydp.empiria.player.client.controller.feedback.structure.action.FeedbackAction;
import eu.ydp.empiria.player.client.controller.feedback.structure.action.ShowUrlAction;
import eu.ydp.empiria.player.client.controller.variables.objects.outcome.Outcome;
import eu.ydp.empiria.player.client.controller.variables.storage.item.ItemOutcomeStorageImpl;
import eu.ydp.empiria.player.client.gin.factory.FeedbackModuleFactory;
import eu.ydp.empiria.player.client.module.core.base.IModule;
import eu.ydp.empiria.player.client.module.core.base.IUniqueModule;
import eu.ydp.empiria.player.client.util.events.internal.bus.EventsBus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.util.List;
import java.util.Map;

import static eu.ydp.empiria.player.client.controller.variables.processor.results.model.LastMistaken.CORRECT;
import static eu.ydp.empiria.player.client.controller.variables.processor.results.model.LastMistaken.WRONG;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class FeedbackProcessingIntegrationJUnitTest extends AbstractTestBaseWithoutAutoInjectorInit {

    private static final String MODULE_1 = "+MODULE_1";

    private static final String WRONG_MP3 = "wrong.mp3";

    private static final String ALLOK_MP3 = "allok.mp3";

    private static final String GOOD_MP3 = "good.mp3";

    private IModule sender;

    private Map<String, Outcome> variables;

    @Before
    @Override
    public void setUp() {
        setUp(new Class<?>[]{FeedbackRegistry.class, FeedbackActionCollector.class, SoundActionProcessor.class, FeedbackConditionMatcher.class,
                FeedbackPropertiesCollector.class}, new ProcessingModule());
    }

    @Test
    public void shouldProcessOkFeedback() {
        ModuleInfo info = ModuleInfo.create(MODULE_1).setLastOk(CORRECT).setDone(1).setTodo(3).setErrors(0);
        List<FeedbackAction> actions = processUserAction(info);

        assertThat(actions.size(), is(equalTo(1)));
        FeedbackAction action = actions.get(0);
        assertUrlAction(action, ActionType.NARRATION, GOOD_MP3);
    }

    @Test
    public void shouldIgnoreFeedbackBecauseIsOnUnselect() {
        ModuleInfo info = ModuleInfo.create(MODULE_1).setLastOk(CORRECT).setDone(1).setTodo(3).setErrors(0).setLastChange("-");
        List<FeedbackAction> actions = processUserAction(info);

        assertThat(actions.size(), is(equalTo(0)));
    }

    @Test
    public void shouldProcessWrongFeedback() {
        ModuleInfo info = ModuleInfo.create(MODULE_1).setLastOk(WRONG).setDone(1).setTodo(3).setErrors(0);
        List<FeedbackAction> actions = processUserAction(info);

        assertThat(actions.size(), is(equalTo(1)));
        FeedbackAction action = actions.get(0);
        assertUrlAction(action, ActionType.NARRATION, WRONG_MP3);
    }

    @Test
    public void shouldProcessAllOkFeedback() {
        ModuleInfo info = ModuleInfo.create(MODULE_1).setLastOk(CORRECT).setDone(3).setTodo(3).setErrors(0);
        List<FeedbackAction> actions = processUserAction(info);

        assertThat(actions.size(), is(equalTo(2)));
        String[] expectedUrls = new String[]{GOOD_MP3, ALLOK_MP3};
        int index = 0;

        for (String expectedUrl : expectedUrls) {
            assertUrlAction(actions.get(index++), ActionType.NARRATION, expectedUrl);
        }
    }

    @Test
    public void shouldProcessOkFeedbackWhen_allAreDoneWithOneError() {
        ModuleInfo info = ModuleInfo.create(MODULE_1).setLastOk(CORRECT).setDone(3).setTodo(3).setErrors(1);
        List<FeedbackAction> actions = processUserAction(info);

        assertThat(actions.size(), is(equalTo(1)));
        assertUrlAction(actions.get(0), ActionType.NARRATION, GOOD_MP3);
    }

    @Test
    public void shouldProcessWrongFeedbackWhen_allAreDoneWithOneError() {
        ModuleInfo info = ModuleInfo.create(MODULE_1).setLastOk(WRONG).setDone(3).setTodo(3).setErrors(1);
        List<FeedbackAction> actions = processUserAction(info);

        assertThat(actions.size(), is(equalTo(1)));
        assertUrlAction(actions.get(0), ActionType.NARRATION, WRONG_MP3);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<FeedbackAction> processUserAction(ModuleInfo info) {
        sender = createSender(info);
        ModuleFeedbackProcessor processor = getProcessor();
        FeedbackRegistry feedbackRegistry = injector.getInstance(FeedbackRegistry.class);

        when(feedbackRegistry.hasFeedbacks()).thenReturn(true);

        ItemOutcomeStorageImpl outcomeStorage = new ItemOutcomeStorageImpl();
        outcomeStorage.importFromMap(variables);
        processor.processFeedbacks(outcomeStorage, (IUniqueModule) sender);

        ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
        verify(processor.soundProcessor, times(1)).processActions(argument.capture(), Matchers.isA(InlineBodyGeneratorSocket.class), Matchers.isA(FeedbackMark.class));

        return argument.getValue();
    }

    private void assertUrlAction(FeedbackAction actualAction, ActionType expectedType, String expectedUrl) {
        assertThat(actualAction, is(instanceOf(ShowUrlAction.class)));
        ShowUrlAction showUrlAction = (ShowUrlAction) actualAction;
        assertThat(showUrlAction.getType(), is(equalTo(expectedType.getName())));
        assertThat(showUrlAction.getHref(), is(equalTo(expectedUrl)));
    }

    private IModule createSender(ModuleInfo info) {
        FeedbackPropertiesCollectorTestHelper helper = new FeedbackPropertiesCollectorTestHelper();
        variables = helper.createOutcomeVariables(info);
        return helper.createUniqueModuleMock(null, info.getId(), variables);
    }

    private ModuleFeedbackProcessor getProcessor() {
        InlineBodyGeneratorSocket inlineBodyGeneratorSocket = mock(InlineBodyGeneratorSocket.class);
        FeedbackModuleFactory feedbackModuleFactory = injector.getInstance(FeedbackModuleFactory.class);
        return feedbackModuleFactory.getModuleFeedbackProcessor(inlineBodyGeneratorSocket);
    }

    private class ProcessingModule implements Module {

        private FeedbackRegistry feedbackRegistry = null;

        @Override
        public void configure(Binder binder) {
        }

        @Provides
        public FeedbackRegistry getFeedbackRegistry() {
            if (feedbackRegistry == null) {
                FeedbackRegistry registry = mock(FeedbackRegistry.class);
                List<Feedback> feedbackList = new FeedbackCreator(GOOD_MP3, WRONG_MP3, ALLOK_MP3).createFeedbackList();

                when(registry.isModuleRegistered(sender)).thenReturn(true);
                when(registry.getModuleFeedbacks(sender)).thenReturn(feedbackList);
                this.feedbackRegistry = registry;
            }

            return this.feedbackRegistry;
        }

        @Provides
        public SoundActionProcessor getSoundActionProcessor(EventsBus eventsBus) {
            SoundActionProcessor processor = new SoundActionProcessor(eventsBus);
            injector.injectMembers(processor);
            return spy(processor);
        }
    }
}
