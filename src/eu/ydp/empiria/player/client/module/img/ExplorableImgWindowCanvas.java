package eu.ydp.empiria.player.client.module.img;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import eu.ydp.canvasadapter.client.CanvasAdapter;
import eu.ydp.canvasadapter.client.Context2dAdapter;
import eu.ydp.empiria.player.client.components.PanelWithScrollbars;

public class ExplorableImgWindowCanvas extends ExplorableImgWindowBase {

	private static ExplorableCanvasImgContentUiBinder uiBinder = GWT.create(ExplorableCanvasImgContentUiBinder.class);

	interface ExplorableCanvasImgContentUiBinder extends UiBinder<Widget, ExplorableImgWindowCanvas> {}

	@UiField
	FlowPanel imagePanel;
	@UiField
	CanvasAdapter imageCanvas;
	@UiField
	PanelWithScrollbars scrollbarsPanel;

	private Context2dAdapter context2d;
	private final int REDRAW_INTERVAL_MIN = 50;
	private double imgX = 0;
	private double imgY = 0;
	private FocusWidget focusCanvas;
	
	private double prevX, prevY;
	private boolean moving = false;
	private double prevDistance = -1;
	private long lastRedrawTime = -1;
	private boolean imageLoaded = false;

	private Image tempImage;
	
	public ExplorableImgWindowCanvas() {
		initWidget(uiBinder.createAndBindUi(this));
		context2d = imageCanvas.getContext2d();
	}
	
	@Override
	public void init(int wndWidth, int wndHeight, String imageUrl, double initialScale) {
		setWindowWidth(wndWidth);
		setWindowHeight(wndHeight);
		setScale(initialScale);

		tempImage = new Image(imageUrl);
		RootPanel.get().add(tempImage);
		// TODO: try to put img on a div with visibility:hidden
		// see http://gwt-image-loader.googlecode.com/svn/trunk/src/com/reveregroup/gwt/imagepreloader/ImagePreloader.java
		if (!Navigator.getUserAgent().contains("MSIE"))
			tempImage.setVisible(false);
		
		tempImage.addLoadHandler(new LoadHandler() {
			
			@Override
			public void onLoad(LoadEvent event) {	
				imageLoaded = true;
				
				setOriginalImageWidth(tempImage.getWidth());
				setOriginalImageHeight(tempImage.getHeight());
				
				findScaleMinAndOriginalAspectRatio();
				
				centerImage();
				redraw(false);

				RootPanel.get().remove(tempImage);
			}
		});
		
		tempImage.addErrorHandler(new ErrorHandler() {
			
			@Override
			public void onError(ErrorEvent event) {
				RootPanel.get().remove(tempImage);
			}
		});
		
		imageCanvas.setCoordinateSpaceWidth((int)getWindowWidth());
		imageCanvas.setCoordinateSpaceHeight((int)getWindowHeight());
		imageCanvas.setWidth(String.valueOf((int)getWindowWidth()) + "px");
		imageCanvas.setHeight(String.valueOf((int)getWindowHeight()) + "px");
		scrollbarsPanel.setSize(String.valueOf((int)getWindowWidth()) + "px", String.valueOf((int)getWindowHeight()) + "px");
		focusCanvas = (FocusWidget)imageCanvas.asWidget();
		focusCanvas.addTouchStartHandler(new TouchStartHandler() {
			
			@Override
			public void onTouchStart(TouchStartEvent event) {
				onMoveStart(event.getTouches().get(0).getClientX(), event.getTouches().get(0).getClientY());
				event.preventDefault();
			}
		});
		focusCanvas.addTouchMoveHandler(new TouchMoveHandler() {
			
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				if (event.getTouches().length() == 1){
					onMoveMove(event.getTouches().get(0).getClientX(), event.getTouches().get(0).getClientY());
				} else if (event.getTouches().length() == 2){
					onMoveScale(event.getTouches().get(0).getClientX(), event.getTouches().get(0).getClientY(), event.getTouches().get(1).getClientX(), event.getTouches().get(1).getClientY());
				}
				event.preventDefault();
			}

		});
		focusCanvas.addTouchEndHandler(new TouchEndHandler() {
			
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				onMoveEnd();
				event.preventDefault();
			}
		});
		
		focusCanvas.addMouseDownHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				onMoveStart(event.getClientX(), event.getClientY());
			}
		});
		
		focusCanvas.addMouseMoveHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				onMoveMove(event.getClientX(), event.getClientY());
			}
		});
		
		focusCanvas.addMouseUpHandler(new MouseUpHandler() {
			
			@Override
			public void onMouseUp(MouseUpEvent event) {
				onMoveEnd();
			}
		});
		
		focusCanvas.addMouseOutHandler(new MouseOutHandler() {
			
			@Override
			public void onMouseOut(MouseOutEvent event) {
				onMoveEnd();
			}
		});

	}
	
	private void onMoveStart(int x, int y){
		moving = true;
		prevX = x;
		prevY = y;
	}
	
	private void onMoveScale(int x1, int y1, int x2, int y2) {
		double currDistance= Math.sqrt(
				Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)
				);
		
		if (prevDistance != -1){
			scaleBy(currDistance/prevDistance);
			redraw(true);
		}
		
		prevDistance = currDistance;
				
	}
	
	private void onMoveMove(int x, int y){
		if (moving){
			
			double dx = x - prevX;
			double dy = y - prevY;
			
			
			double zoom  = getZoom();
			
			imgX -= dx/zoom;
			imgY -= dy/zoom;
			
			redraw(true);

			prevX = x;
			prevY = y;
			
		}
	}
	
	private void onMoveEnd(){
		moving = false;
		prevDistance = -1;
	}
	
	private void redraw(boolean showScrollbars){
		
		checkImageCoords();
		
		double scaleNormalized = getScale();
		
		double sourceX = imgX;
		double sourceY = imgY;
		double sourceWidth = getOriginalImageWidth() / scaleNormalized;
		double sourceHeight = (getOriginalImageWidth() * getWindowHeight() / getWindowWidth()) / scaleNormalized;
		double destX = 0;
		double destY = 0;
		double destWidth = getWindowWidth();
		double destHeight = getWindowHeight();
		
		if (sourceX + sourceWidth >  getOriginalImageWidth()){
			sourceWidth = getOriginalImageWidth() - sourceX;
			double z = getZoom();
			destWidth = sourceWidth * z;
			context2d.clearRect(destWidth, 0, getWindowWidth() - destWidth, getWindowHeight());
		}
		if (sourceY + sourceHeight >  getOriginalImageHeight()){
			sourceHeight = getOriginalImageHeight() - sourceY;
			double z = getZoom();
			destHeight = sourceHeight * z;
			context2d.clearRect(0, destHeight, getWindowWidth(), getWindowHeight() - destHeight);
		}
		
		if ((new Date()).getTime() - lastRedrawTime > REDRAW_INTERVAL_MIN){
			if (imageLoaded)
				context2d.drawImage(ImageElement.as(tempImage.getElement()), sourceX, sourceY, sourceWidth, sourceHeight, destX, destY, destWidth, destHeight);
			lastRedrawTime = (new Date()).getTime();
			updateScrollbars(showScrollbars);
		}
	}
	
	private void updateScrollbars(boolean showScrollbars){
		double posX = imgX*getZoom();
		double posY = imgY*getZoom();
		scrollbarsPanel.setHorizontalPosition(posX, getWindowWidth(), getOriginalImageWidth()*getZoom(), showScrollbars);
		scrollbarsPanel.setVerticalPosition(posY, getWindowHeight(), getOriginalImageHeight()*getZoom(), showScrollbars);
	}
	
	private void scaleBy(double dScale){
		double newScale;
		if (getZoom()*dScale > ZOOM_MAX)
			newScale = (double)getOriginalImageWidth() / (double)getWindowWidth() * (ZOOM_MAX);
		else if (getScale() * dScale > getScaleMin())
			newScale = getScale()*dScale;
		else
			newScale = getScaleMin();
		
		double lastCenterX = imgX*getZoom() + getWindowWidth()/2;
		double lastCenterY = imgY*getZoom() + getWindowHeight()/2;
		
		double newCenterX = lastCenterX * newScale/getScale();
		double newCenterY = lastCenterY * newScale/getScale();
		
		int newImgX = (int)(newCenterX - getWindowWidth()/2);
		int newImgY = (int)(newCenterY - getWindowHeight()/2);
		
		imgX = newImgX/getZoom(newScale);
		imgY = newImgY/getZoom(newScale);
		
		setScale(newScale);
		
	}
	
	private void checkImageCoords(){

		if (imgX + getOriginalImageWidth() / getScale() > getOriginalImageWidth())
			imgX = getOriginalImageWidth() - (int)(getOriginalImageWidth() / getScale()) - 1;	
		
		double h = (getOriginalImageWidth() * getWindowHeight() / getWindowWidth());
		
		if (imgY + h / getScale() > getOriginalImageHeight())
			imgY = getOriginalImageHeight() - (int)(h / getScale()) - 1; 
		
		if (imgX < 0)
			imgX = 0;
		
		if (imgY < 0)
			imgY = 0;	
	}
	
	private void centerImage(){

		imgX = (getOriginalImageWidth() - getWindowWidth() * getScale())/2 / getScale();
		imgY = (getOriginalImageHeight() - getWindowHeight() * getScale())/2 / getScale(); 
	}
	
	@Override
	public void zoomIn() {
		scaleBy(SCALE_STEP);
		redraw(true);
	}
	
	@Override
	public void zoomOut() {
		scaleBy(1.0d/SCALE_STEP);
		redraw(true);
	}
}