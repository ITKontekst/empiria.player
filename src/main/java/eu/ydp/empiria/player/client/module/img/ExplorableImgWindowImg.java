package eu.ydp.empiria.player.client.module.img;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ExplorableImgWindowImg extends AbstractExplorableImgWindowBase {

    private static ExplorableImgWindowImgUiBinder uiBinder = GWT.create(ExplorableImgWindowImgUiBinder.class);

    interface ExplorableImgWindowImgUiBinder extends UiBinder<Widget, ExplorableImgWindowImg> {
    }

    @UiField
    protected FlowPanel windowPanel;
    @UiField
    protected FlowPanel imagePanel;
    @UiField
    protected Image image;

    private int prevX = -1, prevY = -1;

    public ExplorableImgWindowImg() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(int wndWidth, int wndHeight, String imageUrl, double initialScale, double scaleStep, double zoomMax, String title) {
        setWindowWidth(wndWidth);
        setWindowHeight(wndHeight);
        setScale(initialScale);
        setScaleStep(scaleStep);
        setZoomMax(zoomMax);

        windowPanel.setSize(getWindowWidth() + "px", getWindowHeight() + "px");
        windowPanel.getElement().getStyle().setOverflow(Overflow.AUTO);

        image.setUrl(imageUrl);
        image.setAltText(title);
        image.addLoadHandler(new LoadHandler() {

            @Override
            public void onLoad(LoadEvent event) {
                setOriginalImageWidth(image.getWidth());
                setOriginalImageHeight(image.getHeight());

                findScaleMinAndOriginalAspectRatio();

                scaleTo(getScale());
                centerImage();
            }
        });

        image.addErrorHandler(new ErrorHandler() {

            @Override
            public void onError(ErrorEvent event) {// NPOMD
            }
        });

        image.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent event) {
                event.preventDefault();
                prevX = event.getClientX();
                prevY = event.getClientY();
            }
        });

        image.addMouseMoveHandler(new MouseMoveHandler() {

            @Override
            public void onMouseMove(MouseMoveEvent event) {
                event.preventDefault();

                if (prevX != -1 && prevY != -1) {
                    int scrollLeft = windowPanel.getElement().getScrollLeft();
                    windowPanel.getElement().setScrollLeft(scrollLeft - (event.getClientX() - prevX));
                    prevX = event.getClientX();

                    int scrollTop = windowPanel.getElement().getScrollTop();
                    windowPanel.getElement().setScrollTop(scrollTop - (event.getClientY() - prevY));
                    prevY = event.getClientY();
                }
            }
        });

        image.addMouseUpHandler(new MouseUpHandler() {

            @Override
            public void onMouseUp(MouseUpEvent event) {
                event.preventDefault();
                prevX = -1;
                prevY = -1;
            }
        });
    }

    @Override
    public void zoomIn() {
        scaleBy(getScaleStep());
    }

    @Override
    public void zoomOut() {
        scaleBy(1.0d / getScaleStep());
    }

    private void centerImage() {

        windowPanel.getElement().setScrollLeft((image.getWidth() - windowPanel.getOffsetWidth()) / 2);
        windowPanel.getElement().setScrollTop((image.getHeight() - windowPanel.getOffsetHeight()) / 2);
    }

    private void scaleBy(double dScale) {
        double newScale;
        if (getZoom() * dScale > getZoomMax()) {
            newScale = getOriginalImageWidth() / getWindowWidth() * (getZoomMax());
        } else if (getScale() * dScale > getScaleMin()) {
            newScale = getScale() * dScale;
        } else {
            newScale = getScaleMin();
        }

        scaleTo(newScale);
    }

    private void scaleTo(double newScale) {

        int lastCenterLeft = windowPanel.getElement().getScrollLeft() + getWindowWidth() / 2;
        int lastCenterTop = windowPanel.getElement().getScrollTop() + getWindowHeight() / 2;

        int newCenterLeft = (int) (lastCenterLeft * (newScale / getScale()));
        int newCenterTop = (int) (lastCenterTop * (newScale / getScale()));

        int nextScrollLeft = newCenterLeft - getWindowWidth() / 2;
        int nextScrollTop = newCenterTop - getWindowHeight() / 2;

        setScale(newScale);

        double newImageWidth = getWindowWidth() * getScale();
        double newImageheight = newImageWidth / getOriginalAspectRatio();

        image.setSize(((int) newImageWidth) + "px", ((int) newImageheight) + "px");

        windowPanel.getElement().setScrollLeft(nextScrollLeft);
        windowPanel.getElement().setScrollTop(nextScrollTop);

    }

}
