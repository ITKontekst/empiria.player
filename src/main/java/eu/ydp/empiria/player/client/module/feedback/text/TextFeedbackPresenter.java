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

package eu.ydp.empiria.player.client.module.feedback.text;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import eu.ydp.empiria.player.client.controller.feedback.FeedbackMark;
import eu.ydp.empiria.player.client.controller.feedback.FeedbackMarkStyleProvider;
import eu.ydp.empiria.player.client.module.feedback.FeedbackStyleNameConstants;
import eu.ydp.gwtutil.client.ui.button.CustomPushButton;

public class TextFeedbackPresenter extends Composite implements TextFeedback {

    private static TextFeedbackViewUiBinder uiBinder = GWT.create(TextFeedbackViewUiBinder.class);

    private FeedbackStyleNameConstants feedbackStyleNameConstants;
    private FeedbackMarkStyleProvider typeStyleProvider;

    @UiTemplate("TextFeedbackView.ui.xml")
    interface TextFeedbackViewUiBinder extends UiBinder<Widget, TextFeedbackPresenter> {
    }

    @Inject
    public TextFeedbackPresenter(FeedbackStyleNameConstants feedbackStyleNameConstants, FeedbackMarkStyleProvider typeStyleProvider) {
        this.feedbackStyleNameConstants = feedbackStyleNameConstants;
        this.typeStyleProvider = typeStyleProvider;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    FlowPanel feedbackTextPanel;
    @UiField
    CustomPushButton feedbackCloseButton;
    @UiField
    CustomPushButton feedbackShowButton;

    @Override
    public void setFeedbackContent(Widget widget, FeedbackMark mark) {
        feedbackTextPanel.clear();
        feedbackTextPanel.add(widget);
        clearStyleNames();
        addStyleName(typeStyleProvider.getStyleName(mark));
    }

    @Override
    public void showModule() {
        removeStyleName(feedbackStyleNameConstants.QP_FEEDBACK_TEXT_MODULE_WRAPPER_HIDDEN());
    }

    @Override
    public void hideModule() {
        addStyleName(feedbackStyleNameConstants.QP_FEEDBACK_TEXT_MODULE_WRAPPER_HIDDEN());
    }

    @Override
    public void showFeedback() {
        removeStyleName(feedbackStyleNameConstants.QP_FEEDBACK_TEXT_MODULE_HIDDEN());
    }

    @Override
    public void hideFeedback() {
        addStyleName(feedbackStyleNameConstants.QP_FEEDBACK_TEXT_MODULE_HIDDEN());
    }

    @Override
    public void addCloseButtonClickHandler(ClickHandler handler) {
        feedbackCloseButton.addClickHandler(handler);
    }

    @Override
    public void addShowButtonClickHandler(ClickHandler handler) {
        feedbackShowButton.addClickHandler(handler);
    }

    private void clearStyleNames() {
        removeStyleName(feedbackStyleNameConstants.QP_FEEDBACK_ALLOK());
        removeStyleName(feedbackStyleNameConstants.QP_FEEDBACK_OK());
        removeStyleName(feedbackStyleNameConstants.QP_FEEDBACK_WRONG());
    }
}
