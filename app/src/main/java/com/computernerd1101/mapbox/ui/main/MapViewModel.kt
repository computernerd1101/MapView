package com.computernerd1101.mapbox.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mapbox.mapboxsdk.geometry.*
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.offline.*
import kotlinx.coroutines.*

class MapViewModel(application: Application) : AndroidViewModel(application) {

    companion object {

        private const val TAG = "MapViewModel"

        private const val METADATA = "{\"regionName\":\"Zion National Park\"}"

    }

    private val viewModelJob = Job()

    @Suppress("unused")
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val offlineManager: OfflineManager = OfflineManager.getInstance(application.applicationContext)

    val definition: OfflineTilePyramidRegionDefinition

    init {
        offlineManager.setOfflineMapboxTileCountLimit(10000)
        val style = Style.MAPBOX_STREETS
        val latLngBounds: LatLngBounds = LatLngBounds.from(
            37.504266, -112.863380,
            37.141387, -113.228305
        )
        definition = OfflineTilePyramidRegionDefinition(
            style, latLngBounds, 10.0, 20.0,
            application.resources.displayMetrics.density
        )
        offlineManager.createOfflineRegion(definition, METADATA.toByteArray(),
            object : OfflineManager.CreateOfflineRegionCallback {

                override fun onCreate(offlineRegion: OfflineRegion) {
                    uiScope.launch { createOfflineRegion(offlineRegion) }
                }

                override fun onError(error: String) {
                    Log.e(TAG, "Error: $error")
                }

            })
    }

    private suspend fun createOfflineRegion(offlineRegion: OfflineRegion) {
        Log.d(TAG, "createOfflineRegion")
        withContext(Dispatchers.IO) {
            offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE)
            offlineRegion.setObserver(object: OfflineRegion.OfflineRegionObserver {

                private var percentage: Int = -1

                override fun onStatusChanged(status: OfflineRegionStatus) {
                    val required = status.requiredResourceCount
                    val oldPercentage = this.percentage
                    val percentage: Int = when {
                        status.isComplete -> {
                            101
                        }
                        required > 0L ->
                            (100 * status.completedResourceCount / required).toInt()
                        else -> 0
                    }
                    this.percentage = percentage
                    if (percentage > oldPercentage)
                        Log.d(
                            TAG, if (percentage >= 100)
                                "Region downloaded successfully."
                            else "$percentage% of region downloaded"
                        )
                }

                override fun onError(error: OfflineRegionError) {
                    Log.e(TAG, "onError reason: ${error.reason}")
                    Log.e(TAG, "onError message: ${error.message}")
                }

                override fun mapboxTileCountLimitExceeded(limit: Long) {
                    Log.e(TAG, "Mapbox tile count limit exceeded: $limit")
                }

            })
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
