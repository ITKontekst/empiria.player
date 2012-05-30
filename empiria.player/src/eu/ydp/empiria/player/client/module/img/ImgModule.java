package eu.ydp.empiria.player.client.module.img;

import static eu.ydp.empiria.player.client.util.xml.XMLUtils.getAttributeAsDouble;
import static eu.ydp.empiria.player.client.util.xml.XMLUtils.getAttributeAsInt;
import static eu.ydp.empiria.player.client.util.xml.XMLUtils.getAttributeAsString;
import static eu.ydp.empiria.player.client.util.xml.XMLUtils.getFirstElementWithTagName;
import static eu.ydp.empiria.player.client.util.xml.XMLUtils.getText;

import java.util.Map;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.touch.client.Point;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import eu.ydp.empiria.player.client.components.CanvasArrow;
import eu.ydp.empiria.player.client.controller.events.interaction.InteractionEventsListener;
import eu.ydp.empiria.player.client.module.Factory;
import eu.ydp.empiria.player.client.module.ISimpleModule;
import eu.ydp.empiria.player.client.module.ModuleSocket;

/**
 * Klasa odpowiedzialna za renderwoanie elementu img.
 *
 *
 * Parametry css dla canvas dodajemy je dla obiektu img:<br/>
 *  <i>line-width</i>: grubosc linii rysowanej na canvasie: <br/>
 *  <i>line-color</i>: kolor linii rysowanej na canvasie<br/>
 * <br/>
 * <br/>
 */
public class ImgModule extends Composite implements ISimpleModule,Factory<ImgModule> {

	private static IMGModuleUiBinder uiBinder = GWT.create(IMGModuleUiBinder.class);

	interface IMGModuleUiBinder extends UiBinder<Widget, ImgModule> {
	}

	protected Image img;
	protected Canvas imgCanvas = null;
	@UiField
	protected Panel containerPanel;
	@UiField
	protected Panel titlePanel;
	@UiField
	protected Panel descriptionPanel;
	@UiField
	protected Panel contentPanel;

	public ImgModule() {
		initWidget(uiBinder.createAndBindUi(this));
		img = new Image();
	}

	/**
	 * rysuje linie
	 *
	 * @param line
	 * @param context2d
	 */
	private void parseLine(Element line, Context2d context2d) {
		NodeList elements = line.getChildNodes();
		String endPoint = null;
		String startPoint = null;
		boolean isStartSet = false;
		double lastX = 0, lastY = 0, startX = 0, startY = 0;
		double startBegX = 0, startBegY = 0, startEndX = 0, startEndY = 0;
		
		for (int x = 0; x < elements.getLength(); ++x) {
			Node node = elements.item(x);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) node;
				if (!"".equals(getAttributeAsString(e, "x"))) {
					startX = lastX;
					startY = lastY;
					lastX = getAttributeAsDouble(e, "x");
					lastY = getAttributeAsDouble(e, "y");
					
					if(!isStartSet && startPoint != null){
						startBegX = lastX;
						startBegY = lastY;
						isStartSet = true;
					}
				}
				if (e.getNodeName().equals("startPoint")) {
					context2d.moveTo(lastX, lastY);
					startPoint = getAttributeAsString(e, "type");
					startEndX = lastX;
					startEndY = lastY;
				} else if (e.getNodeName().equals("endPoint")) {
					endPoint = getText(e);
				} else if (e.getNodeName().equals("lineTo")) {
					context2d.lineTo(lastX, lastY);
					context2d.stroke();
				} else if (e.getNodeName().equals("lineStyle")) {
					if (!"".equals(getAttributeAsString(e, "alpha"))) {
						context2d.setGlobalAlpha(getAttributeAsInt(e, "alpha"));
					}
					// hex itd
					String color = getAttributeAsString(e, "color");
					if (color != null) {
						context2d.setStrokeStyle(color);
					}
					// liczba
					if (!"".equals(getAttributeAsString(e, "width"))) {
						getAttributeAsInt(e, "width");
					}
				}
			}
		}
		if (endPoint != null){
			drawShape(endPoint, context2d, lastX, lastY, startX, startY);
		}
		
		if(startPoint != null){
			drawShape(startPoint, context2d, startEndX, startEndY, startBegX, startBegY);
		}
	}

	/**
	 * Parsuje element text i generuje odpowiednie widgety
	 * @param text
	 * @param anchor
	 * @param context2d
	 * @param ms
	 * @param mainPanel
	 */
	private void parseText(Element text, Element anchor, Context2d context2d, ModuleSocket ms, Panel mainPanel) {
		HorizontalPanel panel = new HorizontalPanel();
		Label label = new Label(getText(text));
		label.setStyleName("qp-img");
		panel.add(label);
		NodeList nodes = text.getChildNodes();
		for (int x = 0; x < nodes.getLength(); ++x) {
			Node node = nodes.item(x);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Widget widget = ms.getInlineBodyGeneratorSocket().generateInlineBodyForNode(node);
				widget.addStyleName("qp-img");
				if (widget != null) {
					panel.add(widget);
				}
			}
		}
		
		mainPanel.add(panel);
		alignWidget(panel, anchor);
	}

	/**
	 * Rysuje zakonczenie linii
	 * @param shapeName
	 * @param context
	 * @param centerX
	 * @param centerY
	 * @param startX
	 * @param startY
	 */
	private void drawShape(String shapeName, Context2d context, double centerX, double centerY, double startX, double startY) {
		if (shapeName.equals("dot")) {
			context.beginPath();
			context.arc(centerX, centerY, 3, 0, 2 * Math.PI, false);
			context.closePath();
			context.fill();
		} else if (shapeName.equals("arrow")) {
			CanvasArrow ca = new CanvasArrow(context, startX, startY, centerX, centerY);
			ca.draw();
		}
	}

	/**
	 * Tworzy obiekt html5/canvas
	 *
	 * @param element
	 * @param ms
	 */
	private void fillCanvas(final Element element, final ModuleSocket ms, final Panel mainPanel) {
		String src = element.getAttribute("src");
		if (src != null)
			img.setUrl(src);
		String alt = element.getAttribute("alt");
		if (alt != null)
			img.setAltText(alt);
		String cls = element.getAttribute("class");
		if (cls != null)
			img.addStyleName(cls);
		ms.getStyles(element);
		RootPanel.get().add(img);
		img.setVisible(false);
		img.addLoadHandler(new LoadHandler() {

			@Override
			public void onLoad(LoadEvent event) {
				ImageElement imgelement = ImageElement.as(img.getElement());
				imgCanvas.setWidth(img.getWidth() + "px");
				imgCanvas.setCoordinateSpaceWidth(img.getWidth());
				imgCanvas.setHeight(img.getHeight() + "px");
				imgCanvas.setCoordinateSpaceHeight(img.getHeight());
				//imgelement.
				Context2d context2d = imgCanvas.getContext2d();
				context2d.drawImage(imgelement, 0, 0);
				setContextStyle(context2d,ms);
				NodeList labelList = element.getElementsByTagName("label");
				for (int x = 0; x < labelList.getLength(); ++x) {
					Element label = (Element) labelList.item(x);
					// ustawiony czas nie obslugujemy w pictureplayer
					if (getAttributeAsInt(label, "start") != 0) {
						continue;
					}
					Element anchor = getFirstElementWithTagName(label, "anchor");
					Element line = getFirstElementWithTagName(label, "line");
					Element text = getFirstElementWithTagName(label, "text");
					if (line != null) {
						parseLine(line, context2d);
					}
					if (text != null && anchor != null) {
						parseText(text, anchor, context2d, ms, mainPanel);
					}
				}
				RootPanel.get().remove(img);
			}
		});
	}

	/**
	 * ustawia style dla context
	 * @param context2d
	 * @param ms
	 */
	private void setContextStyle(Context2d context2d, ModuleSocket ms){
		Map<String, String> styles =  ms.getStyles(XMLParser.createDocument().createElement("img"));
		if(styles.containsKey("line-color")){
			try{
				context2d.setStrokeStyle(CssColor.make(styles.get("line-color")).value());
			}catch(Exception e){
				e.fillInStackTrace();
			}
		}
		if(styles.containsKey("line-width")){
			try{
				context2d.setLineWidth(Double.valueOf(styles.get("line-width").replaceAll("\\D", "")));
			}catch(Exception e){}
		}
	}
	
	private void alignWidget(Widget widget, Element anchorElement){
		String horizontalAlign = getAttributeAsString(
				getFirstElementWithTagName(anchorElement, "x_anchor"), "anchor");
		String verticalAlign = getAttributeAsString(
						getFirstElementWithTagName(anchorElement, "y_anchor"), "anchor");
		Point anchorPoint = new Point(
						getAttributeAsDouble(anchorElement, "x"), 
						getAttributeAsDouble(anchorElement, "y"));
		
		alignWidget(widget, anchorPoint, horizontalAlign, verticalAlign);
	}
	
	private void alignWidget(Widget widget, Point anchorPoint, String horizontalAlign, String verticalAlign){
		Style style = widget.getElement().getStyle();
		double xPos = anchorPoint.getX();
		double yPos = anchorPoint.getY();
		
		if(horizontalAlign.equals("center")){
			xPos -= widget.getOffsetWidth()/2;
		}else if(horizontalAlign.equals("right")){
			xPos -= widget.getOffsetWidth();
		}
		
		if(verticalAlign.equals("center")){
			yPos -= widget.getOffsetHeight()/2;
		}else if(verticalAlign.equals("bottom")){
			yPos -= widget.getOffsetHeight();
		}
		
		style.setPosition(Position.ABSOLUTE);
		style.setTop(yPos, Unit.PX);
		style.setLeft(xPos, Unit.PX);
	}

	/**
	 * ustawia dodatkowe parametry dla img pobrane z atrybutow elemntu xml-a
	 * @param element
	 */
	private void fillImage(Element element) {
		String src = element.getAttribute("src");
		if (src != null)
			img.setUrl(src);
		String alt = element.getAttribute("alt");
		if (alt != null)
			img.setAltText(alt);
		String cls = element.getAttribute("class");
		if (cls != null)
			img.addStyleName(cls);
	}

	@Override
	public void initModule(Element element, ModuleSocket ms, InteractionEventsListener mil) {
		contentPanel.getElement().getStyle().setPosition(Position.RELATIVE);
		if (Canvas.isSupported()) {
			imgCanvas = Canvas.createIfSupported();
			fillCanvas(element, ms, contentPanel);
		} else {
			img.setStyleName("qp-img");
			fillImage(element);
		}

		contentPanel.add(imgCanvas != null ? imgCanvas : img);
		String id = element.getAttribute("id");
		if (id != null && !"".equals(id) && getView() != null) {
			getView().getElement().setId(id);
		}

		NodeList titleNodes = element.getElementsByTagName("title");
		if (titleNodes.getLength() > 0) {
			Widget titleWidget = ms.getInlineBodyGeneratorSocket().generateInlineBody(titleNodes.item(0));
			if (titleWidget != null) {
				titlePanel.add(titleWidget);
			}
		}
		NodeList descriptionNodes = element.getElementsByTagName("description");
		if (descriptionNodes.getLength() > 0) {
			Widget descriptionWidget = ms.getInlineBodyGeneratorSocket().generateInlineBody(descriptionNodes.item(0));
			if (descriptionWidget != null) {
				descriptionPanel.add(descriptionWidget);
			}
		}
	}

	@Override
	public Widget getView() {
		return this;
	}

	@Override
	public ImgModule getNewInstance() {
		return new ImgModule();
	}
}
