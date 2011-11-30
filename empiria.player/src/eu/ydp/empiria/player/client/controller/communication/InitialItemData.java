package eu.ydp.empiria.player.client.controller.communication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.xml.client.NodeList;
import eu.ydp.empiria.player.client.controller.variables.objects.outcome.Outcome;
import eu.ydp.empiria.player.client.util.xml.document.XMLData;

public class InitialItemData {

	protected Map<String, Outcome> outcomes;
	
	public InitialItemData(XMLData itemData){

		NodeList outcomeDeclarationNodes = null;
		
		if (itemData != null)
			outcomeDeclarationNodes = itemData.getDocument().getElementsByTagName("outcomeDeclaration");
		
		outcomes = new HashMap<String, Outcome>();
		
		Outcome currOutcome;
		for (int i = 0 ; i < outcomeDeclarationNodes.getLength() ; i ++){
			currOutcome = new Outcome(outcomeDeclarationNodes.item(i));
			if (currOutcome != null){
				outcomes.put(currOutcome.identifier, currOutcome);
			}
		}
	}
	
	public Map<String, Outcome> getOutcomes(){		
		return outcomes;
	}
	
}
