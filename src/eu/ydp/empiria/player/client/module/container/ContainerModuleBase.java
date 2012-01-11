package eu.ydp.empiria.player.client.module.container;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;

import eu.ydp.empiria.player.client.controller.body.BodyGeneratorSocket;
import eu.ydp.empiria.player.client.module.IContainerModule;
import eu.ydp.empiria.player.client.module.ModuleSocket;

public abstract class ContainerModuleBase implements IContainerModule {

	protected Panel panel;

	public ContainerModuleBase(){
		panel = new FlowPanel();
	}
	
	@Override
	public void initModule(Element element, ModuleSocket moduleSocket, BodyGeneratorSocket bodyGeneratorSocket) {
		bodyGeneratorSocket.generateBody(element, getContainer());

		String className = element.getAttribute("class");
		if (className != null  &&  !"".equals(className)  &&  getView() != null){
			getView().addStyleName(className);
		}
		String id = element.getAttribute("id");
		if (id != null  &&  !"".equals(id)  &&  getView() != null){
			getView().getElement().setId(id);
		}
		
	}
	
	@Override
	public Widget getView() {
		return panel;
	}

	@Override
	public HasWidgets getContainer() {
		return panel;
	}

}
