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

package eu.ydp.empiria.player.client.module.selection.controller;

import eu.ydp.empiria.player.client.module.AbstractResponseModel;
import eu.ydp.empiria.player.client.module.core.answer.MarkAnswersType;
import eu.ydp.empiria.player.client.module.components.choicebutton.Identifiable;

import java.util.ArrayList;
import java.util.List;

public class IdentifiableAnswersByTypeFinder {

    public <R extends Identifiable> List<R> findAnswersObjectsOfGivenType(MarkAnswersType type, List<R> identifiableObjects,
                                                                          AbstractResponseModel<?> responseModel) {
        List<R> answerObjectsOfGivenType = new ArrayList<R>();
        for (R answerObject : identifiableObjects) {
            String answerId = answerObject.getId();

            if (isAnswerOfGivenType(answerId, type, responseModel)) {
                answerObjectsOfGivenType.add(answerObject);
            }
        }
        return answerObjectsOfGivenType;
    }

    private boolean isAnswerOfGivenType(String answerId, MarkAnswersType type, AbstractResponseModel<?> responseModel) {
        boolean correctAnswer = responseModel.isCorrectAnswer(answerId);

        if (MarkAnswersType.CORRECT.equals(type) && correctAnswer) {
            return true;
        } else if (MarkAnswersType.WRONG.equals(type) && !correctAnswer) {
            return true;
        }

        return false;
    }

}
