package eu.ydp.empiria.player.client.module.img.picture.player.presenter;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Node;
import com.google.gwtmockito.GwtMockitoTestRunner;
import eu.ydp.empiria.player.client.controller.body.InlineBodyGeneratorSocket;
import eu.ydp.empiria.player.client.module.UserAgentCheckerWrapper;
import eu.ydp.empiria.player.client.module.img.picture.player.lightbox.LightBox;
import eu.ydp.empiria.player.client.module.img.picture.player.lightbox.LightBoxProvider;
import eu.ydp.empiria.player.client.module.img.picture.player.structure.PicturePlayerBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class PicturePlayerFullscreenControllerTest {

    @InjectMocks
    private PicturePlayerFullscreenController testObj;
    @Mock
    private LightBoxProvider modeProvider;
    @Mock
    private LightBox lightBox;
    @Mock
    private PicturePlayerFullscreenDelay fullscreenDelay;
    @Mock
    private UserAgentCheckerWrapper userAgentCheckerWrapper;
    @Mock
    private InlineBodyGeneratorSocket inlineBodyGeneratorSocket;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private PicturePlayerBean bean;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Widget titleElement;

    @Before
    public void setUp() throws Exception {
        when(modeProvider.getFullscreen(Mockito.<String>any())).thenReturn(lightBox);
        when(inlineBodyGeneratorSocket.generateInlineBody(Mockito.<Node>any())).thenReturn(titleElement);
    }

    @Test
    public void shouldOpenFullscreen_withoutDelay() {
        // given
        when(userAgentCheckerWrapper.isStackAndroidBrowser()).thenReturn(false);

        // when
        testObj.openFullscreen(bean, inlineBodyGeneratorSocket);

        // then
        verify(modeProvider).getFullscreen(Mockito.<String>any());
        verify(lightBox).openImage(bean.getSrcFullScreen(), titleElement);
    }

    @Test
    public void shouldOpenFullscreen_withDelay() {
        // given
        when(userAgentCheckerWrapper.isStackAndroidBrowser()).thenReturn(true);

        // when
        testObj.openFullscreen(bean, inlineBodyGeneratorSocket);

        // then
        verify(modeProvider).getFullscreen(Mockito.<String>any());
        verify(fullscreenDelay).openImageWithDelay(lightBox, bean.getSrcFullScreen(), titleElement);
    }
}
