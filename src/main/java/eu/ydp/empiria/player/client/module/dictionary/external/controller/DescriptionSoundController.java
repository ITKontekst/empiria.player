package eu.ydp.empiria.player.client.module.dictionary.external.controller;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import eu.ydp.empiria.player.client.module.media.MediaWrapper;
import eu.ydp.empiria.player.client.module.media.MediaWrapperController;
import eu.ydp.empiria.player.client.util.events.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.callback.CallbackReceiver;
import eu.ydp.empiria.player.client.util.events.media.AbstractMediaEventHandler;
import eu.ydp.empiria.player.client.util.events.media.MediaEvent;
import eu.ydp.empiria.player.client.util.events.media.MediaEventHandler;
import eu.ydp.empiria.player.client.util.events.media.MediaEventTypes;
import eu.ydp.empiria.player.client.util.events.scope.CurrentPageScope;

import static eu.ydp.empiria.player.client.util.events.media.MediaEventTypes.*;

public class DescriptionSoundController {

    private boolean playing;
    private final MediaWrapperController mediaWrapperController;
    private MediaWrapper<Widget> mediaWrapper;
    private final DictionaryMediaWrapperCreator mediaWrapperCreator;
    private final EventsBus eventsBus;
    private final Provider<CurrentPageScope> currentPageScopeProvider;

    @Inject
    public DescriptionSoundController(MediaWrapperController mediaWrapperController,
                                      DictionaryMediaWrapperCreator mediaWrapperCreator,
                                      EventsBus eventsBus,
                                      Provider<CurrentPageScope> currentPageScopeProvider){
        this.mediaWrapperController = mediaWrapperController;
        this.mediaWrapperCreator = mediaWrapperCreator;
        this.eventsBus = eventsBus;
        this.currentPageScopeProvider = currentPageScopeProvider;
    }

    public void playDescriptionSound(String fileName, CallbackReceiver<MediaWrapper<Widget>> callbackReceiver) {
            playSoundIfFileNameNotEmpty(fileName, callbackReceiver);
    }

    private void playSoundIfFileNameNotEmpty(String fileName, CallbackReceiver<MediaWrapper<Widget>> callbackReceiver) {
        if (!Strings.isNullOrEmpty(fileName)) {
            createMediaWrapper(fileName, callbackReceiver);
        }
    }

    public boolean isPlaying(){
        return playing;
    }

    private void createMediaWrapper(String filePath, CallbackReceiver<MediaWrapper<Widget>> callbackReceiver) {
        mediaWrapperCreator.create(filePath, callbackReceiver);
    }

    public void playFromMediaWrapper(AbstractMediaEventHandler mediaEventHandler, MediaWrapper<Widget> mediaWrapper){
        this.mediaWrapper = mediaWrapper;
        addMediaHandlers(mediaEventHandler);
        playing = true;
        mediaWrapperController.stopAndPlay(mediaWrapper);
    }

    private void addMediaHandlers(AbstractMediaEventHandler handler) {
        MediaEventTypes[] eventTypes = {ON_PAUSE,ON_END,ON_STOP,ON_PLAY};
        addMediaHandlers(eventTypes, handler);
    }

    private void addMediaHandlers(MediaEventTypes[] types, MediaEventHandler handler) {
        for(MediaEventTypes eventType:types){
            eventsBus.addHandlerToSource(MediaEvent.getType(eventType), mediaWrapper, handler, currentPageScopeProvider.get());
        }
    }

    public boolean isMediaEventNotOnPlay(MediaEvent event){
        return !MediaEventTypes.ON_PLAY.equals(event.getType());
    }

    public void stopPlaying(){
        playing = false;
    }

    public void stopMediaWrapper(){
        mediaWrapperController.stop(mediaWrapper);
    }

}
