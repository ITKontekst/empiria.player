package eu.ydp.empiria.player.client.module.math;

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

import eu.ydp.empiria.player.client.controller.variables.objects.response.Response;
import eu.ydp.empiria.player.client.controller.variables.objects.response.ResponseValue;
import eu.ydp.empiria.player.client.module.IModule;
import eu.ydp.empiria.player.client.module.gap.GapBase;
import eu.ydp.empiria.player.client.resources.EmpiriaTagConstants;
import eu.ydp.gwtutil.client.StringUtils;

public abstract class MathGapBase extends GapBase implements MathGap {
	
	protected String uid;
	
	protected int index;
	
	protected MathModule parentMathModule;
	
	protected Map<String, String> mathStyles;
	
	public abstract String getValue();
	
	protected void loadUID(){
		uid = getElementAttributeValue(EmpiriaTagConstants.ATTR_UID);
	}
	
	@Override
	protected void updateResponse(boolean userInteract) {
		if (isResponseInMathModule()) {
			getParentMathModule().updateResponseAfterUserAction();
		} else {
			if (showingAnswer) {
				return;
			}

			if (getResponse() != null) {
				if (lastValue != null) {
					getResponse().remove(lastValue);
				}

				lastValue = getValue();
				getResponse().add(lastValue);
				fireStateChanged(userInteract);
			}
		}
	}
	
	@Override
	protected String getCurrentResponseValue() {
		String answer = StringUtils.EMPTY_STRING;
		
		if (isResponseInMathModule()) {
			if (index < getResponse().values.size()) {
				answer = getResponse().values.get(index);
			}
		} else {
			if (getResponse().values.size() > 0) {
				answer = getResponse().values.get(0);
			}
		}
		
		return answer;
	}
	
	protected List<Boolean> getEvaluatedResponse() {
		return getModuleSocket().evaluateResponse(getResponse());
	}
	
	@Override
	protected boolean isResponseCorrect() {
		boolean isCorrect = false;
		List<Boolean> evaluations = getEvaluatedResponse();
		
		if (isResponseInMathModule()) {
			if (index < evaluations.size()) {
				isCorrect = evaluations.get(index);
			}
		} else {
			if (evaluations.size() > 0) {
				isCorrect = evaluations.get(0);				
			}
		}
		
		return isCorrect;
	}
	
	// potrzebne zeby zachowac kompatybilnosc wsteczna - w starszych wersjach contentu responsy byly przypisane do math modulu, po zmianach
	// z taska YPUB-4878 responsy sa przypisane do kazdej gapy matematycznej (dla kazdej z nich odpowiedzi liczone sa osobno)
	protected boolean isResponseInMathModule() {
		boolean isResponseInMathModule = true;
		
		if ( (getParentMathModule() == null) || (getParentMathModule().getResponse() == null) ) {
			isResponseInMathModule = false;
		}

		return isResponseInMathModule;
	}
	
	protected MathModule getParentMathModule() {
		if (parentMathModule == null) {
			IModule parent = this;

			do {
				parent = getModuleSocket().getParent(parent);
			} while ( !(parent instanceof MathModule) );

			parentMathModule = (parent == null) ? null : (MathModule)parent;
		}

		return parentMathModule;
	}
	
	@Override
	protected Response findResponse() {
		Response response;
		
		if (isResponseInMathModule()) {
			response = getParentMathModule().getResponse();
		} else {
			response = super.findResponse();			
		}
		
		return response;
	}
	
	@Override
	public String getCorrectAnswer() {
		List<String> correctAnswers = getCorrectResponseValue().getAnswers();
		return correctAnswers.get(0);
	}
	
	protected ResponseValue getCorrectResponseValue(){
		if (isResponseInMathModule()) {
			return getResponse().correctAnswers.getResponseValue(index);
		} else {
			return getResponse().correctAnswers.getResponseValue(0);
		}
	}

	@Override
	public Widget getContainer() {
		return (Widget) presenter.getContainer();
	}

	@Override
	public String getUid() {
		return (uid == null) ? StringUtils.EMPTY_STRING : uid;
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setGapHeight(int gapHeight) {
		presenter.setHeight(gapHeight, Unit.PX);
	}
	
	public int getGapHeight() {
		return presenter.getOffsetHeight();
	}
	
	@Override
	public void setGapWidth(int gapWidth) {
		presenter.setWidth(gapWidth, Unit.PX);
	}
	
	public int getGapWidth() {
		return presenter.getOffsetWidth();
	}

	@Override
	public void setGapFontSize(int gapFontSize) {
		presenter.setFontSize(gapFontSize, Unit.PX);
	}

	@Override
	public void setMathStyles(Map<String, String> mathStyles) {
		this.mathStyles = mathStyles;
	}

}