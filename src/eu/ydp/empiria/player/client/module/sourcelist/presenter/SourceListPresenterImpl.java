package eu.ydp.empiria.player.client.module.sourcelist.presenter;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import eu.ydp.empiria.player.client.module.sourcelist.structure.SourceListBean;
import eu.ydp.empiria.player.client.module.sourcelist.view.SourceListView;
import eu.ydp.empiria.player.client.util.events.bus.EventsBus;

public class SourceListPresenterImpl implements SourceListPresenter {

	@Inject
	private SourceListView view;

	@Inject
	private EventsBus eventsBus;

	@Override
	public void setBean(SourceListBean bean) {
		view.setBean(bean);
	}


	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private void init(){
		//TODO bindowanie zdarzen
	}
	@Override
	public void createAndBindUi() {
		view.createAndBindUi();
	}
}