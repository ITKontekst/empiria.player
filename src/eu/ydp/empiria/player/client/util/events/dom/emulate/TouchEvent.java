package eu.ydp.empiria.player.client.util.events.dom.emulate;

import com.google.gwt.dom.client.NativeEvent;

import eu.ydp.empiria.player.client.util.events.AbstractEvent;
import eu.ydp.empiria.player.client.util.events.EventTypes;

/**
 * emulacja zdarzen touch
 *
 */
public class TouchEvent extends AbstractEvent<TouchHandler, TouchTypes> {
	public static EventTypes<TouchHandler, TouchTypes> types = new EventTypes<TouchHandler, TouchTypes>();

	private final NativeEvent nativeEvent;

	public TouchEvent(TouchTypes type, NativeEvent nativeEvent) {
		super(type, nativeEvent);
		this.nativeEvent = nativeEvent;
	}

	@Override
	protected EventTypes<TouchHandler, TouchTypes> getTypes() {
		return types;
	}

	@Override
	public void dispatch(TouchHandler handler) {
		handler.onTouchEvent(this);
	}

	public static Type<TouchHandler, TouchTypes> getType(TouchTypes type) {
		return types.getType(type);
	}

	public static Type<TouchHandler, TouchTypes>[] getTypes(TouchTypes... typeList) {
		return typeList.length > 0 ? types.getTypes(typeList) : new Type[0];

	}

	public NativeEvent getNativeEvent() {
		return nativeEvent;
	}

	public void preventDefault() {
		nativeEvent.preventDefault();
	}

}
