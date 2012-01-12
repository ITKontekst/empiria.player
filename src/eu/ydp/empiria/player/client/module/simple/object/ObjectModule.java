package eu.ydp.empiria.player.client.module.simple.object;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;

import eu.ydp.empiria.player.client.module.ISimpleModule;
import eu.ydp.empiria.player.client.module.ModuleSocket;
import eu.ydp.empiria.player.client.module.listener.ModuleInteractionListener;
import eu.ydp.empiria.player.client.module.simple.object.impl.AudioImpl;
import eu.ydp.empiria.player.client.module.simple.object.impl.VideoImpl;
import eu.ydp.empiria.player.client.util.xml.XMLUtils;

public class ObjectModule implements ISimpleModule {

	protected HTML htmlWidget;
	
	@Override
	public Widget getView() {
		return htmlWidget;
	}

	@Override
	public void initModule(Element element, ModuleSocket ms, ModuleInteractionListener mil) {
		
		  String html;
		  String src = XMLUtils.getAttributeAsString(element, "data");
		  String type = XMLUtils.getAttributeAsString(element, "type");
		  
		  if(type.startsWith("video")){

		    VideoImpl video = GWT.create(VideoImpl.class);
	  		html = video.getHTML(src);
		  }
		  else{
		    
	      AudioImpl audio = GWT.create(AudioImpl.class);
	      html = audio.getHTML(src);
		  }
			
		  htmlWidget = new HTML(html);
		
	}

}