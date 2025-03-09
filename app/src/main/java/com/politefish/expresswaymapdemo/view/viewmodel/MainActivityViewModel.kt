package com.politefish.expresswaymapdemo.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.politefish.expresswaymapdemo.domain.GetLocationMatchUseCase
import com.politefish.expresswaymapdemo.domain.model.LocationAndBearing
import com.politefish.expresswaymapdemo.domain.model.PointData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val getLocationMatchUseCase: GetLocationMatchUseCase,
): ViewModel() {

    private val expresswayDiagramSink: MutableSharedFlow<PointData> = MutableSharedFlow()
    val expresswayDiagramUpdates = expresswayDiagramSink.asSharedFlow()

    fun updateLocation(locationData: LocationAndBearing) {
        viewModelScope.launch {
            val pointDataDef = async(Dispatchers.Default) {
                getLocationMatchUseCase.invoke(locationData)
            }
            val pointData = pointDataDef.await()
            // todo should be checking the distance delta to
            // stop updating when driver is off expressway.
            expresswayDiagramSink.emit(pointData)
        }
    }
}