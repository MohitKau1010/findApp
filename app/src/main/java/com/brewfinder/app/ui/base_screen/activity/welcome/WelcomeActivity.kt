package com.brewfinder.app.ui.base_screen.activity.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.brewfinder.app.BrewFinder
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.welcome.WelcomeFragment
import com.brewfinder.app.utils.MyCustomLoader


/**
 * @author Shubham
 * 28/9/19
 */

class WelcomeActivity : BaseActivity() {


    lateinit var locationManager: LocationManager
    private var REQUEST_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.brewfinder.app.R.layout.activity_welcome)


        if (savedInstanceState == null) {
//            hideSystemUI()
            PrefrenceUtil(BrewFinder.applicationContext()).clearPrefsData()

            doFragmentTransaction(
                    containerViewId = com.brewfinder.app.R.id.flWelcomeContainer,
                    fragment = WelcomeFragment(),
                    isAddToBackStack = true,
                    isAddFragment = true
            )
        }

        //get User LatLng
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //Check gps is enable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
//            onGPS()
            getLocation()

        } else {
            //GPS is already On then
            getLocation()
        }


    }

//    private fun turnGPSOn() {
//        val provider =
//            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
//
//        if (!provider.contains("gps")) { //if gps is disabled
//            val poke = Intent()
//            poke.setClassName(
//                "com.android.settings",
//                "com.android.settings.widget.SettingsAppWidgetProvider"
//            )
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
//            poke.data = Uri.parse("3")
//            sendBroadcast(poke)
//        }
//        //getLocation()
//    }
//
//    private fun turnGPSOff() {
//        val provider =
//            Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
//
//        if (provider.contains("gps")) { //if gps is enabled
//            val poke = Intent()
//            poke.setClassName(
//                "com.android.settings",
//                "com.android.settings.widget.SettingsAppWidgetProvider"
//            )
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE)
//            poke.data = Uri.parse("3")
//            sendBroadcast(poke)
//        }
//    }

    private fun onGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton(
                "YES"
        ) { _, _ ->
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            getLocation()

        }
                .setNegativeButton(
                        "NO"
                ) { _, _ ->
                    //                    dialog.cancel()
                }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    ///////////////////
    @SuppressLint("MissingPermission")
    private fun getLocation() {

//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(
                        this@WelcomeActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@WelcomeActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION
            )
        } else {
            val locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val locationNetwork =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val locationPassive =
                    locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            when {
                locationGps != null -> {
                    val lati = locationGps.latitude
                    val longi = locationGps.longitude
                    val latlng = "${lati},${longi}"

                    PrefrenceUtil(this).curretLatLng = latlng

                }
                locationNetwork != null -> {
                    val lat = locationNetwork.latitude
                    val longi = locationNetwork.longitude

                    val latlng = "${lat},${longi}"
                    PrefrenceUtil(this).curretLatLng = latlng

                }
                locationPassive != null -> {
                    val lat = locationPassive.latitude
                    val longi = locationPassive.longitude

                    val latlng = "${lat},${longi}"
                    PrefrenceUtil(this).curretLatLng = latlng


                }
                else -> Toast.makeText(this, "Can't Get Your Location On your GPS ", Toast.LENGTH_SHORT).show()
                //                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            //Thats All Run Your App
        }

//
    }

    override fun onBackPressed() {

        if (1 < supportFragmentManager.backStackEntryCount) {
            supportFragmentManager.popBackStackImmediate()
        } else {


            MyCustomLoader(this@WelcomeActivity).showAlertDialog("Alert!!",
                    "Do you want to Exit this Application?",
                    "OK",
                    "CANCEL",
                DialogInterface.OnClickListener { _, _ ->
                        //                    dialog?.dismiss()
                        finish()
                        super.onBackPressed()
                        // mMyCustomLoader.dismissProgressDialog()
                    },
                DialogInterface.OnClickListener { dialog, _ ->
                        dialog?.dismiss()
                        //mMyCustomLoader.dismissProgressDialog()
                    })

        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> onGPS()
//                    Toast.makeText(this@WelcomeActivity,"App need of grant permission",Toast.LENGTH_SHORT).show() //Tell to user the need of grant permission
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }


}
