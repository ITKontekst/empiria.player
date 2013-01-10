package eu.ydp.empiria.player.client.module.connection;

import com.google.inject.Inject;
import com.google.inject.Provider;

import eu.ydp.empiria.player.client.gin.factory.ConnectionModuleFactory;
import eu.ydp.empiria.player.client.module.AbstractInteractionModule;
import eu.ydp.empiria.player.client.module.ActivityPresenter;
import eu.ydp.empiria.player.client.module.abstractmodule.structure.AbstractModuleStructure;
import eu.ydp.empiria.player.client.module.connection.presenter.ConnectionModulePresenter;
import eu.ydp.empiria.player.client.module.connection.structure.ConnectionModuleJAXBParser;
import eu.ydp.empiria.player.client.module.connection.structure.ConnectionModuleStructure;
import eu.ydp.empiria.player.client.module.connection.structure.MatchInteractionBean;

public class ConnectionModule extends AbstractInteractionModule<ConnectionModule, ConnectionModuleModel, MatchInteractionBean> {

	@Inject
	private ConnectionModulePresenter presenter;

	@Inject
	private ConnectionModuleStructure connectionStructure;

	@Inject
	protected Provider<ConnectionModule> moduleFactory;

	@Inject
	private ConnectionModuleFactory connectionModuleFactory;

	private ConnectionModuleModel connectionModel;

	@Override
	protected void initalizeModule() {
		connectionModel = connectionModuleFactory.getConnectionModuleModel(getResponse(), this);
		connectionModel.setResponseSocket(getModuleSocket());
		getResponse().setCountMode(getCountMode());
	}

	@Override
	public ConnectionModule getNewInstance() {
		return moduleFactory.get();
	}

	@Override
	protected ActivityPresenter<ConnectionModuleModel, MatchInteractionBean> getPresenter() {
		return presenter;
	}

	@Override
	protected ConnectionModuleModel getResponseModel() {
		return connectionModel;
	}

	@Override
	protected AbstractModuleStructure<MatchInteractionBean, ConnectionModuleJAXBParser> getStructure() {
		return connectionStructure;
	}

}
