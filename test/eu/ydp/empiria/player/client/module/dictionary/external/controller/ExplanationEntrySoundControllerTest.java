package eu.ydp.empiria.player.client.module.dictionary.external.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.gwt.thirdparty.guava.common.collect.Maps;
import com.google.gwt.user.client.ui.Widget;

import eu.ydp.empiria.player.client.module.dictionary.external.MediaWrapperController;
import eu.ydp.empiria.player.client.module.dictionary.external.MimeSourceProvider;
import eu.ydp.empiria.player.client.module.dictionary.external.model.Entry;
import eu.ydp.empiria.player.client.module.media.MediaWrapper;
import eu.ydp.empiria.player.client.util.events.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.callback.CallbackRecevier;
import eu.ydp.empiria.player.client.util.events.player.PlayerEvent;
import eu.ydp.empiria.player.client.util.events.player.PlayerEventTypes;

@RunWith(MockitoJUnitRunner.class)
public class ExplanationEntrySoundControllerTest {

	@InjectMocks
	private ExplanationEntrySoundController testObj;

	@Mock
	private MimeSourceProvider mimeSourceProvider;
	@Mock
	private EventsBus eventsBus;
	@Mock
	private MediaWrapperController mediaWrapperController;
	@Mock
	private MediaWrapper<Widget> mediaWrapper;
	@Mock
	private Entry entry;

	@Captor
	private ArgumentCaptor<PlayerEvent> playerEventCaptor;

	private CallbackRecevier<MediaWrapper<Widget>> callbackRecevier;

	private static final String FILE_NAME = "test.mp3";

	@Before
	public void setUp() {
		when(entry.getEntrySound()).thenReturn(FILE_NAME);
	}

	@Test
	public void shouldFireCreateMediaWrapperPlayerEvent() {
		// given
		Map<String, String> sourcesWithTypes = Maps.newHashMap();
		when(mimeSourceProvider.getSourcesWithTypes(FILE_NAME)).thenReturn(sourcesWithTypes);

		// when
		testObj.playEntrySound(entry);

		// then
		verify(eventsBus).fireEvent(playerEventCaptor.capture());
		PlayerEvent resultEvent = playerEventCaptor.getValue();
		assertEquals(PlayerEventTypes.CREATE_MEDIA_WRAPPER, resultEvent.getType());
	}

	@Test
	public void shouldPlayMediaWrapperOnCallback() {
		// given
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) {
				PlayerEvent plEvent = (PlayerEvent) invocation.getArguments()[0];
				callbackRecevier = (CallbackRecevier<MediaWrapper<Widget>>) plEvent.getSource();
				return null;
			}
		}).when(eventsBus).fireEvent(any(PlayerEvent.class));

		testObj.playEntrySound(entry);
		callbackRecevier.setCallbackReturnObject(mediaWrapper);

		// then
		InOrder inOrder = inOrder(mediaWrapperController);
		inOrder.verify(mediaWrapperController).addMediaWrapperControls(mediaWrapper);
		inOrder.verify(mediaWrapperController).play(mediaWrapper);
	}
}