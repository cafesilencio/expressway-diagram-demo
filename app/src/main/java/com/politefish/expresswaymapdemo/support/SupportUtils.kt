package com.politefish.expresswaymapdemo.support

import android.content.Context
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.common.location.Location
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.RouterOrigin
import com.politefish.expresswaymapdemo.R

object SupportUtils {

    fun getTestRoute(context: Context): NavigationRoute {
        val routeJson = context.resources.openRawResource(R.raw.test_route).bufferedReader().use { it.readText() }
        val directionsRoute = DirectionsRoute.fromJson(routeJson)

        val response = DirectionsResponse.builder()
            .routes(listOf(directionsRoute))
            .code("Ok")
            .waypoints(listOf())
            .uuid(directionsRoute.requestUuid())
            .build()

        return NavigationRoute.create(
            response.toJson(),
            directionsRoute.routeOptions()!!.toUrl("***").toString(),
            RouterOrigin.Offboard,
        ).first()
    }

    fun Location.toAndroidLocation(): android.location.Location {
        return android.location.Location("").also {
            it.latitude = this.latitude
            it.longitude = this.longitude
        }
    }
}