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

package eu.ydp.empiria.player.client.module.colorfill.structure;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.inject.Inject;
import eu.ydp.empiria.player.client.module.abstractmodule.structure.AbstractModuleStructure;
import eu.ydp.gwtutil.client.json.YJsonArray;
import eu.ydp.gwtutil.client.service.json.IJSONService;
import eu.ydp.gwtutil.client.xml.XMLParser;

public class ColorfillInteractionStructure extends AbstractModuleStructure<ColorfillInteractionBean, ColorfillInteractionModuleJAXBParserFactory> {

    @Inject
    private ColorfillInteractionModuleJAXBParserFactory parserFactory;

    @Inject
    private XMLParser xmlParser;

    @Inject
    private IJSONService iJsonService;

    @Override
    public YJsonArray getSavedStructure() {
        return iJsonService.createArray();
    }

    @Override
    protected ColorfillInteractionModuleJAXBParserFactory getParserFactory() {
        return parserFactory;
    }

    @Override
    protected XMLParser getXMLParser() {
        return xmlParser;
    }

    @Override
    protected NodeList getParentNodesForFeedbacks(Document xmlDocument) {
        return null;
    }

    @Override
    protected void prepareStructure(YJsonArray structure) {
    }

}
