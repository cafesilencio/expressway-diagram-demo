package com.politefish.expresswaymapdemo.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Paint
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.replay.route.ReplayRouteMapper
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.NavigationStyles
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.lifecycle.NavigationBasicGesturesHandler
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.utils.internal.extensions.getBitmap
import com.politefish.expresswaymapdemo.R
import com.politefish.expresswaymapdemo.databinding.ActivityMainBinding
import com.politefish.expresswaymapdemo.domain.model.ExpImagePuckPosition
import com.politefish.expresswaymapdemo.domain.model.LocationAndBearing
import com.politefish.expresswaymapdemo.support.SupportUtils.getTestRoute
import com.politefish.expresswaymapdemo.view.viewmodel.MainActivityViewModel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val requiredPermissions = arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)
    private val viewModel: MainActivityViewModel by inject()
    private val mapboxReplayer = MapboxReplayer()
    private val replayLocationEngine = ReplayLocationEngine(mapboxReplayer)
    private val replayProgressObserver = ReplayProgressObserver(mapboxReplayer)
    private lateinit var binding: ActivityMainBinding
    private val navigationCamera by lazy {
        NavigationCamera(
            binding.mapView.getMapboxMap(),
            binding.mapView.camera,
            viewportDataSource
        )
    }
    private val viewportDataSource: MapboxNavigationViewportDataSource by lazy {
        MapboxNavigationViewportDataSource(binding.mapView.getMapboxMap())
    }

    private val expresswayDiagramBitmap: Bitmap by lazy {
        AppCompatResources.getDrawable(this, R.drawable.expressway_diagram)!!.getBitmap()
    }

    private val expresswayDiagramPuckBitmap by lazy {
        AppCompatResources.getDrawable(this, R.drawable.arrow_icon)!!.getBitmap()
    }

    private val puckPaint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
    }

    private val pixelDensity = Resources.getSystem().displayMetrics.density
    private val overviewPadding: EdgeInsets by lazy {
        EdgeInsets(
            140.0 * pixelDensity,
            40.0 * pixelDensity,
            120.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    private val followingPadding: EdgeInsets by lazy {
        EdgeInsets(
            180.0 * pixelDensity,
            40.0 * pixelDensity,
            150.0 * pixelDensity,
            40.0 * pixelDensity
        )
    }

    private val mapboxRouteLineOptions by lazy {
        MapboxRouteLineOptions.Builder(this)
            .withRouteLineBelowLayerId("road-label-navigation")
            .build()
    }

    private val routeLineApi by lazy {
        MapboxRouteLineApi(mapboxRouteLineOptions)
    }
    private val routeLineView by lazy {
        MapboxRouteLineView(mapboxRouteLineOptions)
    }

    private var lastLocation: LocationAndBearing? = null
    private val navigationLocationProvider = NavigationLocationProvider()

    private val locationObserver = object : LocationObserver {
        var firstLocationUpdateReceived = false

        override fun onNewRawLocation(rawLocation: Location) {
            // not handled
        }

        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            navigationLocationProvider.changePosition(
                location = enhancedLocation,
                keyPoints = locationMatcherResult.keyPoints,
            )

            viewportDataSource.onLocationChanged(enhancedLocation)
            viewportDataSource.evaluate()

            lastLocation = LocationAndBearing(
                Point.fromLngLat(enhancedLocation.longitude, enhancedLocation.latitude),
                enhancedLocation.bearing
            )

            if (!firstLocationUpdateReceived) {
                firstLocationUpdateReceived = true
                navigationCamera.requestNavigationCameraToOverview(
                    stateTransitionOptions = NavigationCameraTransitionOptions.Builder()
                        .maxDuration(0)
                        .build()
                )
            }
        }
    }

    private val routeProgressObserver = RouteProgressObserver { routeProgress ->
        viewportDataSource.onRouteProgressChanged(routeProgress)
        viewportDataSource.evaluate()

        lastLocation?.apply {
            viewModel.updateLocation(this)
        }
    }

    private val routesObserver = RoutesObserver { routeUpdateResult ->
        if (routeUpdateResult.navigationRoutes.isNotEmpty()) {
            routeLineApi.setNavigationRoutes(
                routeUpdateResult.navigationRoutes
            ) { value ->
                binding.mapView.getMapboxMap().getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }
            viewportDataSource.onRouteChanged(routeUpdateResult.navigationRoutes.first())
            viewportDataSource.evaluate()
        }
    }

    private val mapboxNavigation: MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            @SuppressLint("MissingPermission")
            override fun onAttached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.registerRoutesObserver(routesObserver)
                mapboxNavigation.registerLocationObserver(locationObserver)
                mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.registerRouteProgressObserver(replayProgressObserver)
                mapboxNavigation.startTripSession()
            }

            override fun onDetached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.stopTripSession()
                mapboxNavigation.unregisterRoutesObserver(routesObserver)
                mapboxNavigation.unregisterLocationObserver(locationObserver)
                mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
                mapboxNavigation.unregisterRouteProgressObserver(replayProgressObserver)
            }
        },
        onInitialize = this::initNavigation
    )

    private val initialPoint = Point.fromLngLat(139.791604, 35.518599)
    private val navigationRoute: NavigationRoute by lazy {
        getTestRoute(this)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewportDataSource.overviewPadding = overviewPadding
        viewportDataSource.followingPadding = followingPadding

        binding.btnStart.setOnClickListener {
            setRouteAndStartNavigation(listOf(navigationRoute))
            binding.btnStart.visibility = View.INVISIBLE
            binding.expresswayDiagramView.visibility = View.VISIBLE
        }

        lifecycleScope.launch {
            viewModel.expresswayDiagramUpdates.collect { diagramPointData ->
                val puckPosition = ExpImagePuckPosition(
                    diagramPointData.expPoint.getExpCoordinates().first,
                    diagramPointData.expPoint.getExpCoordinates().second,
                    diagramPointData.puckBearing,
                )
                binding.expresswayDiagramView.updateDiagramImage(
                    expresswayDiagramBitmap,
                    puckPosition,
                    expresswayDiagramPuckBitmap,
                    puckPaint,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (permissionsGranted()) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun initMap() {
        binding.mapView.camera.addCameraAnimationsLifecycleListener(
            NavigationBasicGesturesHandler(navigationCamera)
        )
        binding.mapView.getMapboxMap().loadStyleUri(NavigationStyles.NAVIGATION_DAY_STYLE) { style ->
            //
        }
    }

    override fun onStop() {
        clearRouteAndStopNavigation()
        super.onStop()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapboxReplayer.finish()
        routeLineApi.cancel()
        routeLineView.cancel()
    }

    private fun initNavigation() {
        MapboxNavigationApp.setup(
            NavigationOptions.Builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                .locationEngine(replayLocationEngine)
                .build()
        )

        binding.mapView.location.apply {
            setLocationProvider(navigationLocationProvider)
            this.locationPuck = LocationPuck2D(
                bearingImage = ContextCompat.getDrawable(
                    this@MainActivity,
                    R.drawable.navigation_puck_icon
                )
            )
            enabled = true
        }

        replayOriginLocation()
    }

    private fun replayOriginLocation() {
        mapboxReplayer.pushEvents(
            listOf(
                ReplayRouteMapper.mapToUpdateLocation(
                    Date().time.toDouble(),
                    initialPoint
                )
            )
        )
        mapboxReplayer.playFirstLocation()
        mapboxReplayer.playbackSpeed(5.0)
    }

    private fun setRouteAndStartNavigation(routes: List<NavigationRoute>) {
        mapboxNavigation.setNavigationRoutes(routes)
        navigationCamera.requestNavigationCameraToOverview()
    }

    private fun clearRouteAndStopNavigation() {
        mapboxNavigation.setNavigationRoutes(listOf())

        mapboxReplayer.stop()
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

    private companion object {
        private const val PERMISSION_REQUEST_CODE = 111
    }
}
