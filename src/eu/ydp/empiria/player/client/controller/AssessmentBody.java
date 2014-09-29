package eu.ydp.empiria.player.client.controller;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import eu.ydp.empiria.player.client.controller.body.BodyGenerator;
import eu.ydp.empiria.player.client.controller.body.ModulesInstalator;
import eu.ydp.empiria.player.client.controller.body.ParenthoodManager;
import eu.ydp.empiria.player.client.controller.communication.DisplayContentOptions;
import eu.ydp.empiria.player.client.controller.events.interaction.InteractionEventsListener;
import eu.ydp.empiria.player.client.controller.events.widgets.WidgetWorkflowListener;
import eu.ydp.empiria.player.client.controller.extensions.internal.workmode.PlayerWorkModeService;
import eu.ydp.empiria.player.client.module.*;
import eu.ydp.empiria.player.client.module.containers.AssessmentBodyModule;
import eu.ydp.empiria.player.client.module.pageinpage.PageInPageModule;
import eu.ydp.empiria.player.client.module.registry.ModulesRegistrySocket;
import eu.ydp.empiria.player.client.module.workmode.WorkModeClient;
import eu.ydp.empiria.player.client.module.workmode.WorkModeClientType;
import eu.ydp.empiria.player.client.resources.StyleNameConstants;

import java.util.List;

public class AssessmentBody implements WidgetWorkflowListener, WorkModeClient {

	protected DisplayContentOptions options;
	protected ModuleSocket moduleSocket;
	protected ModulesRegistrySocket modulesRegistrySocket;
	protected InteractionEventsListener interactionEventsListener;
	protected Panel pageSlot;
	protected ParenthoodManager parenthood;
	protected List<IModule> modules;
	private final PlayerWorkModeService playerWorkModeService;
	private final StyleNameConstants styleNameConstants;

	@Inject
	public AssessmentBody(@Assisted DisplayContentOptions options, @Assisted ModuleSocket moduleSocket,
			@Assisted final InteractionEventsListener interactionEventsListener, @Assisted ModulesRegistrySocket modulesRegistrySocket,
			PlayerWorkModeService playerWorkModeService, StyleNameConstants styleNameConstants) {
		this.options = options;
		this.moduleSocket = moduleSocket;
		this.modulesRegistrySocket = modulesRegistrySocket;
		this.interactionEventsListener = interactionEventsListener;
		this.playerWorkModeService = playerWorkModeService;
		this.styleNameConstants = styleNameConstants;

		parenthood = new ParenthoodManager();
	}

	public Widget init(Element assessmentBodyElement) {

		ModulesInstalator instalator = new ModulesInstalator(parenthood, modulesRegistrySocket, moduleSocket, interactionEventsListener);
		BodyGenerator generator = new BodyGenerator(instalator, options);

		AssessmentBodyModule bodyModule = new AssessmentBodyModule();
		instalator.setInitialParent(bodyModule);
		bodyModule.initModule(assessmentBodyElement, generator);

		modules = instalator.getInstalledSingleViewModules();

		pageSlot = findPageInPage();
		playerWorkModeService.registerModule(this);

		return bodyModule.getView();
	}

	public Panel getPageSlot() {
		return pageSlot;
	}

	private Panel findPageInPage() {
		Panel pagePanel = null;

		for (IModule module : modules) {
			if (module instanceof PageInPageModule) {
				pagePanel = (Panel) ((PageInPageModule) module).getView();
				break;
			}
		}

		return pagePanel;
	}

	public HasChildren getModuleParent(IModule module) {
		return parenthood.getParent(module);
	}

	public List<IModule> getModuleChildren(IModule parent) {
		return parenthood.getChildren(parent);
	}

	public ParenthoodSocket getParenthoodSocket() {
		return moduleSocket;
	}

	@Override
	public void onLoad() {
		for (IModule currModule : modules) {
			if (currModule instanceof ILifecycleModule) {
				((ILifecycleModule) currModule).onBodyLoad();
			}
		}
	}

	@Override
	public void onUnload() {
		for (IModule currModule : modules) {
			if (currModule instanceof ILifecycleModule) {
				((ILifecycleModule) currModule).onBodyUnload();
			}
		}
	}

	public void setUp() {
		for (IModule currModule : modules) {
			if (currModule instanceof ILifecycleModule) {
				((ILifecycleModule) currModule).onSetUp();
			}
			workModeProceeding(currModule);
		}
	}

	private void workModeProceeding(IModule currModule) {
		if (currModule instanceof WorkModeClientType) {
			playerWorkModeService.registerModule((WorkModeClientType) currModule);
		}
	}

	public void start() {
		for (IModule currModule : modules) {
			if (currModule instanceof ILifecycleModule) {
				((ILifecycleModule) currModule).onStart();
			}
		}
	}

	public void close() {
		for (IModule currModule : modules) {
			if (currModule instanceof ILifecycleModule) {
				((ILifecycleModule) currModule).onClose();
			}
		}
	}

	public ParenthoodManager getParenthood() {
		return parenthood;
	}

	@Override
	public void enablePreviewMode() {
		pageSlot.addStyleName(styleNameConstants.QP_MODULE_MODE_PREVIEW());
	}

	@Override
	public void enableTestMode() {
		pageSlot.addStyleName(styleNameConstants.QP_MODULE_MODE_TEST());
	}

	@Override
	public void disableTestMode() {
		pageSlot.removeStyleName(styleNameConstants.QP_MODULE_MODE_TEST());
	}

	@Override
	public void enableTestSubmittedMode() {
		pageSlot.addStyleName(styleNameConstants.QP_MODULE_MODE_TEST_SUBMITTED());
	}

	@Override
	public void disableTestSubmittedMode() {
		pageSlot.removeStyleName(styleNameConstants.QP_MODULE_MODE_TEST_SUBMITTED());
	}
}
