package eu.ydp.empiria.player.client.controller.extensions.jswrappers;

import com.google.gwt.core.client.JavaScriptObject;

import eu.ydp.empiria.player.client.controller.extensions.Extension;

public abstract class JsExtension extends Extension {

	protected JavaScriptObject extensionJsObject;

	public void initJs(JavaScriptObject extensionJsObject) {
		this.extensionJsObject = extensionJsObject;
	}

}