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

package eu.ydp.empiria.player.client.module.bonus;

import com.google.inject.Inject;
import eu.ydp.empiria.player.client.module.bonus.popup.BonusPopupPresenter;
import eu.ydp.empiria.player.client.resources.EmpiriaPaths;
import eu.ydp.gwtutil.client.util.geom.Size;

public class ImageBonus implements BonusWithAsset {

    @Inject
    private BonusPopupPresenter presenter;
    @Inject
    private EmpiriaPaths empiriaPaths;

    private String url;
    private Size size;

    @Override
    public void setAsset(String url, Size size) {
        this.url = empiriaPaths.getCommonsFilePath(url);
        this.size = size;
    }

    @Override
    public void execute() {
        presenter.showImage(url, size);
    }

}
