package com.politefish.expresswaymapdemo.domain

import com.mapbox.geojson.Point
import com.politefish.expresswaymapdemo.domain.locationsearch.LocationSearchTree
import com.politefish.expresswaymapdemo.domain.model.LocationAndBearing
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetLocationMatchUseCaseTest {

    @Test
    fun getLocationMatchTest(): Unit = runBlocking {
        val result = GetLocationMatchUseCase(LocationSearchTree()).invoke(
            LocationAndBearing(
                Point.fromLngLat(139.7926192507698, 35.522158181851935),
                0f
            )
        )

        assertEquals(
            Point.fromLngLat(139.7926071907744, 35.52217169138362),
            result.expPoint.point
        )
    }
}