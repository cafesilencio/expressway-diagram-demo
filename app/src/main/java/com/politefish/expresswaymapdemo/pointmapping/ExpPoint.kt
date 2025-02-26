package com.politefish.expresswaymapdemo.pointmapping

import com.mapbox.geojson.Point
import com.politefish.expresswaymapdemo.pointmapping.ExpConstants.DEFAULT_CHM_POINT_BEARING
import com.politefish.expresswaymapdemo.pointmapping.ExpConstants.DEFAULT_CHM_POINT_DISTANCE_METERS

sealed class ExpPoint(val point: Point) {
    abstract fun getChmCoordinates(): Pair<Float, Float>
    abstract fun getPuckBearing(): ExpBearing
    abstract fun getMaxDistanceDelta(): Double

    override fun toString(): String {
        return "point=$point; coords=${getChmCoordinates()}; bearing=${getPuckBearing()}; maxDistanceDelta=${getMaxDistanceDelta()}"
    }

    class KeyPoint(
        point: Point,
        private val chmCoords: Pair<Float, Float>,
        private val puckBearing: ExpBearing = DEFAULT_CHM_POINT_BEARING,
        private val maxDistance: Double = DEFAULT_CHM_POINT_DISTANCE_METERS
    ) : ExpPoint(point) {
        override fun getChmCoordinates(): Pair<Float, Float> = chmCoords
        override fun getPuckBearing(): ExpBearing = puckBearing
        override fun getMaxDistanceDelta(): Double = maxDistance

        override fun toString(): String {
            return "KeyPoint(${super.toString()})"
        }
    }

    class TweenPoint(
        point: Point,
        private val puckBearing: ExpBearing = DEFAULT_CHM_POINT_BEARING,
        private val maxDistance: Double = DEFAULT_CHM_POINT_DISTANCE_METERS
    ) : ExpPoint(point) {

        var chmCoords: Pair<Float, Float>? = null

        override fun getChmCoordinates(): Pair<Float, Float> = chmCoords
            ?: throw RuntimeException(
                "Obtaining CHM coordinates for tween point is not allowed" +
                        " until location tree is parsed!"
            )
        override fun getPuckBearing(): ExpBearing = puckBearing
        override fun getMaxDistanceDelta(): Double = maxDistance

        override fun toString(): String {
            return "TweenPoint(${super.toString()})"
        }
    }
}
