package eu.ydp.empiria.player.client.gin;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import eu.ydp.empiria.player.client.controller.ContentPreloader;
import eu.ydp.empiria.player.client.controller.Page;
import eu.ydp.empiria.player.client.controller.delivery.DeliveryEngine;
import eu.ydp.empiria.player.client.controller.multiview.PanelCache;
import eu.ydp.empiria.player.client.gin.factory.PlayerFactory;
import eu.ydp.empiria.player.client.gin.factory.TextTrackFactory;
import eu.ydp.empiria.player.client.gin.module.*;
import eu.ydp.empiria.player.client.gin.module.tutor.TutorGinModule;
import eu.ydp.empiria.player.client.scripts.ScriptsLoader;
import eu.ydp.empiria.player.client.util.events.external.ExternalEventDispatcher;
import eu.ydp.empiria.player.client.util.events.internal.bus.EventsBus;
import eu.ydp.gwtutil.client.debug.log.Logger;
import eu.ydp.gwtutil.client.gin.module.UtilGinModule;

@GinModules(value = {PlayerGinModule.class, UtilGinModule.class, ChoiceGinModule.class, ConnectionGinModule.class, SourceListGinModule.class,
        SelectionGinModule.class, SimulationGinModule.class, SlideshowGinModule.class,
        OrderingGinModule.class, ColorfillGinModule.class, DragDropGinModule.class, TutorGinModule.class, ButtonGinModule.class,
        DrawingGinModule.class, BonusGinModule.class, ProgressBonusGinModule.class, VideoGinModule.class, DictionaryGinModule.class,
        TextEditorGinModule.class, TestGinModule.class, SpeechScoreGinModule.class,
        ExternalGinModule.class, PicturePlayerGinModule.class, MathJaxGinModule.class, AccordionGinModule.class,
        PageContentGinModule.class, MathGinModule.class, FeedbackGinModule.class, StyleGinModule.class,
        MediaGinModule.class,
        ReportGinModule.class})
public interface PlayerGinjector extends Ginjector {

    DeliveryEngine getDeliveryEngine();

    EventsBus getEventsBus();

    Page getPage();

    PanelCache getPanelCache();

    TextTrackFactory getTextTrackFactory();

    Logger getLogger();

    ScriptsLoader getScriptsLoader();

    ExternalEventDispatcher getEventDispatcher();

    PlayerFactory getPlayerFactory();

    ContentPreloader getContentPreloader();
}
