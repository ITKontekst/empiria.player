package eu.ydp.empiria.player.client.module.components.multiplepair.structure;

import java.util.List;

import eu.ydp.empiria.player.client.module.abstractmodule.structure.ModuleBean;

public interface MultiplePairBean extends ModuleBean {

	public List<? extends MultiplePairChoiceBean> getFirstChoicesSet();
	
	public List<? extends MultiplePairChoiceBean> getSecondChoicesSet();
	
}