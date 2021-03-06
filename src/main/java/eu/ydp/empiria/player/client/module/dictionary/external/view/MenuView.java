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

package eu.ydp.empiria.player.client.module.dictionary.external.view;

import com.google.common.base.Optional;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import eu.ydp.empiria.player.client.module.dictionary.DictionaryStyleNameConstants;
import eu.ydp.empiria.player.client.module.dictionary.external.components.PasteAwareTextBox;
import eu.ydp.empiria.player.client.module.dictionary.external.components.PasteAwareTextBox.PasteListener;
import eu.ydp.empiria.player.client.module.dictionary.external.components.PushButtonWithIndex;
import eu.ydp.empiria.player.client.module.dictionary.external.components.ScrollbarPanel;
import eu.ydp.empiria.player.client.module.dictionary.external.controller.*;
import eu.ydp.empiria.player.client.module.dictionary.external.view.visibility.VisibilityChanger;
import eu.ydp.empiria.player.client.module.dictionary.external.view.visibility.VisibilityClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class MenuView extends Composite implements VisibilityClient {

    private static final int PASSWORDS_COUNT_INIT = 25;
    private static final int PASSWORDS_COUNT_EXTENSION = 25;

    @UiField
    PasteAwareTextBox searchTextBox;
    @UiField
    PushButton searchButton;
    @UiField
    FlowPanel quickLettersPanel;
    @UiField
    FlowPanel passwordsPanelContainer;
    @UiField
    FlowPanel passwordsPanel;
    @UiField
    Panel passwordsListPanel;
    @UiField
    FlowPanel passwordsListPanelBody;
    @UiField
    PushButton exitButton;
    @UiField(provided = true)
    ScrollbarPanel scrollbarPanel;

    private List<PushButtonWithIndex> passwordButtons;
    private WordsResult wordsResultString;
    private String prevLetter;
    private PushButton prevSelectedButton;
    private PushButton showMoreButton;

    @Inject
    private DictionaryStyleNameConstants styleNameConstants;
    @Inject
    private Provider<WordsSocket> passwordsSocket;
    @Inject
    private EntriesController entriesController;
    @Inject
    private VisibilityChanger visibilityChanger;
    @Inject
    private MenuViewNative menuViewNative;

    private final Timer fillTimer = new Timer() {

        @Override
        public void run() {
            refillPasswords();
        }
    };

    private static MenuViewUiBinder uiBinder = GWT.create(MenuViewUiBinder.class);

    interface MenuViewUiBinder extends UiBinder<Widget, MenuView> {
    }

    @Inject
    public MenuView(ScrollbarPanel scrollbarPanel) {
        this.scrollbarPanel = scrollbarPanel;
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void show() {
        visibilityChanger.show(this);
    }

    public void hide() {
        visibilityChanger.hide(this);
    }

    @Override
    public int getScrollTop() {
        return passwordsListPanel.getElement().getScrollTop();
    }

    @Override
    public void setScrollTop(int scroll) {
        passwordsListPanel.getElement().setScrollTop(scroll);
    }

    @Override
    public Style getElementStyle() {
        return getElement().getStyle();
    }

    public void init() {
        String initialPassword = "";

        fillQuickLetters();

        fillPasswords(initialPassword);
        if (Options.getViewType() == ViewType.FULL) {
            showExplanationOfFirstItem(false);
        }

        passwordsListPanel.addDomHandler(new ScrollHandler() {

            @Override
            public void onScroll(ScrollEvent event) {
                scrollbarPanel.updateScrollBar(passwordsPanel, passwordsListPanel);

            }
        }, ScrollEvent.getType());

        searchTextBox.addPasteListener(new PasteListener() {

            @Override
            public void onPaste() {
                startTimer();
            }
        });
    }

    @UiHandler("exitButton")
    public void onExitClick(ClickEvent event) {
        menuViewNative.exitJs();
    }

    @UiHandler("searchTextBox")
    public void onSearchKeyPress(KeyPressEvent event) {
        if (Character.isLetter(event.getCharCode())) {
            startTimer();
        }
    }

    @UiHandler("searchTextBox")
    public void onSearchKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_BACKSPACE) {
            startTimer();
        } else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            showExplanationOfFirstItem(true);
        }
    }

    @UiHandler("searchButton")
    public void onSearchButtonClick(ClickEvent event) {
        showExplanationOfFirstItem(true);
    }

    private void fillQuickLetters() {
        Iterator<String> letter = passwordsSocket.get().getLetters().iterator();
        if (Options.getViewType() == ViewType.FULL) {
            fillQuickLettersFull(letter);
        } else {
            fillQuickLettersHalf(letter);
        }
    }

    private void fillQuickLettersHalf(Iterator<String> letter) {
        Panel qlList = new FlowPanel();
        qlList.setStyleName(styleNameConstants.QP_DICTIONARY_QUICKLETTERS_LIST());
        while (letter.hasNext()) {
            final String currLetter = letter.next();
            PushButton quickLetterButton = new PushButton(currLetter);
            quickLetterButton.setStyleName(styleNameConstants.QP_DICTIONARY_QUICKLETTERS_BUTTON());
            quickLetterButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    clearTextBox();
                    fillPasswords(currLetter);
                }
            });
            qlList.add(quickLetterButton);
        }
        quickLettersPanel.add(qlList);
    }

    private void fillQuickLettersFull(Iterator<String> letter) {
        Grid quickLettersTable = new Grid(2, 14);
        int row = 0;
        int col = 0;
        while (letter.hasNext()) {
            final String currLetter = letter.next();
            PushButton quickLetterButton = new PushButton(currLetter);
            quickLetterButton.setStyleName(styleNameConstants.QP_DICTIONARY_QUICKLETTERS_BUTTON());
            quickLetterButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    clearTextBox();
                    fillPasswords(currLetter);
                    showExplanationOfFirstItem(true);
                }
            });

            quickLettersTable.setWidget(row, col, quickLetterButton);
            if (row == 0) {
                row = 1;
            } else {
                row = 0;
                col++;
            }
        }
        quickLettersPanel.add(quickLettersTable);
    }

    private void startTimer() {
        fillTimer.cancel();
        fillTimer.schedule(200);
    }

    private void clearTextBox() {
        searchTextBox.setText("");
    }

    private void refillPasswords() {
        fillPasswords(searchTextBox.getText());
    }

    private void fillPasswords(String text) {
        prevLetter = text;
        Optional<WordsResult> wordsResult = passwordsSocket.get().getWords(text);
        passwordButtons = new ArrayList<>();
        if (wordsResult.isPresent()) {
            wordsResultString = wordsResult.get();
            passwordsListPanelBody.clear();
            scrollbarPanel.setScrollTop(passwordsPanel.getElement(), 0);
            prevSelectedButton = null;
            fillOptions(wordsResultString.getList(), wordsResultString.getIndex(), PASSWORDS_COUNT_INIT);
        }
    }

    private void fillOptions(List<String> pwds, int firstPasswordIndex, int count) {

        int alreadyShownOptionsCount = passwordButtons.size();

        for (int i = alreadyShownOptionsCount; i < pwds.size() && i < alreadyShownOptionsCount + count; i++) {
            final PushButtonWithIndex currPwd = new PushButtonWithIndex(pwds.get(i));
            currPwd.setIndex(firstPasswordIndex + i);
            currPwd.setStylePrimaryName(styleNameConstants.QP_DICTIONARY_PASSWORD_BUTTON());
            currPwd.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectButton(currPwd, true);
                }
            });
            passwordButtons.add(currPwd);
            passwordsListPanelBody.insert(currPwd, passwordButtons.size() - 1);
        }

        if (pwds.size() <= passwordButtons.size()) {
            passwordsListPanelBody.remove(getShowMoreButton());
        } else if (getShowMoreButton().getParent() != passwordsListPanelBody) {
            passwordsListPanelBody.add(getShowMoreButton());
        }

    }

    private PushButton getShowMoreButton() {
        if (showMoreButton != null) {
            return showMoreButton;
        }
        showMoreButton = new PushButton("Show more");
        showMoreButton.setStylePrimaryName(styleNameConstants.QP_DICTIONARY_SHOW_MORE_BUTTON());
        showMoreButton.getElement().getElementsByTagName("input").getItem(0).getStyle().setPosition(Position.RELATIVE);
        showMoreButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showMore();
            }
        });
        return showMoreButton;
    }

    private void showMore() {
        fillOptions(wordsResultString.getList(), wordsResultString.getIndex(), PASSWORDS_COUNT_EXTENSION);

    }

    private void showExplanationOfFirstItem(boolean playSound) {
        if (passwordButtons.size() > 0) {
            selectButton(passwordButtons.get(0), playSound);
        }
    }

    private void showExplanation(String pwd, int index, boolean playSound) {
        entriesController.loadEntry(pwd, index, playSound);
    }

    private void selectButton(PushButtonWithIndex buttonToSelect, boolean playSound) {
        unselectAllButtons();
        prevSelectedButton = buttonToSelect;
        buttonToSelect.addStyleDependentName("selected");
        showExplanation(buttonToSelect.getText(), buttonToSelect.getIndex(), playSound);
    }

    private void unselectAllButtons() {
        if (prevSelectedButton != null) {
            prevSelectedButton.removeStyleDependentName("selected");
        }
    }
}
