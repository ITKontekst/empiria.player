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

package eu.ydp.empiria.player.client.module.object.impl;

import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.media.client.MediaBase;
import com.google.gwt.user.client.ui.FlowPanel;
import eu.ydp.empiria.player.client.PlayerGinjectorFactory;
import eu.ydp.empiria.player.client.module.media.MediaWrapper;
import eu.ydp.empiria.player.client.util.events.internal.bus.EventsBus;

public class HTML5VideoImpl extends FlowPanel implements Video {

    protected eu.ydp.empiria.player.client.media.Video video;
    protected final EventsBus eventsBus = PlayerGinjectorFactory.getPlayerGinjector().getEventsBus();
    private MediaWrapper<?> eventBusSource;

    public HTML5VideoImpl() {
        video = eu.ydp.empiria.player.client.media.Video.createIfSupported();
        video.setPreload(MediaElement.PRELOAD_METADATA);
        video.setLoop(false);
        add(video);

    }

    public void setSrc(String src) {
        video.setSrc(src);
    }

    @Override
    public void addSrc(String src, String type) {
        video.addSource(src, type);
    }

    @Override
    public void setWidth(int width) {
        video.setWidth(width + "px");

    }

    @Override
    public void setHeight(int height) {
        video.setHeight(height + "px");
    }

    @Override
    public void setPoster(String url) {
        // TODO na ktoryms ios-ie jest blad z posterem sprawdzic i zrobic
        // implementacje dla niego
        video.setPoster(url);
    }

    @Override
    public void setShowNativeControls(boolean show) {
        video.setControls(show);
    }

    @Override
    public MediaBase getMedia() {
        return video;
    }

    @Override
    public void setEventBusSourceObject(MediaWrapper<?> object) {
        this.eventBusSource = object;
        video.setEventBusSourceObject(object);
    }

    public MediaWrapper<?> getEventBusSourceObject() {
        return eventBusSource;
    }
}
