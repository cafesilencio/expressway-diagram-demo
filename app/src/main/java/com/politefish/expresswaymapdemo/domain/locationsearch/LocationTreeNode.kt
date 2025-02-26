package com.politefish.expresswaymapdemo.domain.locationsearch

import com.mapbox.geojson.Point
import com.mapbox.navigation.utils.internal.ifNonNull
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpPoint
import java.util.concurrent.CopyOnWriteArrayList

internal class LocationTreeNode(
    private val capacity: Int = 32,
    private val distanceCalcFunction: (Point, Point) -> Double
) {
    private var threshold = 0.0
    private var closer: LocationTreeNode? = null
    private var farther: LocationTreeNode? = null
    private var vantagePoint: Point? = null
    private val points = CopyOnWriteArrayList<ExpPoint>()

    fun addPoint(point: ExpPoint) {
        if (points.isEmpty()) {
            vantagePoint = point.point
            points.add(point)
        } else {
            ifNonNull(vantagePoint) { vp ->
                val vantagePointToPointDistance = distanceCalcFunction(vp, point.point)
                if (threshold == 0.0) {
                    threshold = vantagePointToPointDistance
                }

                if (vantagePointToPointDistance <= threshold) {
                    if (points.size < capacity) {
                        points.add(point)
                    } else{
                        if (closer == null) {
                            closer = LocationTreeNode(capacity, distanceCalcFunction)
                        }
                        closer?.addPoint(point)
                    }
                } else {
                    if (farther == null) {
                        farther = LocationTreeNode(capacity, distanceCalcFunction)
                    }
                    farther?.addPoint(point)
                }
            }
        }
    }

    fun size(): Int {
        return points.size + (closer?.size() ?: 0) + (farther?.size() ?: 0)
    }

    fun remove(expPoint: ExpPoint): Boolean {
        if (vantagePoint != null) {
            val distance = distanceCalcFunction(expPoint.point, vantagePoint!!)
            if (distance <= threshold) {
                if (points.contains(expPoint)) {
                    points.remove(expPoint)
                    return true
                } else {
                    closer?.remove(expPoint)
                }
            } else {
                farther?.remove(expPoint)
            }
        }
        return false
    }

    fun collectNearestNeighbors(collector: NearestNeighborCollector) {
        points.forEach {
            collector.offerPoint(it)
        }

        val firstNodeSearched = getChildNodeForPoint(collector.queryPoint)?.also {
            it.collectNearestNeighbors(collector)
        }

        val distanceFromVantagePointToQueryPoint = ifNonNull(vantagePoint) { vp ->
            distanceCalcFunction(vp, collector.queryPoint)
        } ?: 0.0
        val distanceFromQueryPointToFarthestPoint = if (collector.getFarthestPoint() != null) {
            distanceCalcFunction(collector.queryPoint, collector.getFarthestPoint()!!)
        } else {
            Double.MAX_VALUE
        }

        if (firstNodeSearched == closer) {
            val distanceFromQueryPointToThreshold = threshold - distanceFromVantagePointToQueryPoint

            if (distanceFromQueryPointToFarthestPoint > distanceFromQueryPointToThreshold) {
                farther?.collectNearestNeighbors(collector)
            }
        } else {
            val distanceFromQueryPointToThreshold = distanceFromVantagePointToQueryPoint - threshold

            if (distanceFromQueryPointToThreshold <= distanceFromQueryPointToFarthestPoint) {
                closer?.collectNearestNeighbors(collector)
            }
        }
    }

    private fun getChildNodeForPoint(point: Point): LocationTreeNode? {
        return ifNonNull(vantagePoint) { vp ->
            if (distanceCalcFunction(vp, point) <= threshold) {
                closer
            } else {
                farther
            }
        }
    }

    internal fun prune() {
        closer?.prune()
        farther?.prune()
        if (closer?.size() == 0) {
            closer = null
        }
        if (farther?.size() == 0) {
            farther = null
        }
    }
}