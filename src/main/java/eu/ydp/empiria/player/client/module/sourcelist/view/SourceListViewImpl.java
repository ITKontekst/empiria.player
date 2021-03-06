/*
 * Copyright 2017 Young Digital Planet S.A.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.ydp.empiria.player.client.module.sourcelist.view;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DragDropEventBase;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import eu.ydp.empiria.player.client.controller.body.InlineBodyGenerator;
import eu.ydp.empiria.player.client.controller.body.InlineBodyGeneratorSocket;
import eu.ydp.empiria.player.client.gin.factory.TouchReservationFactory;
import eu.ydp.empiria.player.client.module.dragdrop.SourcelistItemValue;
import eu.ydp.empiria.player.client.module.draggap.view.DragDataObjectFromEventExtractor;
import eu.ydp.empiria.player.client.module.sourcelist.presenter.SourceListPresenter;
import eu.ydp.empiria.player.client.ui.drop.FlowPanelWithDropZone;
import eu.ydp.empiria.player.client.util.dom.drag.DragDataObject;
import eu.ydp.empiria.player.client.util.dom.drag.DragDropHelper;
import eu.ydp.empiria.player.client.util.dom.drag.DroppableObject;
import eu.ydp.empiria.player.client.util.events.internal.dragdrop.DragDropEventTypes;
import eu.ydp.gwtutil.client.event.factory.EventHandlerProxy;
import eu.ydp.gwtutil.client.event.factory.UserInteractionHandlerFactory;
import eu.ydp.gwtutil.client.util.geom.HasDimensions;
import eu.ydp.gwtutil.client.util.geom.Size;

public class SourceListViewImpl extends Composite implements SourceListView {

    protected static SourceListViewImplUiBinder uiBinder = GWT.create(SourceListViewImplUiBinder.class);

    interface SourceListViewImplUiBinder extends UiBinder<Widget, SourceListViewImpl> {
    }

    @Inject
    private TouchReservationFactory touchReservationFactory;
    @Inject
    private Provider<SourceListViewItem> sourceListViewItemProvider;
    @Inject
    private DragDropHelper dragDropHelper;
    @Inject
    private DragDataObjectFromEventExtractor objectFromEventExtractor;
    @Inject
    private UserInteractionHandlerFactory interactionHandlerFactory;
    @UiField
    FlowPanelWithDropZone items;

    private final BiMap<String, SourceListViewItem> itemIdToItemCollection = HashBiMap.create();
    private SourceListPresenter sourceListPresenter;
    private DroppableObject<FlowPanelWithDropZone> sourceListDropZone;

    private SourceListViewItem getItem(SourcelistItemValue itemValue, InlineBodyGeneratorSocket inlineBodyGeneratorSocket) {
        SourceListViewItem item = sourceListViewItemProvider.get();
        item.setSourceListView(this);
        item.createAndBindUi(itemValue, inlineBodyGeneratorSocket);
        return item;
    }

    @Override
    public void createAndBindUi() {
        initWidget(uiBinder.createAndBindUi(this));
        sourceListDropZone = dragDropHelper.enableDropForWidget(items, false);
        touchReservationFactory.addTouchReservationHandler(items);
        addDropHandler();
        disableAutoBehaviorOnSelect();
    }

    private void disableAutoBehaviorOnSelect() {
        final EventHandlerProxy userOverHandler = interactionHandlerFactory.createUserOverHandler(new DisableDefaultBehaviorCommand());
        userOverHandler.apply(this);
    }

    private void addDropHandler() {
        SourceListViewDropHandler dropHandler = new SourceListViewDropHandler(objectFromEventExtractor, sourceListPresenter);
        sourceListDropZone.addDropHandler(dropHandler);
    }

    public void onDragEvent(DragDropEventTypes dropEventType, SourceListViewItem item, DragDropEventBase<?> dragEvent) {
        String itemId = itemIdToItemCollection.inverse().get(item);
        if (dropEventType == DragDropEventTypes.DRAG_START) {
            setDataOnNativeEvent(dragEvent, itemId);
        }
        sourceListPresenter.onDragEvent(dropEventType, itemId);
    }

    private void setDataOnNativeEvent(DragDropEventBase<?> dragEvent, String itemId) {
        DragDataObject dataObject = sourceListPresenter.getDragDataObject(itemId);
        dragEvent.setData("json", dataObject.toJSON());
    }

    @Override
    public SourcelistItemValue getItemValue(String itemId) {
        return itemIdToItemCollection.get(itemId).getItemContent();
    }

    @Override
    public void createItem(SourcelistItemValue itemContent, InlineBodyGeneratorSocket inlineBodyGeneratorSocket) {
        SourceListViewItem item = getItem(itemContent, inlineBodyGeneratorSocket);
        items.add(item);
        itemIdToItemCollection.put(itemContent.getItemId(), item);
    }

    @Override
    public void hideItem(String itemId) {
        if (itemIdToItemCollection.containsKey(itemId)) {
            itemIdToItemCollection.get(itemId).hide();
        }
    }

    @Override
    public void showItem(String itemId) {
        if (itemIdToItemCollection.containsKey(itemId)) {
            itemIdToItemCollection.get(itemId).show();
        }
    }

    @Override
    public void lockItemForDragDrop(String itemId) {
        if (itemIdToItemCollection.containsKey(itemId)) {
            itemIdToItemCollection.get(itemId).lockForDragDrop();
        }
    }

    @Override
    public void unlockItemForDragDrop(String itemId) {
        if (itemIdToItemCollection.containsKey(itemId)) {
            itemIdToItemCollection.get(itemId).unlockForDragDrop();
        }
    }

    @Override
    public void setSourceListPresenter(SourceListPresenter sourceListPresenter) {
        this.sourceListPresenter = sourceListPresenter;
    }

    @Override
    public void lockForDragDrop() {
        sourceListDropZone.setDisableDrop(true);
    }

    @Override
    public void unlockForDragDrop() {
        sourceListDropZone.setDisableDrop(false);
    }

    @Override
    public HasDimensions getMaxItemSize() {
        int maxWidth = 0;
        int maxHeight = 0;
        for (SourceListViewItem viewItem : itemIdToItemCollection.values()) {
            maxWidth = Math.max(maxWidth, viewItem.getWidth());
            maxHeight = Math.max(maxHeight, viewItem.getHeight());
        }
        return new Size(maxWidth, maxHeight);
    }
}
