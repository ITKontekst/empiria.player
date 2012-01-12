package eu.ydp.empiria.player.client.controller.body;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import eu.ydp.empiria.player.client.controller.communication.DisplayContentOptions;
import eu.ydp.empiria.player.client.module.IModule;
import eu.ydp.empiria.player.client.module.registry.ModulesRegistrySocket;

public class InlineBodyGenerator implements InlineBodyGeneratorSocket {

	protected ModulesRegistrySocket modulesRegistrySocket;
	protected DisplayContentOptions options;
	
	public InlineBodyGenerator(ModulesRegistrySocket mrs, DisplayContentOptions options){
		this.modulesRegistrySocket = mrs;
		this.options = options;
	}

	public void generateInlineBody(Node mainNode, com.google.gwt.dom.client.Element parentElement){
		if (mainNode != null  &&  mainNode.hasChildNodes()  &&  parentElement instanceof com.google.gwt.dom.client.Element)
			parseXML(mainNode.getChildNodes(), parentElement);
	}
	

	public Widget generateInlineBody(Node mainNode){
		Widget h = new InlineHTML();
		h.setStyleName("qp-text-inline");
		parseXML(mainNode.getChildNodes(), h.getElement());
		return h;
	}
	
	protected com.google.gwt.dom.client.Element parseXML(NodeList nodes, com.google.gwt.dom.client.Element parentElement){
		
		for (int i = 0 ; i < nodes.getLength() ; i ++){
		
			Node currNode = nodes.item(i);
			
			if (currNode.getNodeType() == Node.ELEMENT_NODE){
				if (options.getIgnoredInlineTags().contains(currNode.getNodeName())){
					continue;
				} else if (modulesRegistrySocket.isModuleSupported(currNode.getNodeName())  &&  modulesRegistrySocket.isInlineModule(currNode.getNodeName())){
					IModule module = modulesRegistrySocket.createModule(currNode.getNodeName());
					if (module instanceof Widget){
						parentElement.appendChild(((Widget)module).getElement());
					}
				} else {
					com.google.gwt.dom.client.Element newElement = Document.get().createElement(currNode.getNodeName());
					parseXMLAttributes((Element)currNode, newElement);
					parentElement.appendChild(newElement);
					parseXML(currNode.getChildNodes(), newElement);
				}
			} else if(currNode.getNodeType() == Node.TEXT_NODE){
				Document doc = Document.get();
				parentElement.appendChild(doc.createTextNode(currNode.getNodeValue()));
			}
		}
		
		return parentElement;
	}
	
	private void parseXMLAttributes(com.google.gwt.xml.client.Element srcElement, com.google.gwt.dom.client.Element dstElement){
		NamedNodeMap attributes = srcElement.getAttributes();
		
		for(int i = 0; i < attributes.getLength(); i++){
			Node attribute = attributes.item(i);
			if (attribute.getNodeValue().length() > 0){
				if (attribute.getNodeName().equals("class")){
					dstElement.addClassName(attribute.getNodeValue());
				} else {
					dstElement.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
				}
			}
			
		}
	}
}