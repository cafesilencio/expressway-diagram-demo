package com.politefish.expresswaymapdemo.domain

import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.politefish.expresswaymapdemo.domain.locationsearch.LocationSearchTree
import com.politefish.expresswaymapdemo.domain.locationsearch.LocationSearchUtil
import com.politefish.expresswaymapdemo.domain.model.LocationAndBearing
import com.politefish.expresswaymapdemo.domain.model.PointData
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpPoint
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpresswayPointMap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GetLocationMatchUseCase(private val locationSearchTree: LocationSearchTree) {
    private val currentQuadrants = mutableSetOf<String>()
    private val mutex = Mutex()

    suspend operator fun invoke(locationData: LocationAndBearing): PointData {
        val segments = if (currentQuadrants.isEmpty()) {
            ExpresswayPointMap.getSegmentsRelatedToPoint(locationData.point)
        } else if (!pointInQuadrants(locationData.point, currentQuadrants)) {
            ExpresswayPointMap.getSegmentsRelatedToPoint(locationData.point)
        } else {
            listOf()
        }

        mutex.withLock {
            // Prune and populate the search tree if the incoming point
            // indicates a change in the current quadrants already loaded.
            if (segments.isNotEmpty()) {
                locationSearchTree.clearPoints()
                currentQuadrants.clear()
                segments.forEach { segment ->
                    val expPoints = LocationSearchUtil.expRouteSegmentToExpPoints(segment).also {
                        LocationSearchUtil.interpolateTweenPointBitmapCoordinates(it)
                    }
                    locationSearchTree.addPoints(expPoints)
                    currentQuadrants.addAll(segment.quadkeys)
                }
            }
        }

        val nearestPoint = locationSearchTree.getNearestNeighbor(locationData.point)
        val closestKeyPoint = ExpPoint.KeyPoint(
            nearestPoint.point,
            nearestPoint.getExpCoordinates(),
            nearestPoint.getPuckBearing(),
            nearestPoint.getMaxDistanceDelta()
        )
        return calculatePointData(closestKeyPoint, locationData.point, locationData.bearing)
    }

    private fun pointInQuadrants(point: Point, quadrants: Set<String>): Boolean {
        val quadkeyForPoint = QuadkeyUtil.getQuadkey(point, 16)
        return quadrants.contains(quadkeyForPoint)
    }

    private fun calculatePointData(
        expPoint: ExpPoint.KeyPoint,
        targetLocation: Point,
        bearing: Float
    ): PointData {
        val puckBearing = expPoint.getPuckBearing().translateBearing(bearing)
        val distanceDelta = TurfMeasurement.distance(
            targetLocation,
            expPoint.point,
            TurfConstants.UNIT_METERS
        )
        return PointData(expPoint, puckBearing, distanceDelta, expPoint.getMaxDistanceDelta())
    }
}
