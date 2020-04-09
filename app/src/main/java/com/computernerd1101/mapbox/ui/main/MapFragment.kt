package com.computernerd1101.mapbox.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.computernerd1101.mapbox.R
import com.mapbox.mapboxsdk.camera.*
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.offline.*

class MapFragment : Fragment() {

    companion object {
        @Suppress("unused")
        fun newInstance() = MapFragment()

        private const val TAG = "MapFragment"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private lateinit var viewModel: MapViewModel

    private var mapView: MapView? = null

    private var mapboxMap: MapboxMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View = inflater.inflate(R.layout.map_fragment, container, false)
        viewModel = ViewModelProvider(this)[MapViewModel::class.java]
        val mapView: MapView = root.findViewById(R.id.mapView)
        this.mapView = mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            Log.d(TAG, "getMapAsync")
            mapboxMap = map
            map.setOfflineRegionDefinition(viewModel.definition)
        }
        return root
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_zion -> {
                navigateToZion()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToZion() {
        viewModel.offlineManager.listOfflineRegions(
            object: OfflineManager.ListOfflineRegionsCallback {

                override fun onList(offlineRegions: Array<out OfflineRegion>) {
                    Log.d(TAG, "Regions downloaded: ${offlineRegions.size}")
                    if (offlineRegions.isEmpty()) return
                    val mapboxMap = this@MapFragment.mapboxMap ?: return
                    val definition = offlineRegions[0].definition
                            as OfflineTilePyramidRegionDefinition
                    val bounds = definition.bounds
                    val regionZoom = definition.minZoom
                    val center = bounds.center
                    Log.d(TAG, center.toString())
                    val cameraPosition = CameraPosition.Builder()
                        .target(bounds.center)
                        .zoom(regionZoom)
                        .build()
                    mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }

                override fun onError(error: String) {
                    Log.e(TAG, "navigateToZion error: $error")
                }

            }
        )
    }

}
