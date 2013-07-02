package eu.ydp.empiria.player.client.module.draggap.presenter;

import java.util.List;

import javax.annotation.PostConstruct;

import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import eu.ydp.empiria.player.client.controller.variables.processor.AnswerEvaluationSupplier;
import eu.ydp.empiria.player.client.gin.scopes.module.ModuleScoped;
import eu.ydp.empiria.player.client.gin.scopes.page.PageScoped;
import eu.ydp.empiria.player.client.module.MarkAnswersMode;
import eu.ydp.empiria.player.client.module.MarkAnswersType;
import eu.ydp.empiria.player.client.module.ModuleSocket;
import eu.ydp.empiria.player.client.module.ShowAnswersType;
import eu.ydp.empiria.player.client.module.draggap.DragGapModuleModel;
import eu.ydp.empiria.player.client.module.draggap.structure.DragGapBean;
import eu.ydp.empiria.player.client.module.draggap.view.DragGapDropHandler;
import eu.ydp.empiria.player.client.module.draggap.view.DragGapStartDragHandler;
import eu.ydp.empiria.player.client.module.draggap.view.DragGapView;
import eu.ydp.empiria.player.client.module.gap.DropZoneGuardian;
import eu.ydp.empiria.player.client.module.selection.model.UserAnswerType;
import eu.ydp.empiria.player.client.resources.StyleNameConstants;
import eu.ydp.empiria.player.client.util.dom.drag.DragDropHelper;
import eu.ydp.empiria.player.client.util.dom.drag.DroppableObject;

public class DragGapPresenterImpl implements DragGapPresenter {

	@Inject
	@ModuleScoped
	private DragGapModuleModel model;

	@Inject
	private DragGapView view;

	@Inject
	@PageScoped
	private AnswerEvaluationSupplier answerEvaluationSupplier;

	private DragGapBean bean;
	private ModuleSocket socket;

	private final StyleNameConstants styleNames;

	private final DroppableObject<TextBox> droppable;
	private DropZoneGuardian dropZoneGuardian;

	@Inject
	public DragGapPresenterImpl(DragDropHelper dragDropHelper, StyleNameConstants styleNameConstants) {
		droppable = dragDropHelper.enableDropForWidget(new TextBox());
		styleNames = styleNameConstants;
	}
	
	@PostConstruct
	public void postConstruct(){
		dropZoneGuardian = new DropZoneGuardian(droppable, view.asWidget(), styleNames);
	}
	
	@Override
	public void bindView() {
		view.updateStyle(UserAnswerType.DEFAULT);
	}

	@Override
	public void reset() {
		view.removeContent();
		model.reset();
		view.updateStyle(UserAnswerType.DEFAULT);
	}

	@Deprecated
	@Override
	public void setModel(DragGapModuleModel model) {
	}

	@Override
	public void setModuleSocket(ModuleSocket socket) {
		this.socket = socket;
	}

	@Override
	public void setBean(DragGapBean bean) {
		this.bean = bean;
	}

	@Override
	public void setLocked(boolean locked) {
		view.lock(locked);
	}

	@Override
	public void markAnswers(MarkAnswersType type, MarkAnswersMode mode) {
		List<Boolean> evaluatedAnswers = answerEvaluationSupplier
				.evaluateAnswer(model.getResponse());

		if (evaluatedAnswers.isEmpty()){
			view.updateStyle(UserAnswerType.NONE);
		}else{
			Boolean isAnswerCorrect = evaluatedAnswers.get(0);

			if (mode == MarkAnswersMode.MARK) {
				if (type == MarkAnswersType.CORRECT && isAnswerCorrect) {
					view.updateStyle(UserAnswerType.CORRECT);
				} else if (type == MarkAnswersType.WRONG && !isAnswerCorrect) {
					view.updateStyle(UserAnswerType.WRONG);
				}
			} else if (mode == MarkAnswersMode.UNMARK) {
				view.updateStyle(UserAnswerType.DEFAULT);
			}
		}
	}

	@Override
	public void showAnswers(ShowAnswersType mode) {
		List<String> answers;
		if (mode == ShowAnswersType.CORRECT) {
			answers = model.getCorrectAnswers();
		} else if (mode == ShowAnswersType.USER) {
			answers = model.getCurrentAnswers();
		} else {
			return;
		}
		String answerToSet = answers.get(0);
		view.setContent(answerToSet);
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public void setContent(String itemContent) {
		view.setContent(itemContent);
	}

	@Override
	public void removeContent() {
		view.removeContent();
	}

	@Override
	public void lockDropZone() {
		dropZoneGuardian.lockDropZone();
	}

	@Override
	public void unlockDropZone() {
		dropZoneGuardian.unlockDropZone();
	}


	@Override
	public void setDragStartHandler(DragGapStartDragHandler dragGapStartDragHandler) {
		view.setDragStartHandler(dragGapStartDragHandler);
	}

	@Override
	public void setDropHandler(DragGapDropHandler dragGapDropHandler) {
		view.setDropHandler(dragGapDropHandler);
	}
}
