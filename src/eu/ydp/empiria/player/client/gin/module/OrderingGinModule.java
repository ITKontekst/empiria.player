package eu.ydp.empiria.player.client.gin.module;

import com.google.gwt.inject.client.AbstractGinModule;

import eu.ydp.empiria.player.client.module.ordering.presenter.OrderInteractionPresenter;
import eu.ydp.empiria.player.client.module.ordering.presenter.OrderInteractionPresenterImpl;
import eu.ydp.empiria.player.client.module.ordering.view.OrderInteractionView;
import eu.ydp.empiria.player.client.module.ordering.view.OrderInteractionViewImpl;

public class OrderingGinModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(OrderInteractionPresenter.class).to(OrderInteractionPresenterImpl.class);
		bind(OrderInteractionView.class).to(OrderInteractionViewImpl.class);
	}

}
