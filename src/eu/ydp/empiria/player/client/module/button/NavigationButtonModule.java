package eu.ydp.empiria.player.client.module.button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import eu.ydp.empiria.player.client.controller.events.delivery.DeliveryEvent;
import eu.ydp.empiria.player.client.module.ControlModule;
import eu.ydp.empiria.player.client.module.ISimpleModule;
import eu.ydp.empiria.player.client.module.workmode.WorkModeTestClient;
import eu.ydp.empiria.player.client.util.events.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.player.PlayerEvent;
import eu.ydp.empiria.player.client.util.events.player.PlayerEventHandler;
import eu.ydp.empiria.player.client.util.events.scope.CurrentPageScope;
import eu.ydp.gwtutil.client.ui.button.CustomPushButton;

import static eu.ydp.empiria.player.client.util.events.player.PlayerEventTypes.*;

public class NavigationButtonModule extends ControlModule implements ISimpleModule, PlayerEventHandler, WorkModeTestClient {

	private PushButton button;
	private boolean enabled = true;
	private boolean testMode = false;
	private final NavigationButtonDirection direction;

	protected EventsBus eventsBus;

	@Inject
	public NavigationButtonModule(@Assisted NavigationButtonDirection dir, EventsBus eventsBus) {
		direction = dir;
		this.eventsBus = eventsBus;
	}

	@Override
	public void initModule(Element element) {
		eventsBus.addHandler(PlayerEvent.getTypes(PAGE_LOADED, BEFORE_FLOW, PAGE_CHANGE), this, new CurrentPageScope());
	}

	private boolean isFirstPage() {
		return (flowDataSupplier.getCurrentPageIndex() == 0);
	}

	private boolean isEnd() {
		boolean retValue = false;
		if (direction.equals(NavigationButtonDirection.PREVIOUS)) {
			retValue = isFirstPage();
		} else if (direction.equals(NavigationButtonDirection.NEXT)) {
			retValue = isLastPage();
		}
		return retValue;
	}

	private void setStyleName() {
		String currentStyleName = getCurrentStyleName(isEnabled());
		if (currentStyleName != null) {
			button.setStylePrimaryName(currentStyleName);
		}
	}

	@Override
	public void onDeliveryEvent(DeliveryEvent flowEvent) {
		switch (flowEvent.getType()) {
		case ASSESSMENT_STARTED:
		case CONTINUE:
		case CHECK:
		case SHOW_ANSWERS:
			setEnabled(!isEnd() && !isTestMode());
		case TEST_PAGE_LOADED:
			setStyleName();
			break;
		default:
			break;
		}
	}

	public void setEnabled(boolean isEnabled) {
		this.enabled = isEnabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Widget getView() {
		if (button == null) {
			button = new CustomPushButton();
			button.setStyleName(getStyleName());
			button.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (isEnabled() && !isEnd()) {
						flowRequestInvoker.invokeRequest(direction.getRequest());
					}
				}
			});
		}

		return button;
	}

	private String getStyleName() {
		return "qp-" + direction.getName() + "-button";
	}

	private Boolean isLastPage() {
		return (flowDataSupplier.getCurrentPageIndex() == dataSourceSupplier.getItemsCount() - 1);
	}

	private String getCurrentStyleName(Boolean isEnabled) {
		String styleName = null;

		if (isEnabled) {
			styleName = getStyleName();
		} else {
			styleName = getStyleName() + "-disabled";
		}

		return styleName;
	}

	@Override
	public void onPlayerEvent(PlayerEvent event) {
		if (isTestMode()) {
			return;
		}

		if (event.getType() == PAGE_LOADED) {
			Timer timer = new Timer() {
				@Override
				public void run() {
					setEnabled(true && !isEnd());
					setStyleName();
				}
			};
			timer.schedule(300);
		} else if (event.getType() == BEFORE_FLOW) {
			setEnabled(false);
			setStyleName();
		}
	}

	@Override
	public void enableTestMode() {
		setEnabled(false);
		testMode = true;
	}

	@Override
	public void disableTestMode() {
		setEnabled(true);
		testMode = false;
	}

	private boolean isTestMode() {
		return testMode;
	}
}
