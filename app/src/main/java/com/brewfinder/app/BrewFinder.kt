package com.brewfinder.app

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.mapbox.mapboxsdk.Mapbox

/**
 * @author Mohit
 * 28/9/19
 */
internal class BrewFinder : Application() {

    init {
        instance = this
    }

    companion object {

        //for get Application Context
        private var instance: BrewFinder? = null


        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))


    }
}
