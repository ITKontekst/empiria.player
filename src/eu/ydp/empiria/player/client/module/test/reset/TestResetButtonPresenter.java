package eu.ydp.empiria.player.client.module.test.reset;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import eu.ydp.empiria.player.client.controller.extensions.internal.workmode.PlayerWorkMode;
import eu.ydp.empiria.player.client.controller.extensions.internal.workmode.PlayerWorkModeService;
import eu.ydp.empiria.player.client.controller.flow.FlowManager;
import eu.ydp.empiria.player.client.controller.flow.request.FlowRequest;
import eu.ydp.empiria.player.client.module.test.reset.view.TestResetButtonView;
import eu.ydp.gwtutil.client.event.factory.Command;

public class TestResetButtonPresenter {

	private final TestResetButtonView testResetButtonView;
	private final FlowManager flowManager;
	private final PlayerWorkModeService playerWorkModeService;
	private boolean enabled;

	@Inject
	public TestResetButtonPresenter(TestResetButtonView testResetButtonView, FlowManager flowManager, PlayerWorkModeService playerWorkModeService) {
		this.testResetButtonView = testResetButtonView;
		this.flowManager = flowManager;
		this.playerWorkModeService = playerWorkModeService;
	}

	public void bindUi() {
		testResetButtonView.addHandler(new Command() {
			@Override
			public void execute(NativeEvent event) {
				if (!enabled) {
					updateWorkModeTestSubmitted();
					navigateToFirstItem();
				}
			}

		});
	}

	public Widget getView() {
		return testResetButtonView.asWidget();
	}

	public void enable() {
		enabled = true;
		testResetButtonView.lock();
	}

	public void disable() {
		enabled = false;
		testResetButtonView.unlock();
	}

	public void enablePreviewMode() {
		enable();
		testResetButtonView.enablePreviewMode();
	}

	private void updateWorkModeTestSubmitted() {
		playerWorkModeService.updateWorkMode(PlayerWorkMode.TEST_SUBMITTED);
	}

	private void navigateToFirstItem() {
		flowManager.invokeFlowRequest(new FlowRequest.NavigateFirstItem());
	}
}
