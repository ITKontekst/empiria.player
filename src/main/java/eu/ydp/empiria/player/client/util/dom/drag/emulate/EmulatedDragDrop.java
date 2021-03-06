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

package eu.ydp.empiria.player.client.util.dom.drag.emulate;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import eu.ydp.empiria.player.client.overlaytypes.OverlayTypesParser;
import eu.ydp.empiria.player.client.resources.StyleNameConstants;
import eu.ydp.empiria.player.client.util.dom.drag.AbstractDragDrop;
import eu.ydp.empiria.player.client.util.dom.drag.DragDropType;
import eu.ydp.empiria.player.client.util.dom.drag.DraggableObject;
import eu.ydp.empiria.player.client.util.dom.drag.DroppableObject;
import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import javax.annotation.PostConstruct;

/**
 * For mobile and old browsers gQuery used
 */
public class EmulatedDragDrop<W extends Widget> extends AbstractDragDrop<W> implements DraggableObject<W>, DroppableObject<W> {
    private static final Integer DRAG_Z_INDEX = 100000;
    private static final String DATA_JSON = "data-json";
    private DragStartEndHandlerWrapper dragStartEndHandlerWrapper;
    private DropEventsHandlerWrapper dropEventsHandlerWrapper;
    private DraggableWidget<W> dragWidget;
    private DroppableWidget<W> dropWidget;
    private boolean disabled;
    private final W originalWidget;
    private final boolean disableAutoBehavior;
    private final DragDropType type;
    @Inject
    private OverlayTypesParser overlayTypesParser;
    @Inject
    private StyleNameConstants styleNames;

    @Inject
    public EmulatedDragDrop(@Assisted("widget") W widget, @Assisted("type") DragDropType type, @Assisted("disableAutoBehavior") boolean disableAutoBehavior) {
        this.originalWidget = widget;
        this.disableAutoBehavior = disableAutoBehavior;
        this.type = type;
    }

    @PostConstruct
    public void postConstruct() {
        if (type == DragDropType.DRAG) {
            createDrag(originalWidget);
        } else {
            createDrop(originalWidget);
        }
    }

    private void createDrag(W widget) {
        DraggableOptions options = new DraggableOptions();
        options.setHelper(HelperType.CLONE);
        options.setRevert(RevertOption.ON_INVALID_DROP);
        options.setCursor(Cursor.MOVE);
        options.setZIndex(DRAG_Z_INDEX);
        options.setContainment(".qp-player");
        if (widget instanceof DraggableWidget) {
            dragWidget = (DraggableWidget<W>) widget;
        } else {
            dragWidget = new DraggableWidget<W>(widget, options);
        }
        dragWidget.setDraggableOptions(options);
        dragWidget.setDraggingOpacity(.8f);

    }

    private void createDrop(W widget) {
        if (widget instanceof DroppableWidget) {
            dropWidget = (DroppableWidget<W>) widget;
        } else {
            dropWidget = new DroppableWidget<W>(widget);
        }
        dropWidget.addDropHandler(new DropEventHandler() {
            @Override
            public void onDrop(DropEvent event) {
                String attribute = event.getDraggable().getAttribute(DATA_JSON);
                JsonAttr jsonAttr = overlayTypesParser.get(attribute);
                getDropEventsHandlerWrapper().setJsonAttr(jsonAttr);
            }
        });

        if (!disableAutoBehavior) {
            setAutoBehaviorForDrop(disableAutoBehavior);
        }
    }

    private void setAutoBehaviorForDrop(boolean disableAutoBehavior) {
        dropWidget.addDropHandler(new DropEventHandler() {
            @Override
            public void onDrop(DropEvent event) {
                removeStyleForWidget(styleNames.QP_DROPZONE_OVER(), disabled);
            }
        });

        dropWidget.addOverDroppableHandler(new OverDroppableEventHandler() {
            @Override
            public void onOverDroppable(OverDroppableEvent event) {
                addStyleForWidget(styleNames.QP_DROPZONE_OVER(), disabled);
            }
        });

        dropWidget.addOutDroppableHandler(new OutDroppableEventHandler() {
            @Override
            public void onOutDroppable(OutDroppableEvent event) {
                removeStyleForWidget(styleNames.QP_DROPZONE_OVER(), disabled);
            }
        });
    }

    public DragStartEndHandlerWrapper getDragStartEndHandlerWrapper() {
        if (dragStartEndHandlerWrapper == null) {
            dragStartEndHandlerWrapper = new DragStartEndHandlerWrapper(dragWidget);
        }
        return dragStartEndHandlerWrapper;
    }

    public DropEventsHandlerWrapper getDropEventsHandlerWrapper() {
        if (dropEventsHandlerWrapper == null) {
            dropEventsHandlerWrapper = new DropEventsHandlerWrapper(dropWidget, overlayTypesParser);
        }
        return dropEventsHandlerWrapper;
    }

    @Override
    public HandlerRegistration addDragEndHandler(DragEndHandler handler) {
        return getDragStartEndHandlerWrapper().wrap(handler);
    }

    @Override
    public HandlerRegistration addDragEnterHandler(DragEnterHandler handler) {
        return getDropEventsHandlerWrapper().wrap(handler);
    }

    @Override
    public HandlerRegistration addDragLeaveHandler(DragLeaveHandler handler) {
        return getDropEventsHandlerWrapper().wrap(handler);
    }

    @Override
    public HandlerRegistration addDragOverHandler(DragOverHandler handler) {
        return getDropEventsHandlerWrapper().wrap(handler);
    }

    @Override
    public HandlerRegistration addDragStartHandler(DragStartHandler handler) {
        return getDragStartEndHandlerWrapper().wrap(handler);
    }

    @Override
    public HandlerRegistration addDropHandler(DropHandler handler) {
        return getDropEventsHandlerWrapper().wrap(handler);
    }

    @Override
    public Widget getDraggableWidget() {
        return dragWidget;
    }

    @Override
    public Widget getDroppableWidget() {
        return dropWidget;
    }

    @Override
    public W getOriginalWidget() {
        return originalWidget;
    }

    @Override
    public void setDisableDrag(boolean disable) {
        this.disabled = disable;

    }

    @Override
    public void setDisableDrop(boolean disable) {
        dropWidget.setDisabled(disable);
        this.disabled = disable;

    }

}
