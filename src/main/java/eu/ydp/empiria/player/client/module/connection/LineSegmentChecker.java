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

package eu.ydp.empiria.player.client.module.connection;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.ydp.empiria.player.client.util.position.Point;

@Singleton
public class LineSegmentChecker {

    private final static double ACCEPTABLE_ERROR_LEVEL = 15.0;

    private final DistanceCalculator distanceCalculator;
    private final RectangleChecker rectangleChecker;

    @Inject
    public LineSegmentChecker(DistanceCalculator distanceCalculator, RectangleChecker rectangleChecker) {
        this.distanceCalculator = distanceCalculator;
        this.rectangleChecker = rectangleChecker;
    }

    public boolean isLineSegmentNearPoint(LineSegment lineSegment, Point point) {
        if (!rectangleChecker.isPointInLineSegmentRectangle(lineSegment, point)) {
            return false;
        }

        double distance = distanceCalculator.calculateDistanceBetween(lineSegment, point);

        return distance < ACCEPTABLE_ERROR_LEVEL;
    }
}
