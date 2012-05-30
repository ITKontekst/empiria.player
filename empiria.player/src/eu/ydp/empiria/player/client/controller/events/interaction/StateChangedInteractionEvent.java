package eu.ydp.empiria.player.client.controller.events.interaction;

import java.util.HashMap;
import java.util.Map;

import eu.ydp.empiria.player.client.module.IUniqueModule;

public class StateChangedInteractionEvent extends InteractionEvent {

	protected boolean userInteract;
	protected IUniqueModule sender;
	
	public StateChangedInteractionEvent(boolean userInteract, IUniqueModule sender){
		this.userInteract = userInteract;
		this.sender = sender;
	}

	public boolean isUserInteract() {
		return userInteract;
	}

	public IUniqueModule getSender() {
		return sender;
	}

	@Override
	public InteractionEventType getType() {
		return InteractionEventType.STATE_CHANGED;
	}

	@Override
	public Map<String, Object> getParams() {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("userInteract", Boolean.valueOf(userInteract));
		p.put("sender", sender);
		return p;
	}
}
