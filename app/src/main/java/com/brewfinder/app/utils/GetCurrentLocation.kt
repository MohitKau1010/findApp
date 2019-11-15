package com.brewfinder.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.brewfinder.app.BrewFinder

class GetCurrentLocation {

    var currentLocation: String? = ""


    // Create a new Places client instance
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null


    @SuppressLint("MissingPermission", "SetTextI18n")
    fun getLocation(): String {
        locationManager = BrewFinder.applicationContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {
            //get from GPS if gps is true
            if (hasGps) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        100F,
                        object : LocationListener {
                            @SuppressLint("SetTextI18n")
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationGps = location

                                    currentLocation = "${locationGps!!.latitude},${locationGps!!.longitude}"

                                    //goToPickerActivity(locationGps!!.latitude ,  locationGps!!.longitude)
                                }
                            }

                            override fun onStatusChanged(
                                    provider: String?,
                                    status: Int,
                                    extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {
                            }

                            override fun onProviderDisabled(provider: String?) {
                            }
                        })
                val localGpsLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            //get location is if network is true
            if (hasNetwork) {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 1000, 100F,
                        object : LocationListener {
                            @SuppressLint("SetTextI18n")
                            override fun onLocationChanged(location: Location?) {
                                if (location != null) {
                                    locationNetwork = location

                                    currentLocation = "${locationNetwork!!.latitude},${locationNetwork!!.longitude}"
                                    //goToPickerActivity(locationNetwork!!.latitude ,  locationNetwork!!.longitude)
//                            tv_result.append("\nNetwork ")
//                            tv_result.append("\nLatitude : " + locationNetwork!!.latitude)
//                            tv_result.append("\nLongitude : " + locationNetwork!!.longitude)

                                }
                            }

                            override fun onStatusChanged(
                                    provider: String?,
                                    status: Int,
                                    extras: Bundle?
                            ) {
                            }

                            override fun onProviderEnabled(provider: String?) {
                            }

                            override fun onProviderDisabled(provider: String?) {
                            }
                        })

                val localNetworkLocation =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }
            if (locationGps != null && locationNetwork != null) {
                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {

                    currentLocation = "${locationNetwork!!.latitude},${locationNetwork!!.longitude}"
                    // goToPickerActivity(locationNetwork!!.latitude ,  locationNetwork!!.longitude)
//                    tv_result.append("\nNetwork ")
//                    tv_result.append("\nLatitude : " + locationNetwork!!.latitude)
//                    tv_result.append("\nLongitude : " + locationNetwork!!.longitude)
                } else {
                    currentLocation = "${locationGps!!.latitude},${locationGps!!.longitude}"
                    //goToPickerActivity(locationGps!!.latitude ,  locationGps!!.longitude)
//                  /  tv_result.append("\nGPS ")
//                    tv_result.append("\nLatitude : " + locationGps!!.latitude)
//                    tv_result.append("\nLongitude : " + locationGps!!.longitude)
                }
            }
        }
//        else {
//          // activity!!.startMyActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//        }
        return currentLocation.toString()
    }

}