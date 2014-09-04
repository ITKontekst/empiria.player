package eu.ydp.empiria.player.client.util.events.dom.emulate.handlers.pointer;

import eu.ydp.empiria.player.client.util.events.dom.emulate.handlers.touchon.TouchOnEndHandler;
import eu.ydp.empiria.player.client.util.events.dom.emulate.iepointer.events.PointerUpEvent;
import eu.ydp.empiria.player.client.util.events.dom.emulate.iepointer.handlers.PointerUpHandler;

public class PointerUpHandlerImpl implements PointerUpHandler {

	private final TouchOnEndHandler touchOnEndHandler;

	public PointerUpHandlerImpl(TouchOnEndHandler touchOnEndHandler) {
		this.touchOnEndHandler = touchOnEndHandler;
	}

	@Override
	public void onPointerUp(PointerUpEvent event) {
		if (event.isTouchEvent()) {
			touchOnEndHandler.onEnd(event.getNativeEvent());
		}
	}

}