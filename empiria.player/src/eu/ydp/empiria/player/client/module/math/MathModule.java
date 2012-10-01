package eu.ydp.empiria.player.client.module.math;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;

import eu.ydp.empiria.player.client.PlayerGinjector;
import eu.ydp.empiria.player.client.components.ExListBoxChangeListener;
import eu.ydp.empiria.player.client.module.Factory;
import eu.ydp.empiria.player.client.module.ModuleJsSocketFactory;
import eu.ydp.empiria.player.client.module.OneViewInteractionModuleBase;
import eu.ydp.empiria.player.client.module.binding.BindingGroupIdentifier;
import eu.ydp.empiria.player.client.module.binding.DefaultBindingGroupIdentifier;
import eu.ydp.empiria.player.client.resources.StyleNameConstants;
import eu.ydp.empiria.player.client.util.events.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.player.PlayerEvent;
import eu.ydp.empiria.player.client.util.events.player.PlayerEventHandler;
import eu.ydp.empiria.player.client.util.events.player.PlayerEventTypes;

public class MathModule extends OneViewInteractionModuleBase implements Factory<MathModule> {

	protected AbsolutePanel outerPanel;
	protected FlowPanel mainPanel;
	protected AbsolutePanel listBoxesLayer;
	private final EventsBus eventsBus = PlayerGinjector.INSTANCE.getEventsBus();

	protected List<MathGap> gaps;
	protected BindingGroupIdentifier widthBindingIdentifier;

	protected boolean locked = false;
	protected StyleNameConstants styleNames = PlayerGinjector.INSTANCE.getStyleNameConstants();
	private MathModuleHelper helper;

	@Override
	public void installViews(List<HasWidgets> placeholders) {

		eventsBus.addHandler(PlayerEvent.getType(PlayerEventTypes.BEFORE_FLOW), new PlayerEventHandler() {

			@Override
			public void onPlayerEvent(PlayerEvent event) {
				if(event.getType()==PlayerEventTypes.BEFORE_FLOW){
					updateResponse(false);
				}
			}
		});

		outerPanel = new AbsolutePanel();
		outerPanel.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		outerPanel.setStyleName(styleNames.QP_MATHINTERACTION());
		mainPanel = new FlowPanel();
		mainPanel.setStyleName(styleNames.QP_MATHINTERACTION_INNER());
		outerPanel.add(mainPanel, 0, 0);

		applyIdAndClassToView(mainPanel);
		placeholders.get(0).add(outerPanel);

		if (getModuleElement().hasAttribute("widthBindingGroup")){
			widthBindingIdentifier = new DefaultBindingGroupIdentifier(getModuleElement().getAttribute("widthBindingGroup"));
		}

		helper = new MathModuleHelper(getModuleElement(), getModuleSocket(), getResponse(), this);
		gaps = helper.initGaps();

		initGapListeners();
	}

	@Override
	public void onBodyLoad() {
	}

	@Override
	public void onBodyUnload() {
	}

	@Override
	public void onSetUp() {

		listBoxesLayer = new AbsolutePanel();
		listBoxesLayer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		listBoxesLayer.setStyleName(styleNames.QP_MATHINTERACTION_GAPS());
		outerPanel.add(listBoxesLayer);
		helper.placeGaps(listBoxesLayer);

		updateResponse(false);
	}

	@SuppressWarnings("PMD")
	private void initGapListeners(){
		for (final MathGap gap : gaps){
			if (gap instanceof TextEntryGap){
				((TextEntryGap)gap).getTextBox().addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						updateResponse(true);
					}
				});
				((TextEntryGap)gap).getTextBox().addBlurHandler(new BlurHandler() {

					@Override
					public void onBlur(BlurEvent event) {
						updateResponse(true);
					}
				});
			} else if (gap instanceof InlineChoiceGap){
				((InlineChoiceGap)gap).getListBox().setChangeListener(new ExListBoxChangeListener() {

					@Override
					public void onChange() {
						updateResponse(true);
					}
				});
			}
		}
	}

	@Override
	public void onStart() {
		helper.calculateActualSizes();
		helper.initMath(mainPanel);

		listBoxesLayer.setWidth(mainPanel.getOffsetWidth() + "px");
		listBoxesLayer.setHeight(mainPanel.getOffsetHeight() + "px");

		helper.positionGaps(listBoxesLayer);
	}

	@Override
	public void onClose() {
	}

	@Override
	public void markAnswers(boolean mark) {
		helper.markAnswers(gaps, mark);
	}

	@Override
	public void showCorrectAnswers(boolean show) {
		helper.showCorrectAnswers(gaps, show);
	}

	@Override
	public void lock(boolean lock) {
		locked = lock;
		for (MathGap mathGap : gaps) {
			mathGap.setEnabled(!lock);
		}
	}

	@Override
	public void reset() {		
		for (MathGap mathGap : gaps) {
			mathGap.reset();
			mathGap.unmark();
		}
		updateResponse(false);
	}

	@Override
	public JSONArray getState() {
		JSONArray arr = new JSONArray();
		for (int i = 0 ; i < getResponse().values.size() ; i ++){
			JSONString val = new JSONString(getResponse().values.get(i));
			arr.set(i,  val);
		}
		return arr;
	}

	@Override
	public void setState(JSONArray newState) {
		if (newState.isArray() != null){
			for (int i = 0 ; i < gaps.size() ; i ++){
				gaps.get(i).setValue(newState.get(i).isString().stringValue());
			}
		}
		updateResponse(false);
	}

	@Override
	public JavaScriptObject getJsSocket() {
		return ModuleJsSocketFactory.createSocketObject(this);
	}

	private void updateResponse(boolean userInteract){
		if (helper.updateResponse(gaps, userInteract)) {
			fireStateChanged(userInteract);
		}
	}

	@Override
	public MathModule getNewInstance() {
		return new MathModule();
	}

}
