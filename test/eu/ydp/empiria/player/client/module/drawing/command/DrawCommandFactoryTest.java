package eu.ydp.empiria.player.client.module.drawing.command;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.inject.Provider;

@RunWith(MockitoJUnitRunner.class)
public class DrawCommandFactoryTest {
	@Mock private Provider<ClearAllDrawCommand> clearAllComandProvider;
	@Mock private ClearAllDrawCommand command;
	@InjectMocks private DrawCommandFactory instance;

	@Before
	public void before() {
		doReturn(command).when(clearAllComandProvider).get();
	}

	@Test
	public void createClearAllCommand() throws Exception {
		DrawCommand drawCommand = instance.createCommand(DrawCommandType.CLEAR_ALL);
		assertThat(drawCommand).isNotNull();
		assertThat(drawCommand).isInstanceOf(ClearAllDrawCommand.class);
		assertThat(drawCommand).isEqualTo(command);
		verify(clearAllComandProvider).get();
	}

	@Test
	public void createOtherCommand() throws Exception {
		ArrayList<DrawCommandType> allComandsType = Lists.newArrayList(DrawCommandType.values());
		allComandsType.remove(DrawCommandType.CLEAR_ALL);

		for(DrawCommandType type : allComandsType){
			instance.createCommand(type);
			verifyZeroInteractions(clearAllComandProvider);
		}
		DrawCommand drawCommand = instance.createCommand(null);
		assertThat(drawCommand).isNull();
		verifyZeroInteractions(clearAllComandProvider);
	}

}
