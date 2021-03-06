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

package eu.ydp.empiria.player.client.module.expression;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import eu.ydp.empiria.player.client.controller.variables.objects.response.Response;
import eu.ydp.empiria.player.client.controller.variables.objects.response.ResponseBuilder;
import eu.ydp.empiria.player.client.module.expression.evaluate.ResponseValuesFetcherFunctions;
import eu.ydp.empiria.player.client.module.expression.model.ExpressionBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ExpressionSetsFinderJUnitTest {

    private ExpressionSetsFinder expressionSetsFinder;

    @Before
    public void setUp() throws Exception {
        expressionSetsFinder = new ExpressionSetsFinder(new ResponseFinder(new ExpressionToPartsDivider(), new IdentifiersFromExpressionExtractor()),
                new ResponseValuesFetcherFunctions());
    }

    @Test
    public void shouldUpdateExpressionSetsInBean() throws Exception {
        // given
        ExpressionBean expression = new ExpressionBean();

        expression.setTemplate("'a'+'b'='c'");
        Response responseA = new ResponseBuilder().withIdentifier("a").withCorrectAnswers("1").build();
        expression.getResponses().add(responseA);
        Response responseB = new ResponseBuilder().withIdentifier("b").withCorrectAnswers("2").build();
        expression.getResponses().add(responseB);
        Response responseC = new ResponseBuilder().withIdentifier("c").withCorrectAnswers("3").build();
        expression.getResponses().add(responseC);

        // when
        expressionSetsFinder.updateResponsesSetsInExpression(expression);

        Multiset<Multiset<String>> corectResponsesSets = expression.getCorectResponses();
        assertEquals(3, corectResponsesSets.size());
        assertTrue(corectResponsesSets.contains(HashMultiset.create(Lists.newArrayList("3"))));
        assertTrue(corectResponsesSets.contains(HashMultiset.create(Lists.newArrayList("1", "2"))));
        assertTrue(corectResponsesSets.contains(HashMultiset.create(Lists.newArrayList("1", "2", "3"))));
    }
}
