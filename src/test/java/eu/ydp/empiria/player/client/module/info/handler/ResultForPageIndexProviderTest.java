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

package eu.ydp.empiria.player.client.module.info.handler;


import eu.ydp.empiria.player.client.controller.session.datasockets.ItemSessionDataSocket;
import eu.ydp.empiria.player.client.controller.session.datasupplier.SessionDataSupplier;
import eu.ydp.empiria.player.client.controller.variables.ResultExtractorsFactory;
import eu.ydp.empiria.player.client.controller.variables.VariableProviderSocket;
import eu.ydp.empiria.player.client.controller.variables.VariableResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResultForPageIndexProviderTest {

    @InjectMocks
    private ResultForPageIndexProvider testObj;

    @Mock
    private ResultExtractorsFactory variableResultFactory;
    @Mock
    private SessionDataSupplier sessionDataSupplier;
    @Mock
    private ItemSessionDataSocket itemSessionDataSocket;
    @Mock
    private VariableProviderSocket variableProviderSocket;
    @Mock
    private VariableResult variableResult;

    private static final int EXPECTED_RESULT = 10;
    private int pageIndex = 1;

    @Before
    public void setUp() {
        when(sessionDataSupplier.getItemSessionDataSocket(pageIndex)).thenReturn(itemSessionDataSocket);
        when(itemSessionDataSocket.getVariableProviderSocket()).thenReturn(variableProviderSocket);
        when(variableResultFactory.createVariableResult(variableProviderSocket)).thenReturn(variableResult);
        when(variableResult.getResult()).thenReturn(EXPECTED_RESULT);
    }

    @Test
    public void shouldGetCorrectResultForIndex() {
        // given

        // when
        int actualResult = testObj.get(pageIndex);

        // then
        assertThat(actualResult).isEqualTo(EXPECTED_RESULT);
    }
}
