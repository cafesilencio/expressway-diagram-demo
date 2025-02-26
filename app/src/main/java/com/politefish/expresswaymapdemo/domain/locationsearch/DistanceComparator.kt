package com.politefish.expresswaymapdemo.domain.locationsearch

import com.mapbox.geojson.Point
import com.mapbox.turf.TurfConstants
import com.mapbox.turf.TurfMeasurement
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpPoint

class DistanceComparator(private val expPointOrigin: Point) : Comparator<ExpPoint> {

    override fun compare(p0: ExpPoint, p1: ExpPoint): Int {
        return TurfMeasurement.distance(expPointOrigin, p0.point, TurfConstants.UNIT_METERS)
            .compareTo(TurfMeasurement.distance(expPointOrigin, p1.point, TurfConstants.UNIT_METERS))
    }
}