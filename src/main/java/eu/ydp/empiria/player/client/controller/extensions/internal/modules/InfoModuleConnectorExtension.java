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

package eu.ydp.empiria.player.client.controller.extensions.internal.modules;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import eu.ydp.empiria.player.client.controller.events.delivery.DeliveryEvent;
import eu.ydp.empiria.player.client.controller.events.delivery.DeliveryEventType;
import eu.ydp.empiria.player.client.controller.extensions.types.DeliveryEventsListenerExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.ModuleConnectorExtension;
import eu.ydp.empiria.player.client.module.core.creator.AbstractModuleCreator;
import eu.ydp.empiria.player.client.module.core.base.IModule;
import eu.ydp.empiria.player.client.module.core.creator.ModuleCreator;
import eu.ydp.empiria.player.client.module.ModuleTagName;
import eu.ydp.empiria.player.client.module.info.InfoModule;
import eu.ydp.empiria.player.client.module.info.InfoModuleUnloadListener;

import java.util.List;

public class InfoModuleConnectorExtension extends ModuleExtension implements ModuleConnectorExtension, DeliveryEventsListenerExtension {

    @Inject
    private Provider<InfoModule> infoModuleProvider;
    protected List<InfoModule> modules = Lists.newArrayList();

    @Override
    public ModuleCreator getModuleCreator() {
        return new AbstractModuleCreator(false, true) {
            @Override
            public IModule createModule() {
                final InfoModule infoModule = infoModuleProvider.get();
                infoModule.setModuleUnloadListener(new InfoModuleUnloadListener() {

                    @Override
                    public void moduleUnloaded() {
                        modules.remove(infoModule);
                    }
                });
                modules.add(infoModule);
                return infoModule;
            }
        };
    }

    @Override
    public String getModuleNodeName() {
        return ModuleTagName.INFO.tagName();
    }

    @Override
    public void onDeliveryEvent(DeliveryEvent event) {
        if (event.getType() == DeliveryEventType.TEST_PAGE_LOADED) {
            for (InfoModule im : modules) {
                im.update();
            }
        }
    }

}
