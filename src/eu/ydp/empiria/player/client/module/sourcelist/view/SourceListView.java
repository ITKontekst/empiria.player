package eu.ydp.empiria.player.client.module.sourcelist.view;

import com.google.gwt.user.client.ui.IsWidget;

import eu.ydp.empiria.player.client.module.sourcelist.structure.SourceListBean;

public interface SourceListView extends IsWidget{
	public void setBean(SourceListBean bean);
	public void createAndBindUi();

}