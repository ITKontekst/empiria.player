package eu.ydp.empiria.player.client.module.identification;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

import eu.ydp.empiria.player.client.controller.events.internal.InternalEvent;
import eu.ydp.empiria.player.client.controller.events.internal.InternalEventTrigger;
import eu.ydp.empiria.player.client.controller.variables.objects.response.Response;
import eu.ydp.empiria.player.client.model.feedback.InlineFeedback;
import eu.ydp.empiria.player.client.module.IInteractionModule;
import eu.ydp.empiria.player.client.module.IMultiViewModule;
import eu.ydp.empiria.player.client.module.ModuleEventsListener;
import eu.ydp.empiria.player.client.module.JsSocketFactory;
import eu.ydp.empiria.player.client.module.ModuleInteractionListener;
import eu.ydp.empiria.player.client.module.ModuleSocket;
import eu.ydp.empiria.player.client.util.RandomizedSet;
import eu.ydp.empiria.player.client.util.xml.XMLUtils;

public class IdentificationModule extends Widget implements
		IInteractionModule, IMultiViewModule {
	
	private int maxSelections;
	private boolean locked;
	
	private String responseIdentifier;
	private Response response;
	
	private Vector<SelectableChoice> options;
	private FlowPanel panel;
	
	private ModuleInteractionListener moduleListener;
	protected ModuleSocket moduleSocket;
	
	protected List<Element> multiViewElements;
	
	public IdentificationModule(ModuleSocket moduleSocket, ModuleEventsListener moduleEventsListener){

		locked = false;
		multiViewElements = new ArrayList<Element>();
		
		moduleListener = moduleEventsListener;
		this.moduleSocket = moduleSocket;
	}


	@Override
	public void addElement(Element element) {
		multiViewElements.add(element);
	}

	@Override
	public void installViews(List<HasWidgets> placeholders) {
		
		options = new Vector<SelectableChoice>();
		
		for (int e = 0 ; e < multiViewElements.size()  &&  e < placeholders.size() ; e ++ ){
			
			Element element = multiViewElements.get(e);
			HasWidgets currPlaceholder = placeholders.get(e);
			Vector<SelectableChoice> currOptions = new Vector<SelectableChoice>();
			
			boolean shuffle = false;
			String separatorString = "/";
			
			if (e == 0){
		
				shuffle = XMLUtils.getAttributeAsBoolean(element, "shuffle");
				maxSelections = XMLUtils.getAttributeAsInt(element, "maxSelections");
		
				responseIdentifier = XMLUtils.getAttributeAsString(element, "responseIdentifier");
				response = moduleSocket.getResponse(responseIdentifier);
				
				separatorString = XMLUtils.getAttributeAsString(element, "separator");
			}
								
			RandomizedSet<SelectableChoice> optionsSet = new RandomizedSet<SelectableChoice>();
			Vector<Boolean> fixeds = new Vector<Boolean>();
			NodeList optionNodes = element.getElementsByTagName("simpleChoice");
			for (int i = 0 ; i < optionNodes.getLength() ; i ++ ){
				SelectableChoice sc = new SelectableChoice((Element)optionNodes.item(i));
				currOptions.add(sc);
				if(shuffle  &&  !XMLUtils.getAttributeAsBoolean((Element)optionNodes.item(i), "fixed")){
					optionsSet.push(sc);
					fixeds.add(false);
				} else 
					fixeds.add(true);
			}
					
			panel = new FlowPanel();
			panel.setStyleName("qp-identification-module");
			for (int i = 0 ; i < currOptions.size() ; i ++){
				if (fixeds.get(i))
					panel.add(currOptions.get(i));
				else
					panel.add(optionsSet.pull());
				if (i != currOptions.size()-1){
					Label sep = new Label(separatorString);
					sep.setStyleName("qp-identification-separator");
					panel.add(sep);
				}
			}
			
			currPlaceholder.add(panel);
			
			options.addAll(currOptions);
	
			NodeList childNodes = element.getChildNodes();
			for (int f = 0 ; f < childNodes.getLength() ; f ++){
				if (childNodes.item(f).getNodeName().compareTo("feedbackInline") == 0)
					moduleSocket.addInlineFeedback(new InlineFeedback(panel, childNodes.item(f), moduleListener));
			}
		}
	}
	

	@Override
	public String getIdentifier() {
		return responseIdentifier;
	}

	@Override
	public void lock(boolean l) {
		locked = l;
	}

	@Override
	public void markAnswers(boolean mark) {

		for (SelectableChoice currSC:options){
			currSC.markAnswers(mark, response.correctAnswers.contains(currSC.getIdentifier()) );
		}

	}

	@Override
	public void reset() {
		markAnswers(false);
		lock(false);
		for (int i = 0 ; i < options.size() ; i ++){
			options.get(i).setSelected(false);
		}
		updateResponse(false);

	}

	@Override
	public void showCorrectAnswers(boolean show) {
		// TODO Auto-generated method stub

	}
		
	public JavaScriptObject getJsSocket(){
		return JsSocketFactory.createSocketObject(this);
	}

	@Override
	public JSONArray getState() {
		
		JSONArray arr = new JSONArray();
		for (int i = 0 ; i < options.size() ; i ++){
			arr.set(i, JSONBoolean.getInstance(options.get(i).getSelected()));
		}
		
		return arr;
	}

	@Override
	public void setState(JSONArray newState) {
		
		for (int i = 0 ; i < options.size() ; i ++){
			JSONValue value =  newState.get(i);
			if (value != null)
				options.get(i).setSelected(value.isBoolean().booleanValue());
		}
		updateResponse(false);
	}

	@Override
	public Vector<InternalEventTrigger> getTriggers() {
		Vector<InternalEventTrigger> triggers = new Vector<InternalEventTrigger>();
		for (int i = 0 ; i < options.size() ; i ++)
			triggers.add(new InternalEventTrigger(options.get(i).getId(), Event.ONMOUSEUP));
		return triggers;
	}

	@Override
	public void handleEvent(String tagID, InternalEvent event) {
		
		if (locked)
			return;
		
		int currTarget = -1;
		
		for (int i = 0 ; i < options.size() ; i ++){
			if (options.get(i).getId().compareTo(tagID) == 0){
				options.get(i).setSelected(!options.get(i).getSelected());
				currTarget = i;
				break;
			}
		}
		
		int currSelectionsCount = 0;
		for (int i = 0 ; i < options.size() ; i ++){
			if (options.get(i).getSelected())
				currSelectionsCount++;
		}
		
		if (currSelectionsCount > maxSelections){
			for (int i = 0 ; i < options.size() ; i ++){
				if (options.get(i).getSelected()  &&  currTarget != i){
					options.get(i).setSelected(false);
					break;
				}
			}
		}

		updateResponse(true);
	}

	@Override
	public void onOwnerAttached() {
		for (SelectableChoice currSC:options)
			currSC.onOwnerAttached();
		updateResponse(false);
	}
	

	private void updateResponse(boolean userInteract){
		Vector<String> currResponseValues = new Vector<String>();
		
		for (SelectableChoice currSC:options){
			if (currSC.getSelected())
				currResponseValues.add(currSC.getIdentifier());
		}
		
		if (!response.compare(currResponseValues)  ||  !response.isInitialized()){
			response.set(currResponseValues);
			moduleListener.onStateChanged(userInteract, this);
		}
	}

}