package com.politefish.expresswaymapdemo.domain.model

import com.mapbox.geojson.Point

data class LocationAndBearing(val point: Point, val bearing: Float)