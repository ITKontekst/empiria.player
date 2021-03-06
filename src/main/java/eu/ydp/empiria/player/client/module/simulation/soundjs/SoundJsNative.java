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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.inject.Singleton;
import eu.ydp.empiria.player.client.util.js.JSArrayUtils;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class SoundJsNative {

    private SoundApiForJs api;
    private final Map<String, JavaScriptObject> soundInstances = new HashMap<>();

    public SoundJsNative() {
        nativesInit();
    }

    public void setApiForJs(SoundApiForJs apiForJs) {
        this.api = apiForJs;
    }

    private native void nativesInit()/*-{
        var instance = this;
        $wnd.empiriaSoundJsPlay = function (src) {
            instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::play(Ljava/lang/String;)(src);
        }
        $wnd.empiriaSoundJsPlayLooped = function (src) {
            instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::playLooped(Ljava/lang/String;)(src);
        }
        $wnd.empiriaSoundJsInit = function (soundInstance, src) {
            instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::preload(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;)(soundInstance, src);
        }
        $wnd.empiriaSoundJsStop = function (src) {
            instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::stop(Ljava/lang/String;)(src);
        }
        $wnd.empiriaSoundJsPause = function (src) {
            instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::pause(Ljava/lang/String;)(src);
        }
        $wnd.empiriaSoundJsResume = function (src) {
            instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::resume(Ljava/lang/String;)(src);
        }
        $wnd.empiriaSoundJsSetCurrentTime = function (src, time) {
            instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::setCurrentTime(Ljava/lang/String;D)(src, time);
        }
        $wnd.empiriaSoundJsGetCurrentTime = function (src) {
            return instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::getCurrentTime(Ljava/lang/String;)(src);
        }
        $wnd.empiriaSoundJsGetSoundInstance = function (src) {
            return instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::getSoundInstance(Ljava/lang/String;)(src);
        }
        $wnd.empiriaSoundJsGetSoundInstances = function () {
            return instance.@eu.ydp.empiria.player.client.module.simulation.soundjs.SoundJsNative::getSoundInstances()();
        }
    }-*/;

    private native void onComplete(JavaScriptObject soundInstance)/*-{
        if (typeof soundInstance.onComplete == 'function') {
            soundInstance.onComplete(soundInstance);
        }
    }-*/;

    public void onComplete(String str) {
        onComplete(soundInstances.get(str));
    }

    private void play(String src) {
        api.play(src);
    }

    private void playLooped(String src) {
        api.playLooped(src);
    }

    private void preload(JavaScriptObject soundInstance, String src) {
        soundInstances.put(src, soundInstance);
        api.preload(src);
    }

    private void stop(String src) {
        api.stop(src);
    }

    private void pause(String src) {
        api.pause(src);
    }

    private void resume(String src) {
        api.resume(src);
    }

    private void setCurrentTime(String src, double time) {
        api.setCurrentTime(src, time);
    }

    private double getCurrentTime(String src) {
        return api.getCurrentTime(src);
    }

    private JavaScriptObject getSoundInstance(String src) {
        return soundInstances.get(src);
    }

    private JsArray<JavaScriptObject> getSoundInstances() {
        return JSArrayUtils.convert(soundInstances.values());
    }
}
