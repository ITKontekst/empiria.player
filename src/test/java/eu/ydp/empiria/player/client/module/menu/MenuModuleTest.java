package eu.ydp.empiria.player.client.module.menu;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.xml.client.Element;
import com.google.gwtmockito.GwtMockitoTestRunner;
import eu.ydp.empiria.player.client.controller.body.BodyGeneratorSocket;
import eu.ydp.empiria.player.client.controller.flow.FlowDataSupplier;
import eu.ydp.empiria.player.client.controller.report.table.ReportTableGenerator;
import eu.ydp.empiria.player.client.gin.factory.ReportModuleFactory;
import eu.ydp.empiria.player.client.module.ModuleSocket;
import eu.ydp.empiria.player.client.util.events.internal.bus.EventsBus;
import eu.ydp.empiria.player.client.util.events.internal.player.PlayerEvent;
import eu.ydp.empiria.player.client.util.events.internal.player.PlayerEventTypes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MenuModuleTest {

    @InjectMocks
    private MenuModule testObj;
    @Mock
    private MenuPresenter presenter;
    @Mock
    private ReportModuleFactory reportModuleFactory;
    @Mock
    private Element element;
    @Mock
    private ModuleSocket moduleSocket;
    @Mock
    private BodyGeneratorSocket bodyGeneratorSocket;
    @Mock
    private EventsBus eventsBus;
    @Mock
    private FlowDataSupplier flowDataSupplier;
    @Mock
    private ReportTableGenerator reportTableGenerator;

    private Map<Integer, Integer> pageToRow;
    int currentPageIndex = 1;

    @Before
    public void init() {
        pageToRow = new HashMap<>();
        when(reportModuleFactory.createReportTableGenerator(bodyGeneratorSocket)).thenReturn(reportTableGenerator);
        when(reportTableGenerator.getPageToRowMap()).thenReturn(pageToRow);
        when(flowDataSupplier.getCurrentPageIndex()).thenReturn(currentPageIndex);
    }

    @Test
    public void shouldCreateTable_onInit() {
        // given
        FlexTable flexTable = mock(FlexTable.class);
        when(reportTableGenerator.generate(element)).thenReturn(flexTable);

        // when
        testObj.initModule(element, moduleSocket, bodyGeneratorSocket);

        // then
        verify(presenter).setTable(flexTable);
    }

    @Test
    public void shouldHideMenu_onPlayerEvent() {
        // given
        int row = 2;
        pageToRow.put(currentPageIndex, row);

        PlayerEvent playerEvent = mock(PlayerEvent.class);
        when(playerEvent.getType()).thenReturn(PlayerEventTypes.BEFORE_FLOW);

        testObj.initModule(element, moduleSocket, bodyGeneratorSocket);

        // when
        testObj.onPlayerEvent(playerEvent);

        // then
        verify(presenter).hide();
        verify(presenter).unmarkRow(row);
    }

    @Test
    public void shouldMarkRow_whenPageLoaded() {
        // given
        int row = 2;
        pageToRow.put(currentPageIndex, row);

        PlayerEvent playerEvent = mock(PlayerEvent.class);
        when(playerEvent.getType()).thenReturn(PlayerEventTypes.PAGE_LOADED);

        testObj.initModule(element, moduleSocket, bodyGeneratorSocket);

        // when
        testObj.onPlayerEvent(playerEvent);

        // then
        verify(presenter).markRow(row);
    }

    @Test
    public void shouldMarkNotExistingRow_whenPageIsNotMapped(){
        // given
        pageToRow.remove(currentPageIndex);

        PlayerEvent playerEvent = mock(PlayerEvent.class);
        when(playerEvent.getType()).thenReturn(PlayerEventTypes.PAGE_LOADED);

        testObj.initModule(element, moduleSocket, bodyGeneratorSocket);

        // when
        testObj.onPlayerEvent(playerEvent);

        // then
        verify(presenter).markRow(-1);
    }
}