package com.politefish.expresswaymapdemo.domain.locationsearch

import com.mapbox.geojson.Point
import com.politefish.expresswaymapdemo.domain.pointmapping.ExpBearing

data class KeyPointData(
    val point: Point,
    val screenCoordinates: Pair<Float, Float>,
    val lineDirection: ExpBearing,
    val pointBearing: ExpBearing
)