package com.politefish.expresswaymapdemo.domain.locationsearch

import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpBearing
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpPoint
import kotlin.math.abs

internal object LocationSearchUtil {

    fun interpolateTweenPointBitmapCoordinates(pointToPixelList: List<ExpPoint>) {
        val position = 0
        var nextKeyPoint = getNextKeyPoint(position, pointToPixelList)
        var followingKeyPoint = getNextKeyPoint(position + 1, pointToPixelList)

        while (nextKeyPoint != null && followingKeyPoint != null) {
            val fillerRange = (followingKeyPoint.first + 1) - nextKeyPoint.first
            val xDelta = (
                    followingKeyPoint.second.getExpCoordinates().first - nextKeyPoint.second.getExpCoordinates().first
                    ) / fillerRange
            val yDelta = (
                    followingKeyPoint.second.getExpCoordinates().second - nextKeyPoint.second.getExpCoordinates().second
                    ) / fillerRange
            var xPoint = nextKeyPoint.second.getExpCoordinates().first + xDelta
            var yPoint = nextKeyPoint.second.getExpCoordinates().second + yDelta
            for (index in nextKeyPoint.first + 1 until followingKeyPoint.first) {
                val item = pointToPixelList[index]
                if (item is ExpPoint.TweenPoint) {
                    item.expCoords = Pair(xPoint, yPoint)
                }
                xPoint += xDelta
                yPoint += yDelta
            }

            nextKeyPoint = followingKeyPoint
            followingKeyPoint =
                getNextKeyPoint(nextKeyPoint.first + 1, pointToPixelList)
        }
    }

    fun expRouteSegmentToExpPoints(segment: ExpRouteSegment): List<ExpPoint> {
        val segmentPoints = LineString.fromPolyline(segment.geometry, 6).coordinates()
        val granularPoints = getFillerPointsForStepPoints(segmentPoints, segment.gapDistance)
        return generateExpPoints(granularPoints, segment.getKeyPoints(), segment.gapDistance)
    }

    private fun getFillerPointsForStepPoints(
        steps: List<Point>,
        gapDistanceInMeters: Double
    ): List<Pair<Point, Double>> {
        val fillerPoints = mutableListOf<Pair<Point, Double>>()
        steps.forEachIndexed { index, point ->
            if (index < steps.lastIndex) {
                getFillerPoints(point, steps[index + 1], gapDistanceInMeters).apply {
                    fillerPoints.addAll(this)
                }
            }
        }

        if (fillerPoints.isNotEmpty()) {
            val bearing = TurfMeasurement.bearing(fillerPoints.last().first, steps.last())
            fillerPoints.add(Pair(steps.last(), bearing))
        }
        return fillerPoints
    }

    private fun getFillerPoints(
        startPoint: Point,
        endPoint: Point,
        gapDistanceInMeters: Double
    ): List<Pair<Point, Double>> {
        val turfDistance = TurfMeasurement.distance(
            startPoint,
            endPoint,
            TurfConstants.UNIT_METERS
        )
        val fillerPoints = mutableListOf<Pair<Point, Double>>()
        val bearing = TurfMeasurement.bearing(startPoint, endPoint)
        val numPointsToCreate = (turfDistance / gapDistanceInMeters).toInt()
        var lastCalculatedPoint = startPoint

        fillerPoints.add(Pair(startPoint, abs(bearing)))
        repeat(numPointsToCreate) {
            val fillerPoint = TurfMeasurement.destination(
                lastCalculatedPoint,
                gapDistanceInMeters,
                bearing,
                TurfConstants.UNIT_METERS
            )

            fillerPoints.add(Pair(fillerPoint, abs(bearing)))
            lastCalculatedPoint = fillerPoints.last().first
        }
        return fillerPoints
    }

    private fun generateExpPoints(
        fillerPoints: List<Pair<Point, Double>>,
        keyPoints: List<KeyPointData>,
        gapDistanceInMeters: Double
    ): List<ExpPoint> {
        val segmentBearing = abs(
            TurfMeasurement.bearing(fillerPoints.first().first, fillerPoints.last().first).toInt()
        )
        var lineDirection: ExpBearing = ExpBearing.NoOp

        var position = 0
        return fillerPoints.map { granularPoint ->
            val keyPoint = keyPoints.firstOrNull { it.point == granularPoint.first }
            if (keyPoint != null) {
                lineDirection = keyPoint.lineDirection
                ExpPoint.KeyPoint(
                    keyPoint.point,
                    keyPoint.screenCoordinates,
                    lineDirection,
                    maxDistance = gapDistanceInMeters
                )
            } else {
                // given the line direction and the actual bearing figure out the correct ExpBearing
                val puckBearing = getExpBearingInterpreter(
                    lineDirection,
                    segmentBearing,
                    granularPoint.second.toInt()
                )
                ExpPoint.TweenPoint(
                    granularPoint.first,
                    puckBearing = puckBearing,
                    maxDistance = gapDistanceInMeters
                )
            }.also {
                position++
            }
        }
    }

    private fun getExpBearingInterpreter(
        lineDirection: ExpBearing,
        segmentBearing: Int,
        bearing: Int
    ): ExpBearing {
        return when (lineDirection) {
            is ExpBearing.UpRightDownLeft -> {
                if (lineDirection.isInUpRange(segmentBearing)) {
                    if (lineDirection.isInUpRange(bearing)) {
                        lineDirection
                    } else {
                        ExpBearing.UpRightDownLeftRotateCW45
                    }
                } else {
                    if (lineDirection.isInDownRange(bearing)) {
                        lineDirection
                    } else {
                        ExpBearing.UpRightDownLeftRotateCW45
                    }
                }
            }
            is ExpBearing.UpLeftDownRight -> {
                if (lineDirection.isInUpRange(segmentBearing)) {
                    if (lineDirection.isInUpRange(bearing)) {
                        lineDirection
                    } else {
                        ExpBearing.UpLeftDownRightRotateCCW45
                    }
                } else {
                    if (lineDirection.isInDownRange(bearing)) {
                        lineDirection
                    } else {
                        ExpBearing.UpLeftDownRightRotateCCW45
                    }
                }
            }
            is ExpBearing.Horizontal -> {
                if (lineDirection.isInRightRange(segmentBearing)) {
                    if (lineDirection.isInRightRange(bearing)) {
                        lineDirection
                    } else {
                        ExpBearing.HorizontalRotateCCW45
                    }
                } else {
                    if (lineDirection.isInLeftRange(bearing)) {
                        lineDirection
                    } else {
                        ExpBearing.HorizontalRotateCCW45
                    }
                }
            }
            is ExpBearing.Vertical -> {
                if (lineDirection.isInUpRange(segmentBearing)) {
                    if (lineDirection.isInUpRange(bearing)) {
                        lineDirection
                    } else if (ExpBearing.VerticalRotateCCW45.isInUpRange(bearing)) {
                        ExpBearing.VerticalRotateCCW45
                    } else {
                        ExpBearing.VerticalRotateCW45
                    }
                } else {
                    if (lineDirection.isInDownRange(bearing)) {
                        lineDirection
                    } else if (ExpBearing.VerticalRotateCCW45.isInDownRange(bearing)) {
                        ExpBearing.VerticalRotateCCW45
                    } else {
                        ExpBearing.VerticalRotateCW45
                    }
                }
            }
            else -> lineDirection
        }
    }

    private fun getNextKeyPoint(
        offset: Int,
        pointToPixelList: List<ExpPoint>
    ): Pair<Int, ExpPoint>? {
        val nextKeyPointIndex =
            pointToPixelList.drop(offset).indexOfFirst { it is ExpPoint.KeyPoint }
        return if (nextKeyPointIndex >= 0) {
            Pair(nextKeyPointIndex + offset, pointToPixelList[nextKeyPointIndex + offset])
        } else {
            null
        }
    }
}
