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

package eu.ydp.empiria.player.client.module.tutor.actions.popup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public class TutorPopupViewWidgetImpl extends Composite implements TutorPopupViewWidget {

    private static TutorPopupViewWidgetUiBinder uiBinder = GWT.create(TutorPopupViewWidgetUiBinder.class);

    interface TutorPopupViewWidgetUiBinder extends UiBinder<Widget, TutorPopupViewWidgetImpl> {
    }

    @UiField
    protected FlowPanel itemsContainer;

    @UiField
    protected FlowPanel closeButton;

    @PostConstruct
    public void postConstruct() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public Widget getWidget(int personaIndex) {
        return itemsContainer.getWidget(personaIndex);
    }

    @Override
    public void addWidget(TutorPopupViewPersonaView personaView) {
        itemsContainer.add(personaView);
    }

    @Override
    public List<Widget> getAllWidgets() {
        int widgetCount = itemsContainer.getWidgetCount();
        List<Widget> widgets = new ArrayList<Widget>();

        for (int i = 0; i < widgetCount; i++) {
            widgets.add(itemsContainer.getWidget(i));
        }
        return widgets;
    }

    @Override
    public Widget getCloseButton() {
        return closeButton;
    }

}
