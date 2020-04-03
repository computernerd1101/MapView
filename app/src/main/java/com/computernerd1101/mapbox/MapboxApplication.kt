package com.computernerd1101.mapbox

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox

class MapboxApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
    }

}