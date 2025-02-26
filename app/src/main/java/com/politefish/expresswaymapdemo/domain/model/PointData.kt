package com.politefish.expresswaymapdemo.domain.model

import com.politefish.expresswaymapdemo.domain.pointmapping.ExpPoint

data class PointData(
    val expPoint: ExpPoint.KeyPoint,
    val puckBearing: Float,
    val distanceDelta: Double,
    val maxDistanceDelta: Double
)