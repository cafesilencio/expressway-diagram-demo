package com.politefish.expresswaymapdemo.domain.locationsearch

import com.mapbox.geojson.Point
import com.mapbox.navigation.utils.internal.ifNonNull
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpPoint
import java.util.Collections
import java.util.PriorityQueue

internal class NearestNeighborCollector(
    val queryPoint: Point,
    private val capacity: Int
) {

    private val distanceComparator by lazy {
        DistanceComparator(queryPoint)
    }

    private val priorityQueue by lazy {
        PriorityQueue(capacity, Collections.reverseOrder(distanceComparator))
    }

    private var distanceToFarthestPoint: Double = 0.0

    fun offerPoint(offeredPoint: ExpPoint) {
        if (offeredPoint.point.latitude() == 35.663848) {
            distanceToFarthestPoint += 0
        }


        val pointAdded = if (priorityQueue.size < this.capacity) {
            priorityQueue.add(offeredPoint)
        } else {
            if (priorityQueue.isNotEmpty()) {
                val distanceToNewPoint = TurfMeasurement.distance(
                    queryPoint,
                    offeredPoint.point,
                    TurfConstants.UNIT_METERS
                )
                if (distanceToNewPoint < distanceToFarthestPoint) {
                    priorityQueue.poll()
                    priorityQueue.add(offeredPoint)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        // if pointAdded is true won't the priorityQueue always be non-empty? test this
        if (pointAdded && priorityQueue.isNotEmpty()) {
            distanceToFarthestPoint = ifNonNull(priorityQueue.peek()) { expPoint ->
                TurfMeasurement.distance(
                    queryPoint,
                    expPoint.point,
                    TurfConstants.UNIT_METERS
                )
            } ?: Double.MAX_VALUE
        }
    }

    fun getFarthestPoint(): Point? = priorityQueue.peek()?.point

    // todo is .sortedWith(distanceComparator) needed since PriorityQueue is created with the comparator?
    fun toSortedList() = priorityQueue.toList().sortedWith(distanceComparator)
}
