package eu.ydp.empiria.player.client.module.pageinpage;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.inject.Inject;
import eu.ydp.empiria.player.client.PlayerGinjectorFactory;
import eu.ydp.empiria.player.client.controller.multiview.MultiPageController;
import eu.ydp.empiria.player.client.gin.factory.ModuleProviderFactory;
import eu.ydp.empiria.player.client.module.Factory;
import eu.ydp.empiria.player.client.module.SimpleModuleBase;
import eu.ydp.empiria.player.client.resources.StyleNameConstants;

public class PageInPageModule extends SimpleModuleBase {

    private Panel pagePanel;

    @Inject
    private StyleNameConstants styleNames;
    @Inject
    private MultiPageController controller;

    @Override
    public void initModule(Element element) {
    }

    @Override
    public Widget getView() {
        if (pagePanel == null) {
            pagePanel = controller.getView();
            pagePanel.setStyleName(styleNames.QP_PAGE_IN_PAGE());
        }
        return pagePanel;
    }
}
