package eu.ydp.empiria.player.client.module;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.xml.client.Element;

import eu.ydp.empiria.player.client.controller.body.BodyGeneratorSocket;

public interface IContainerModule extends ISingleViewModule {

	public void initModule(Element element, ModuleSocket ms, BodyGeneratorSocket bgs);

	public HasWidgets getContainer();
}