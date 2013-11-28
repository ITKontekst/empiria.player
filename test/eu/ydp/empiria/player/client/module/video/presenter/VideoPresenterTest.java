package eu.ydp.empiria.player.client.module.video.presenter;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.ydp.empiria.player.client.module.video.hack.ReAttachVideoPlayerForIOSChecker;
import eu.ydp.empiria.player.client.module.video.hack.ReAttachVideoPlayerForIOSHack;
import eu.ydp.empiria.player.client.module.video.view.VideoView;

@RunWith(MockitoJUnitRunner.class)
public class VideoPresenterTest {

	@InjectMocks
	private VideoPresenter presenter;
	@Mock
	private ReAttachVideoPlayerForIOSChecker hackChecker;
	@Mock
	private ReAttachVideoPlayerForIOSHack reAttachHack;
	@Mock
	private VideoPlayerAttacher videoPlayerAttacher;
	@Mock
	private VideoView view;

	@Test
	public void shouldCreateViewAndAttachPlayerWhenStart() {
		// given
		when(hackChecker.isNeeded()).thenReturn(false);

		// when
		presenter.start();

		// then
		verify(view).createView();
		verify(videoPlayerAttacher).attachNew();
	}

	@Test
	public void shouldCreateViewAndApplyHackWhenStart() {
		// given
		when(hackChecker.isNeeded()).thenReturn(true);

		// when
		presenter.start();

		// then
		verify(view).createView();
		verify(videoPlayerAttacher).attachNew();
		verify(reAttachHack).apply();
	}
}