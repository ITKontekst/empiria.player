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

package eu.ydp.empiria.player.client.gin.module.tutor;

import com.google.gwt.xml.client.Element;
import com.google.inject.Inject;
import com.google.inject.Provider;
import eu.ydp.gwtutil.client.gin.scopes.module.ModuleScoped;

public class TutorIdProvider implements Provider<String> {

    private static final String TUTOR_ID_ATTRIBUTE_NAME = "tutorId";

    @Inject
    @ModuleScoped
    private Element xmlElement;

    @Override
    public String get() {
        String tutorId = xmlElement.getAttribute(TUTOR_ID_ATTRIBUTE_NAME);
        assert tutorId != null;
        return tutorId;
    }

}
