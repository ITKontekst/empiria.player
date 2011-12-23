package eu.ydp.empiria.player.client.controller.extensions.internal;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import eu.ydp.empiria.player.client.controller.data.DataSourceDataSupplier;
import eu.ydp.empiria.player.client.controller.events.delivery.DeliveryEvent;
import eu.ydp.empiria.player.client.controller.events.delivery.DeliveryEventType;
import eu.ydp.empiria.player.client.controller.extensions.ExtensionType;
import eu.ydp.empiria.player.client.controller.extensions.types.AssessmentHeaderViewExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.DataSourceDataSocketUserExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.DeliveryEventsListenerExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.FlowDataSocketUserExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.FlowRequestSocketUserExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.ViewExtension;
import eu.ydp.empiria.player.client.controller.flow.FlowDataSupplier;
import eu.ydp.empiria.player.client.controller.flow.request.FlowRequest;
import eu.ydp.empiria.player.client.controller.flow.request.IFlowRequestInvoker;
import eu.ydp.empiria.player.client.util.localisation.LocalePublisher;
import eu.ydp.empiria.player.client.util.localisation.LocaleVariable;
import eu.ydp.empiria.player.client.view.sockets.ViewSocket;

public class DefaultAssessmentHeaderViewExtension extends InternalExtension implements AssessmentHeaderViewExtension,  
	FlowRequestSocketUserExtension, DataSourceDataSocketUserExtension, FlowDataSocketUserExtension, DeliveryEventsListenerExtension {

	protected DataSourceDataSupplier dataSourceDataSupplier; 
	protected FlowDataSupplier flowDataSupplier;
	protected IFlowRequestInvoker flowRequestInvoker;
	
	private Panel comboPanel;
	private Label comboLabel;
	private ListBox	comboListBox;
	private Label assessmentTitleLabel;
	private Panel viewPanel;
	
	private int integrationCounter;
	
	public DefaultAssessmentHeaderViewExtension(){
		integrationCounter = 0;
	}

	@Override
	public ExtensionType getType() {
		return ExtensionType.EXTENSION_VIEW_ASSESSMENT_HEADER;
	}

	@Override
	public void init() {
		createView();
	}

	@Override
	public void setDataSourceDataSupplier(DataSourceDataSupplier supplier) {
		dataSourceDataSupplier = supplier;
		integrationCounter++;
	}

	@Override
	public void setFlowRequestsInvoker(IFlowRequestInvoker fri) {
		flowRequestInvoker = fri;
		integrationCounter++;
	}

	@Override
	public void setFlowDataSupplier(FlowDataSupplier supplier) {
		flowDataSupplier = supplier;
		integrationCounter++;
	}

	@Override
	public ViewSocket getAssessmentHeaderViewSocket() {
		return new ViewSocket() {
			
			@Override
			public Widget getView() {
				return viewPanel;
			}
		};
	}
	
	protected void createView(){
		createCombo();
		createTitle();
		
		viewPanel = new HorizontalPanel();
		viewPanel.add(assessmentTitleLabel);
		viewPanel.add(comboPanel);
	}
	
	protected void createCombo(){

	    // PAGES COMBO

	    final boolean showToC = flowDataSupplier.getFlowOptions().showToC;
	    final boolean showSummary = flowDataSupplier.getFlowOptions().showSummary;
	    comboListBox = new ListBox();
	    comboListBox.setVisibleItemCount(1);
	    comboListBox.setStyleName("qp-page-counter-list");
	    if (showToC)
	    	comboListBox.addItem(LocalePublisher.getText(LocaleVariable.COMBO_TOC));
	    for (int p = 0 ; p < dataSourceDataSupplier.getItemsCount() ; p ++)
	    	comboListBox.addItem(String.valueOf(p+1));	   
	    if (showSummary)
	    	comboListBox.addItem(LocalePublisher.getText(LocaleVariable.COMBO_SUMMARY)); 
	    comboListBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				
				boolean firstIndex = ((ListBox)event.getSource()).getSelectedIndex() == 0;
				boolean lastIndex = ((ListBox)event.getSource()).getSelectedIndex() == ((ListBox)event.getSource()).getItemCount()-1;
				int pagesCount = ((ListBox)event.getSource()).getItemCount();
				if (showToC)
					pagesCount--;
				if (showSummary)
					pagesCount--;
				
				if (firstIndex){
					if (showToC)
						flowRequestInvoker.invokeRequest( new FlowRequest.NavigateToc() );
					else
						flowRequestInvoker.invokeRequest( new FlowRequest.NavigateFirstItem() );
				} else if (lastIndex){
					if (showSummary)
						flowRequestInvoker.invokeRequest( new FlowRequest.NavigateSummary() );
					else
						flowRequestInvoker.invokeRequest( new FlowRequest.NavigateLastItem() );
				} else {
					flowRequestInvoker.invokeRequest( new FlowRequest.NavigateGotoItem(  ((ListBox)event.getSource()).getSelectedIndex() - ((showToC)?1:0)  ));
				}
			}
		});
	        
	    
	    comboLabel = new Label("/" + dataSourceDataSupplier.getItemsCount());
	    comboLabel.setStyleName("qp-page-counter-count");
	    
	    comboPanel = new FlowPanel();
	    comboPanel.setStyleName("qp-page-counter");
	    comboPanel.add(comboListBox);
	    comboPanel.add(comboLabel);
	}
	
	protected void createTitle(){
		assessmentTitleLabel = new Label(dataSourceDataSupplier.getAssessmentTitle());
		assessmentTitleLabel.setStyleName("qp-assessment-title");
	}

	@Override
	public void onDeliveryEvent(DeliveryEvent deliveryEvent) {
		if (deliveryEvent.getType() == DeliveryEventType.TEST_PAGE_LOADED  ||  
			deliveryEvent.getType() == DeliveryEventType.TOC_PAGE_LOADED  ||
			deliveryEvent.getType() == DeliveryEventType.SUMMARY_PAGE_LOADED){
			updateControls();
		}
			
	}
	
	private void updateControls(){

		if (flowDataSupplier.getFlowOptions().showToC)
			setComboPageIndex(flowDataSupplier.getCurrentPageIndex()+1);
		else
			setComboPageIndex(flowDataSupplier.getCurrentPageIndex());
	}
	
	private void setComboPageIndex(int index){
		if (comboListBox != null)
			comboListBox.setSelectedIndex(index);
	}

}