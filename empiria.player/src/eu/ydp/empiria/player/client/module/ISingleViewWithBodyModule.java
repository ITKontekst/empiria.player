package eu.ydp.empiria.player.client.module;

import com.google.gwt.xml.client.Element;

import eu.ydp.empiria.player.client.controller.body.BodyGeneratorSocket;
import eu.ydp.empiria.player.client.controller.events.interaction.InteractionEventsListener;

public interface ISingleViewWithBodyModule extends ISingleViewModule {

	public void initModule(Element element, ModuleSocket ms, InteractionEventsListener iel, BodyGeneratorSocket bgs);

}
