package com.computernerd1101.mapbox.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.computernerd1101.mapbox.MapBoxApplication
import com.mapbox.mapboxsdk.maps.MapboxMap

class MapViewModelFactory(
    private val application: MapBoxApplication,
    private val mapboxMap: MapboxMap
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java))
            return MapViewModel(application) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}