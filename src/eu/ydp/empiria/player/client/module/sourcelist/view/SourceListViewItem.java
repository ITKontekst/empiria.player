package eu.ydp.empiria.player.client.module.sourcelist.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import eu.ydp.empiria.player.client.resources.StyleNameConstants;
import eu.ydp.empiria.player.client.util.dom.drag.DragDropHelper;
import eu.ydp.empiria.player.client.util.dom.drag.DraggableObject;
import eu.ydp.empiria.player.client.util.events.dragdrop.DragDropEventTypes;

public class SourceListViewItem extends Composite {

	private static SourceListViewItemUiBinder uiBinder = GWT.create(SourceListViewItemUiBinder.class);

	interface SourceListViewItemUiBinder extends UiBinder<Widget, SourceListViewItem> {
	}

	protected @UiField FlowPanel item;
	private @Inject StyleNameConstants styleNames;
	private @Inject DragDropHelper dragDropHelper;
	private SourceListViewImpl sourceListView;
	private DraggableObject<FlowPanel> draggable;
	private FlowPanel container;

	private String itemContent;

	public void setSourceListView(SourceListViewImpl sourceListView) {
		this.sourceListView = sourceListView;
	}

	public void setDisableDrag(boolean disableDrag) {
		draggable.setDisableDrag(disableDrag);
	}

	public void show() {
		container.setVisible(true);
	}

	public void hide() {
		container.setVisible(false);
	}

	public void createAndBindUi(String itemContent) {
		this.itemContent = itemContent;
		initWidget(uiBinder.createAndBindUi(this));
		Label label = new Label(itemContent);
		container = new FlowPanel();
		container.addStyleName(styleNames.QP_DRAG_ITEM());
		container.add(label);
		//FIXME null do wyciecia zmiana api
		draggable = dragDropHelper.enableDragForWidget(container, null);
		item.add(draggable.getDraggableWidget());
		addDragHandlers();
	}

	private void addDragHandlers() {
		addDragStartHandler();
		addDragEndHandler();
	}

	private void addDragStartHandler() {
		draggable.addDragStartHandler(new DragStartHandler() {
			@Override
			public void onDragStart(DragStartEvent event) {
				getElement().addClassName(styleNames.QP_DRAGGED_DRAG());
				event.getDataTransfer().setDragImage(getElement(), 0, 0);
				sourceListView.onDragEvent(DragDropEventTypes.DRAG_START, SourceListViewItem.this,event);
			}
		});
	}

	private void addDragEndHandler() {
		draggable.addDragEndHandler(new DragEndHandler() {
			@Override
			public void onDragEnd(DragEndEvent event) {
				getElement().removeClassName(styleNames.QP_DRAGGED_DRAG());
				//FIXME drag cancel ??
				//sourceListView.onMaybeDragCanceled();
			}
		});


	}

	public String getItemContent() {
		return itemContent;
	}
}
