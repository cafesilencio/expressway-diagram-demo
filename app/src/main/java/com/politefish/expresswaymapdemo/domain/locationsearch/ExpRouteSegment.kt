package com.politefish.expresswaymapdemo.domain.locationsearch

import com.mapbox.geojson.LineString

data class ExpRouteSegment(
    val quadkeys: List<String>,
    val geometry: String,
    val name: String,
    val gapDistance: Double,
    val keyPointMetaData: List<KeyPointMetaData>
) {

    private val points by lazy {
        LineString.fromPolyline(geometry, 6).coordinates()
    }

    // fixme why is this taking for granted there are only two items?
    // if each segment will only have two KeyPointData items they should be identified
    // as first/last rather than in a collection
    fun getKeyPoints(): List<KeyPointData> {
        return listOf(
            KeyPointData(
                points.first(),
                keyPointMetaData.first().screenCoordinates,
                keyPointMetaData.first().lineDirection,
                keyPointMetaData.first().pointBearing
            ),
            KeyPointData(
                points.last(),
                keyPointMetaData.last().screenCoordinates,
                keyPointMetaData.last().lineDirection,
                keyPointMetaData.last().pointBearing
            ),
        )
    }
}