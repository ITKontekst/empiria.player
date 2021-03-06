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

package eu.ydp.empiria.player.client.controller.delivery;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import eu.ydp.empiria.player.client.EmpiriaPlayerGWTTestCase;
import eu.ydp.empiria.player.client.PlayerGinjectorFactory;
import eu.ydp.empiria.player.client.controller.communication.ActivityMode;
import eu.ydp.empiria.player.client.controller.communication.FlowOptions;
import eu.ydp.empiria.player.client.controller.communication.PageItemsDisplayMode;
import eu.ydp.empiria.player.client.controller.events.delivery.DeliveryEvent;
import eu.ydp.empiria.player.client.controller.events.delivery.DeliveryEventType;
import eu.ydp.empiria.player.client.controller.extensions.Extension;
import eu.ydp.empiria.player.client.controller.extensions.internal.InternalExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.DeliveryEventsListenerExtension;
import eu.ydp.empiria.player.client.controller.extensions.types.FlowRequestSocketUserExtension;
import eu.ydp.empiria.player.client.controller.flow.request.FlowRequest;
import eu.ydp.empiria.player.client.controller.flow.request.FlowRequestInvoker;
import eu.ydp.empiria.player.client.gin.PlayerGinjector;
import eu.ydp.empiria.player.client.util.file.xml.XmlData;

public class DeliveryEngineGWTTestCase extends EmpiriaPlayerGWTTestCase {

    public void testEngineMode() {
        PlayerGinjector injector = PlayerGinjectorFactory.getNewPlayerGinjectorForGWTTestCase();
        DeliveryEngine de = injector.getDeliveryEngine();
        de.init(JavaScriptObject.createObject());

        assertEquals(EngineMode.NONE.toString(), de.getEngineMode().toString());

        de.load(getAssessmentXMLData(), getItemXMLDatas());

        assertEquals(EngineMode.RUNNING.toString(), de.getEngineMode().toString());
    }

    protected DeliveryEngine de;
    protected FlowRequestInvoker flowInvoker;

    DeliveryEventType[] typesWithToc = {DeliveryEventType.ASSESSMENT_LOADING, DeliveryEventType.ASSESSMENT_LOADED, DeliveryEventType.ASSESSMENT_STARTING,
            DeliveryEventType.PAGE_UNLOADING, DeliveryEventType.PAGE_UNLOADED, DeliveryEventType.PAGE_LOADING, DeliveryEventType.TOC_PAGE_LOADED,
            DeliveryEventType.ASSESSMENT_STARTED, DeliveryEventType.ASSESSMENT_LOADED};
    DeliveryEventType[] typesNoToc = {DeliveryEventType.ASSESSMENT_LOADING, DeliveryEventType.ASSESSMENT_LOADED, DeliveryEventType.ASSESSMENT_STARTING,
            DeliveryEventType.PAGE_UNLOADING, DeliveryEventType.PAGE_UNLOADED, DeliveryEventType.PAGE_LOADING, DeliveryEventType.TEST_PAGE_LOADED,
            DeliveryEventType.ASSESSMENT_STARTED, DeliveryEventType.ASSESSMENT_LOADED};
    DeliveryEventType[] typesPageSwitch = {DeliveryEventType.ASSESSMENT_LOADING, DeliveryEventType.ASSESSMENT_LOADED, DeliveryEventType.ASSESSMENT_STARTING,
            DeliveryEventType.PAGE_UNLOADING, DeliveryEventType.PAGE_UNLOADED, DeliveryEventType.PAGE_LOADING, DeliveryEventType.TOC_PAGE_LOADED,
            DeliveryEventType.ASSESSMENT_STARTED, DeliveryEventType.ASSESSMENT_LOADED};
    DeliveryEventType[] types;
    private int counter = 0;

    public void testDeliveryEventsWithToc() {
        types = typesWithToc;
        PlayerGinjector injector = PlayerGinjectorFactory.getNewPlayerGinjectorForGWTTestCase();
        de = injector.getDeliveryEngine();
        de.init(JavaScriptObject.createObject());
        Extension ext = new MockDeliveryEventsListenerExtension();
        de.setFlowOptions(new FlowOptions(true, true, PageItemsDisplayMode.ONE, ActivityMode.NORMAL));
        de.loadExtension(ext);
        de.load(getAssessmentXMLData(), getItemXMLDatas());

    }

    public void testDeliveryEventsNoToc() {
        types = typesNoToc;
        PlayerGinjector injector = PlayerGinjectorFactory.getNewPlayerGinjectorForGWTTestCase();
        de = injector.getDeliveryEngine();
        de.init(JavaScriptObject.createObject());
        Extension ext = new MockDeliveryEventsListenerExtension();
        de.setFlowOptions(new FlowOptions(false, false, PageItemsDisplayMode.ONE, ActivityMode.NORMAL));
        de.loadExtension(ext);
        de.load(getAssessmentXMLData(), getItemXMLDatas());

    }

    public void testDeliveryEventsPageSwitch() {
        types = typesPageSwitch;
        PlayerGinjector injector = PlayerGinjectorFactory.getNewPlayerGinjectorForGWTTestCase();
        de = injector.getDeliveryEngine();
        de.init(JavaScriptObject.createObject());
        Extension ext = new MockDeliveryEventsListenerExtension();
        de.setFlowOptions(new FlowOptions(true, false, PageItemsDisplayMode.ONE, ActivityMode.NORMAL));
        de.loadExtension(ext);
        de.load(getAssessmentXMLData(), getItemXMLDatas());
    }

    protected void checkEvents(DeliveryEvent flowEvent) {
        if (counter < types.length) {
            assertEquals(types[counter].toString(), flowEvent.getType().toString());
            if (flowEvent.getType() == DeliveryEventType.ASSESSMENT_STARTED) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                    @Override
                    public void execute() {
                        flowInvoker.invokeRequest(new FlowRequest.NavigateFirstItem());
                    }
                });
            }
            counter++;
        }
    }

    protected XmlData getAssessmentXMLData() {

        String assessmentXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><assessmentTest xmlns=\"http://www.ydp.eu/empiria\" identifier=\"RTEST-13\" title=\"Show player supported functionality\"><testPart><assessmentSection identifier=\"sectionA\" title=\"Section A\" visible=\"true\"><assessmentItemRef identifier=\"inline_choice\" href=\"demo/inline_choice.xml\"/></assessmentSection></testPart></assessmentTest>";
        Document assessmentDoc = XMLParser.parse(assessmentXml);
        return new XmlData(assessmentDoc, "");
    }

    protected XmlData[] getItemXMLDatas() {

        Document itemDoc = XMLParser
                .parse("<assessmentItem identifier=\"inlineChoice\" title=\"Interactive text\"><itemBody></itemBody><variableProcessing template=\"default\"/></assessmentItem>");
        XmlData itemData = new XmlData(itemDoc, "");
        XmlData[] itemDatas = new XmlData[1];
        itemDatas[0] = itemData;

        return itemDatas;
    }

    protected class MockDeliveryEventsListenerExtension extends InternalExtension implements DeliveryEventsListenerExtension, FlowRequestSocketUserExtension {

        @Override
        public void init() {
        }

        @Override
        public void onDeliveryEvent(DeliveryEvent flowEvent) {
            checkEvents(flowEvent);
        }

        @Override
        public void setFlowRequestsInvoker(FlowRequestInvoker fri) {
            flowInvoker = fri;
        }

    }
}
