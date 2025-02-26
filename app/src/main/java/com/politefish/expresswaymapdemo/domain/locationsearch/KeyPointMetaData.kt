package com.politefish.expresswaymapdemo.domain.locationsearch

import com.politefish.expresswaymapdemo.domain.pointmapping.ExpBearing

data class KeyPointMetaData(
    val geometryIndex: Int,
    val screenCoordinates: Pair<Float, Float>,
    val lineDirection: ExpBearing,
    val pointBearing: ExpBearing
)