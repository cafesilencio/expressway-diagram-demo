package com.politefish.expresswaymapdemo.domain.pointmapping

import com.mapbox.geojson.Point
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpConstants.DEFAULT_EXP_POINT_BEARING
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpConstants.DEFAULT_EXP_POINT_DISTANCE_METERS

sealed class ExpPoint(val point: Point) {
    abstract fun getExpCoordinates(): Pair<Float, Float>
    abstract fun getPuckBearing(): ExpBearing
    abstract fun getMaxDistanceDelta(): Double // todo is this still needed?

    override fun toString(): String {
        return "point=$point; coords=${getExpCoordinates()}; bearing=${getPuckBearing()}; maxDistanceDelta=${getMaxDistanceDelta()}"
    }

    class KeyPoint(
        point: Point,
        private val expCoords: Pair<Float, Float>,
        private val puckBearing: ExpBearing = DEFAULT_EXP_POINT_BEARING,
        private val maxDistance: Double = DEFAULT_EXP_POINT_DISTANCE_METERS
    ) : ExpPoint(point) {
        override fun getExpCoordinates(): Pair<Float, Float> = expCoords
        override fun getPuckBearing(): ExpBearing = puckBearing
        override fun getMaxDistanceDelta(): Double = maxDistance

        override fun toString(): String {
            return "KeyPoint(${super.toString()})"
        }
    }

    class TweenPoint(
        point: Point,
        private val puckBearing: ExpBearing = DEFAULT_EXP_POINT_BEARING,
        private val maxDistance: Double = DEFAULT_EXP_POINT_DISTANCE_METERS
    ) : ExpPoint(point) {

        var expCoords: Pair<Float, Float>? = null

        override fun getExpCoordinates(): Pair<Float, Float> = expCoords
            ?: throw RuntimeException(
                "Obtaining coordinates for tween point $point is not allowed" +
                        " until location tree is parsed."
            )
        override fun getPuckBearing(): ExpBearing = puckBearing
        override fun getMaxDistanceDelta(): Double = maxDistance

        override fun toString(): String {
            return "TweenPoint(${super.toString()})"
        }
    }
}
