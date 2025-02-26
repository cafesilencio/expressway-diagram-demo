package com.politefish.expresswaymapdemo.domain.locationsearch

import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpPoint

class LocationSearchTree(
    private val capacity: Int = 10
) {
    private var rootNode: LocationTreeNode? = null

    fun size() = rootNode?.size() ?: 0

    fun addPoints(points: List<ExpPoint>) {
        if (rootNode == null) {
            rootNode = LocationTreeNode(capacity, distanceCalcFunction)
        }
        points.shuffled().forEach {
            rootNode?.addPoint(it)
        }
    }

    fun clearPoints() {
        rootNode = null
    }

    // todo add remove method based on lat/lon rather than the object?
    fun remove(expPoint: ExpPoint) {
        rootNode?.remove(expPoint)
        rootNode?.prune()
    }

    fun getNearestNeighbor(target: Point) = getNearestNeighbors(target)

    private fun getNearestNeighbors(
        target: Point,
        maxResults: Int = 1
    ): ExpPoint {
        return rootNode?.let {
            val collector = NearestNeighborCollector(target, maxResults)
            it.collectNearestNeighbors(collector)
            collector.toSortedList().first()
        } ?: throw RuntimeException("Can not find nearest neighbor until location tree is initialized.")
    }

    private val distanceCalcFunction = { point1: Point, point2: Point ->
        TurfMeasurement.distance(point1, point2, TurfConstants.UNIT_METERS)
    }
}
