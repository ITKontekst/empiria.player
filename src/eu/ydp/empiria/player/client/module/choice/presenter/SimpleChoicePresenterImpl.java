package eu.ydp.empiria.player.client.module.choice.presenter;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import eu.ydp.empiria.player.client.controller.body.InlineBodyGeneratorSocket;
import eu.ydp.empiria.player.client.gin.factory.SimpleChoiceViewFactory;
import eu.ydp.empiria.player.client.module.MarkAnswersMode;
import eu.ydp.empiria.player.client.module.MarkAnswersType;
import eu.ydp.empiria.player.client.module.choice.ChoiceModuleListener;
import eu.ydp.empiria.player.client.module.choice.providers.SimpleChoiceStyleProvider;
import eu.ydp.empiria.player.client.module.choice.structure.SimpleChoiceBean;
import eu.ydp.empiria.player.client.module.choice.view.SimpleChoiceView;
import eu.ydp.empiria.player.client.module.components.choicebutton.ChoiceButtonBase;

public class SimpleChoicePresenterImpl implements SimpleChoicePresenter {

	private static final String STYLE_CHOICE_SINGLE = "choice-single";

	private static final String STYLE_CHOICE_MULTI = "choice-multi";

	private boolean isMulti;

	private InlineBodyGeneratorSocket bodyGenerator;

	private ChoiceModuleListener listener;

	private SimpleChoiceView view;

	SimpleChoiceViewFactory simpleChoiceViewFactory;

	@Inject
	public SimpleChoicePresenterImpl(SimpleChoiceViewFactory simpleChoiceViewFactory, @Assisted SimpleChoiceBean option,
			@Assisted InlineBodyGeneratorSocket bodyGenerator) {
		this.simpleChoiceViewFactory = simpleChoiceViewFactory;
		bindView();
		this.bodyGenerator = bodyGenerator;

		installChildren(option);
	}

	private void installChildren(SimpleChoiceBean choiceOption) {
		isMulti = choiceOption.isMulti();

		ChoiceButtonBase button = createButton();

		view.setButton(button);

		createAndInstallContent(choiceOption);
	}

	private ChoiceButtonBase createButton() {
		if (isMulti) {
			return simpleChoiceViewFactory.getMultiChoiceButton(STYLE_CHOICE_MULTI);
		} else {
			return simpleChoiceViewFactory.getSingleChoiceButton(STYLE_CHOICE_SINGLE);
		}
	}

	private void createAndInstallContent(SimpleChoiceBean choiceOption) {
		Widget contentWidget = bodyGenerator.generateInlineBody(choiceOption.getContent().getValue(), true);
		view.setContent(contentWidget);
	}

	public void onChoiceClick() {
		listener.onChoiceClick(this);
	}

	public void setSelected(boolean select) {
		view.setSelected(select);
	}

	public boolean isSelected() {
		return view.isSelected();
	}

	public Widget getFeedbackPlaceHolder() {
		return view.getFeedbackPlaceHolder();
	}

	public void setLocked(boolean locked) {
		view.setLocked(locked);
	}

	public void reset() {
		view.reset();
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private void bindView() {
		SimpleChoiceStyleProvider styleProvider;
		if (isMulti()) {
			styleProvider = simpleChoiceViewFactory.getMultiChoiceStyleProvider();
		} else {
			styleProvider = simpleChoiceViewFactory.getSingleChoiceStyleProvider();
		}
		view = simpleChoiceViewFactory.getSimpleChoiceView(this, styleProvider);
	}

	@Override
	public void markAnswer(MarkAnswersType type, MarkAnswersMode mode) {
		switch (mode) {
		case MARK:
			markAnswer(type);
			break;
		case UNMARK:
			unmarkAnswer(type);
			break;
		}
	}

	private void unmarkAnswer(MarkAnswersType type) {
		switch (type) {
		case CORRECT:
			view.unmarkCorrect();
			break;
		case WRONG:
			view.unmarkWrong();
			break;
		}
	}

	private void markAnswer(MarkAnswersType type) {
		switch (type) {
		case CORRECT:
			view.markCorrect();
			break;
		case WRONG:
			view.markWrong();
			break;
		}
	}

	@Override
	public void setListener(ChoiceModuleListener listener) {
		this.listener = listener;
	}

	@Override
	public boolean isMulti() {
		return isMulti;
	}
}
