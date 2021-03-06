/*
 * Copyright 2017 Young Digital Planet S.A.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eu.ydp.empiria.player.client.controller.extensions.jswrappers;

import com.google.gwt.core.client.JavaScriptObject;
import eu.ydp.empiria.player.client.controller.extensions.ExtensionType;
import eu.ydp.empiria.player.client.controller.extensions.internal.bonusprogress.ProgressBonusConfig;
import eu.ydp.empiria.player.client.controller.extensions.internal.bonusprogress.ProgressBonusExtension;
import eu.ydp.empiria.player.client.controller.extensions.internal.bonusprogress.js.ProgressBonusConfigJs;

public class JsProgressBonusExtension extends AbstractJsExtension implements ProgressBonusExtension {

    @Override
    public ExtensionType getType() {
        return ExtensionType.EXTENSION_PROGRESS_BONUS;
    }

    @Override
    public void init() {
    }

    @Override
    public ProgressBonusConfig getProgressBonusConfig() {
        JavaScriptObject configJso = getConfigNative(extensionJsObject);
        ProgressBonusConfigJs configJs = configJso.cast();
        return ProgressBonusConfig.fromJs(configJs);
    }

    @Override
    public String getProgressBonusId() {
        return getProgressBonusNative(extensionJsObject);
    }

    private native String getProgressBonusNative(JavaScriptObject extensionJsObject)/*-{
        return extensionJsObject.getProgressBonusId();
    }-*/;

    private native JavaScriptObject getConfigNative(JavaScriptObject extensionJsObject)/*-{
        return extensionJsObject.getProgressBonusConfig();
    }-*/;
}
