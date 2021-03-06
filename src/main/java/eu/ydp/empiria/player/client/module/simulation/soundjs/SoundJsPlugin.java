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

package eu.ydp.empiria.player.client.module.simulation.soundjs;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.ydp.empiria.player.client.media.MediaWrapperCreator;
import eu.ydp.empiria.player.client.module.media.MediaWrapper;
import eu.ydp.empiria.player.client.module.media.MediaWrapperController;
import eu.ydp.empiria.player.client.module.media.MimeSourceProvider;
import eu.ydp.empiria.player.client.util.events.internal.callback.CallbackReceiver;
import eu.ydp.empiria.player.client.util.events.internal.media.MediaEventTypes;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class SoundJsPlugin implements SoundApiForJs {

    private final MediaWrapperCreator mediaWrapperCreator;
    private final MediaWrapperController mediaWrapperController;
    private final MimeSourceProvider mimeSourceProvider;
    private final SoundJsNative soundJsNative;
    private final SoundJsMediaEventHandler soundJsMediaEventHandler;

    private final Map<String, MediaWrapper<Widget>> wrappers = new HashMap<>();

    @Inject
    public SoundJsPlugin(MediaWrapperCreator mediaWrapperCreator, MediaWrapperController mediaWrapperController, MimeSourceProvider mimeSourceProvider,
                         SoundJsNative soundJsNative, SoundJsMediaEventHandler soundJsMediaEventHandler) {
        this.mediaWrapperCreator = mediaWrapperCreator;
        this.mediaWrapperController = mediaWrapperController;
        this.mimeSourceProvider = mimeSourceProvider;
        this.soundJsNative = soundJsNative;
        this.soundJsNative.setApiForJs(this);
        this.soundJsMediaEventHandler = soundJsMediaEventHandler;
        this.soundJsMediaEventHandler.setSoundJsNative(soundJsNative);
    }

    @Override
    public void preload(final String src) {
        if (!wrappers.containsKey(src)) {
            createMediaWrapper(src, addWrapper(src));
        }
    }

    @Override
    public void play(final String src) {
        MediaWrapper<Widget> wrapper = wrappers.get(src);
        if (wrapper == null) {
            createMediaWrapper(src, addWrapperAndPlay(src));
        } else {
            playMediaWrapper(wrapper);
        }
    }

    @Override
    public void playLooped(String src) {
        MediaWrapper<Widget> wrapper = wrappers.get(src);
        if (wrapper == null) {
            createMediaWrapper(src, addWrapperAndPlayLooped(src));
        } else {
            playLoopedMediaWrapper(wrapper);
        }
    }

    @Override
    public void stop(String src) {
        MediaWrapper<Widget> wrapper = wrappers.get(src);
        mediaWrapperController.stop(wrapper);
    }

    @Override
    public void pause(String src) {
        MediaWrapper<Widget> wrapper = wrappers.get(src);
        mediaWrapperController.pause(wrapper);
    }

    @Override
    public void resume(String src) {
        MediaWrapper<Widget> wrapper = wrappers.get(src);
        mediaWrapperController.resume(wrapper);
    }

    @Override
    public void setCurrentTime(String src, Double time) {
        MediaWrapper<Widget> wrapper = wrappers.get(src);
        mediaWrapperController.setCurrentTime(wrapper, time);
    }

    @Override
    public double getCurrentTime(String src) {
        MediaWrapper<Widget> wrapper = wrappers.get(src);
        return mediaWrapperController.getCurrentTime(wrapper);
    }

    private void createMediaWrapper(final String src, CallbackReceiver<MediaWrapper<Widget>> receiver) {
        Map<String, String> sourcesWithTypes = mimeSourceProvider.getSourcesWithTypeByExtension(src);
        mediaWrapperCreator.createSimulationMediaWrapper(src, sourcesWithTypes, receiver);
    }

    private void playMediaWrapper(MediaWrapper<Widget> wrapper) {
        mediaWrapperController.pauseAndPlay(wrapper);
    }

    private void playLoopedMediaWrapper(MediaWrapper<Widget> wrapper) {
        mediaWrapperController.pauseAndPlayLooped(wrapper);
    }

    private void addHandlers(final MediaWrapper<Widget> wrapper) {
        mediaWrapperController.addHandler(MediaEventTypes.ON_END, wrapper, soundJsMediaEventHandler);
    }

    private CallbackReceiver<MediaWrapper<Widget>> addWrapper(final String src) {
        return new SoundJsCallbackReceiver(src);
    }

    private CallbackReceiver<MediaWrapper<Widget>> addWrapperAndPlay(final String src) {
        return new SoundJsCallbackReceiver(src) {
            @Override
            public void setCallbackReturnObject(MediaWrapper<Widget> wrapper) {
                super.setCallbackReturnObject(wrapper);
                playMediaWrapper(wrapper);
            }

        };
    }

    private CallbackReceiver<MediaWrapper<Widget>> addWrapperAndPlayLooped(final String src) {
        return new SoundJsCallbackReceiver(src) {
            @Override
            public void setCallbackReturnObject(final MediaWrapper<Widget> wrapper) {
                super.setCallbackReturnObject(wrapper);
                playLoopedMediaWrapper(wrapper);
            }
        };
    }

    private class SoundJsCallbackReceiver implements CallbackReceiver<MediaWrapper<Widget>> {

        private final String src;

        public SoundJsCallbackReceiver(String src) {
            this.src = src;
        }

        @Override
        public void setCallbackReturnObject(MediaWrapper<Widget> wrapper) {
            addHandlers(wrapper);
            wrappers.put(src, wrapper);
        }
    }
}
