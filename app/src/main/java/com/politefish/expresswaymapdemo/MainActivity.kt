package com.politefish.expresswaymapdemo

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.match
import com.mapbox.maps.extension.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.history.ReplayEventBase
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.replay.route.ReplayRouteMapper
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.NavigationRouteLine
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.politefish.expresswaymapdemo.databinding.ActivityMainBinding
import com.politefish.expresswaymapdemo.pointmapping.ExpPoint
import com.politefish.expresswaymapdemo.pointmapping.ExpresswayPointMap.getPoints
import com.politefish.expresswaymapdemo.support.SupportUtils.getTestRoute
import com.politefish.expresswaymapdemo.support.SupportUtils.toAndroidLocation


class MainActivity : AppCompatActivity() {

    private lateinit var puckBitmap: Bitmap
    private lateinit var mapboxReplayer: MapboxReplayer
    private lateinit var replayProgressObserver: ReplayProgressObserver
    private val replayRouteMapper by lazy { ReplayRouteMapper() }
    private lateinit var mapboxNavigation: MapboxNavigation
    private val requiredPermissions = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)

    private val mapboxMap: MapboxMap by lazy {
        viewBinding.mapView.getMapboxMap()
    }

    private val locationComponent by lazy {
        viewBinding.mapView.location.apply {
            setLocationProvider(navigationLocationProvider)
            enabled = true
        }
    }

    private val navigationRoute: NavigationRoute by lazy {
        getTestRoute(this)
    }

    private val navigationLocationProvider by lazy {
        NavigationLocationProvider()
    }

    private val routeLineColorResources by lazy {
        RouteLineColorResources.Builder().build()
    }

    private val routeLineResources: RouteLineResources by lazy {
        RouteLineResources.Builder()
            .routeLineColorResources(routeLineColorResources)
            .build()
    }

    private val options: MapboxRouteLineOptions by lazy {
        MapboxRouteLineOptions.Builder(this)
            .withRouteLineResources(routeLineResources)
            .withRouteLineBelowLayerId("road-label")
            .build()
    }

    private val routeLineView by lazy {
        MapboxRouteLineView(options)
    }

    private val routeLineApi: MapboxRouteLineApi by lazy {
        MapboxRouteLineApi(options)
    }

    private val viewBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val initialPoint = getPoints().first().point

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        val viewOptions = BitmapFactory.Options().apply {
            inScaled = false
        }
        puckBitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.arrow_icon,
            viewOptions
        )
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted()) {
            initialize()
        } else {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onStop() {
        mapboxNavigation.unregisterRouteProgressObserver(replayProgressObserver)
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.unregisterRoutesObserver(routesObserver)
        mapboxNavigation.unregisterLocationObserver(locationObserver)
        super.onStop()
    }

    override fun onDestroy() {
        routeLineApi.cancel()
        routeLineView.cancel()
        mapboxReplayer.finish()
        MapboxNavigationProvider.destroy()
        super.onDestroy()
    }

    private fun initialize() {
        initStyle()
        locationComponent.locationPuck = LocationPuck2D(
            null,
            ContextCompat.getDrawable(this@MainActivity, R.drawable.navigation_puck_icon),
            null,
            null
        )
        navigationLocationProvider.lastLocation

        initNavigation()
        val startingLocation = com.mapbox.common.location.Location.Builder()
            .latitude(initialPoint.latitude())
            .longitude(initialPoint.longitude())
            .source("ReplayRoute")
            .build()
        navigationLocationProvider.changePosition(startingLocation.toAndroidLocation())
        viewBinding.btnStart.setOnClickListener {
            startSimulation()
        }
    }

    private fun initStyle() {
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS) { style ->
            val cameraOptions = CameraOptions.Builder().center(initialPoint).zoom(14.0).build()
            mapboxMap.setCamera(cameraOptions)


            tempShowPoints(style, getPoints())
            initTestRoute()
        }
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    private fun initNavigation() {
        mapboxNavigation = MapboxNavigationProvider.create(
            NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build()
        ).apply {
            registerRoutesObserver(routesObserver)
            registerLocationObserver(locationObserver)
        }
        mapboxReplayer = mapboxNavigation.mapboxReplayer
        replayProgressObserver = ReplayProgressObserver(mapboxReplayer)
        mapboxNavigation.registerRouteProgressObserver(replayProgressObserver)
    }

    private val routesObserver: RoutesObserver = RoutesObserver { result ->
        val routes = result.navigationRoutes.map { NavigationRouteLine(it, null) }
        routeLineApi.setNavigationRouteLines(routes) { value ->
            mapboxMap.getStyle()?.apply {
                routeLineView.renderRouteDrawData(this, value)
            }
        }
    }

    private fun permissionsGranted(): Boolean {
        requiredPermissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true
    }

    private fun initTestRoute() {
        routeLineApi.setNavigationRoutes(listOf(navigationRoute)) { resp ->
            mapboxMap.getStyle()?.apply {
                routeLineView.renderRouteDrawData(this, resp)
            }
        }
    }

    private val locationObserver = object : LocationObserver {
        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            navigationLocationProvider.changePosition(
                locationMatcherResult.enhancedLocation,
                locationMatcherResult.keyPoints,
            )
            updateCamera(
                com.mapbox.common.location.Location.Builder()
                    .latitude(locationMatcherResult.enhancedLocation.latitude)
                    .longitude(locationMatcherResult.enhancedLocation.longitude)
                    .timestamp(locationMatcherResult.enhancedLocation.time)
                    .build()
            )
        }

        override fun onNewRawLocation(rawLocation: Location) {
            //
        }
    }

    private fun updateCamera(location: com.mapbox.common.location.Location) {
        val mapAnimationOptionsBuilder = MapAnimationOptions.Builder()
        val cameraBuilder = CameraOptions.Builder()
            .center(Point.fromLngLat(location.longitude, location.latitude))
            .pitch(45.0)
            .zoom(14.0)
            .padding(EdgeInsets(1000.0, 0.0, 0.0, 0.0))
        location.bearing?.let { actualBearing ->
            cameraBuilder.bearing(actualBearing)
        }
        viewBinding.mapView.camera.easeTo(
            cameraBuilder.build(),
            mapAnimationOptionsBuilder.build()
        )
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
    @SuppressLint("MissingPermission")
    private fun startSimulation() {
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        mapboxNavigation.startReplayTripSession(withForegroundService = false)

        mapboxReplayer.stop()
        mapboxReplayer.clearEvents()
        mapboxReplayer.pushRealLocation(this, 0.0)
        mapboxReplayer.playbackSpeed(2.0) // todo what is a good speed?
        val replayData: List<ReplayEventBase> = replayRouteMapper.mapDirectionsRouteGeometry(navigationRoute.directionsRoute)
        mapboxReplayer.pushEvents(replayData)
        mapboxReplayer.seekTo(replayData[0])
        mapboxReplayer.play()
    }

    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        routeLineApi.updateWithRouteProgress(routeProgress) { result ->
            mapboxMap.getStyle()?.apply {
                routeLineView.renderRouteLineUpdate(this, result)
            }
        }
    }

    // todo remove this
    private val LINE_END_LAYER_ID = "DRAW_UTIL_LINE_END_LAYER_ID"
    private val LINE_END_SOURCE_ID = "DRAW_UTIL_LINE_END_SOURCE_ID"
    private val LINE_END_POINT_TYPE = "LINE_END_POINT_TYPE"
    private fun tempShowPoints(style: Style, points: List<ExpPoint>) {
        if (!style.styleSourceExists(LINE_END_SOURCE_ID)) {
            geoJsonSource(LINE_END_SOURCE_ID) {}.bindTo(style)
        }

        if (!style.styleLayerExists(LINE_END_LAYER_ID)) {
            CircleLayer(LINE_END_LAYER_ID, LINE_END_SOURCE_ID)
                .circleRadius(
                    match {
                        get {
                            literal(LINE_END_POINT_TYPE)
                        }
                        literal("key")
                        literal(7.0)
                        literal(4.0)
                    }
                )
                .circleOpacity(1.0)
                .circleColor(
                    match {
                        get {
                            literal(LINE_END_POINT_TYPE)
                        }
                        literal("key")
                        rgb(0.0, 0.0, 255.0)
                        rgb(0.0, 0.0, 0.0)
                    }
                )
                .bindTo(style)
        }

        val features = points.map {
            Feature.fromGeometry(it.point).apply {
                addStringProperty(LINE_END_POINT_TYPE, if (it is ExpPoint.KeyPoint) "key" else "tween")
            }
        }

        (mapboxMap.getStyle()?.getSource(LINE_END_SOURCE_ID) as GeoJsonSource).apply {
            this.featureCollection(FeatureCollection.fromFeatures(features))
        }
    }



    private companion object {
        private const val PERMISSION_REQUEST_CODE = 111
    }
}