package eu.ydp.empiria.player.client.module.mathjax.inline.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import eu.ydp.empiria.player.client.module.mathjax.common.MathJaxView;

public class InlineMathJaxView extends Composite implements MathJaxView {
    private static MathJaxUiBinder uiBinder = GWT.create(MathJaxUiBinder.class);

    interface MathJaxUiBinder extends UiBinder<Widget, InlineMathJaxView> {
    }

    @UiField
    InlineHTML mathContainer;

    public InlineMathJaxView() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void setMmlScript(String script) {
        mathContainer.setHTML(script);
    }
}
