package com.brewfinder.app.ui.user.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.brewfinder.app.R
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.welcome.WelcomeActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.UserHomeFragment
import com.brewfinder.app.ui.user.fragments.setting_fragment.UserSettingFragment
import com.brewfinder.app.ui.user.fragments.user_profile.UserProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home_user.*


class HomeUserActivity : BaseActivity() {

    //region variables
    private val requestLocation = 1
    /**
     * here we set navigtion  item selected listner
     * we set three navigatiuon menu items
     * 1. Home
     * 2. Profile
     * 3. Setting
     */
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction().hide(active!!).show(mUserHomeFrag)
                        .commit()
                    active = mUserHomeFrag

                }
                R.id.navigation_profile -> {

                    supportFragmentManager.beginTransaction().hide(active!!).show(mUserProfileFrag)
                        .commit()
                    active = mUserProfileFrag
                }
                R.id.navigation_setting -> {
                    supportFragmentManager.beginTransaction().hide(active!!).show(mUserSettingFrag)
                        .commit()
                    active = mUserSettingFrag
                }
            }
            true
        }
    lateinit var locationManager: LocationManager
    private var mLocationGPS: Location? = null
    private var mLocationNetwork: Location? = null
    private var mLocationPassive: Location? = null
    private var active: BaseFragment? = null
    private val mUserHomeFrag = UserHomeFragment()
    private val mUserProfileFrag = UserProfileFragment()
    private val mUserSettingFrag = UserSettingFragment()
    //endregion

    //region LifeCycle Method
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_user)

        //get User LatLng
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        //Check gps is enable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Write Function To enable gps
            onGPS()
//            getLocation()
        } else {
            //GPS is already On then
            for (i in 1..3) {
                getLocation()
            }

        }

        user_bottomNavigationView.setOnNavigationItemSelectedListener(
            mOnNavigationItemSelectedListener
        )

        /**
         * add fragments here
         */
        active = mUserHomeFrag
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout, mUserSettingFrag, "Setting").hide(mUserSettingFrag).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout, mUserProfileFrag, "Profile").hide(mUserProfileFrag).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.frame_layout, mUserHomeFrag, "Home").commit()
    }
    //endregion

    /**
     * gps is on
     */
    //region custom methods
    private fun onGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Enable GPS")
            .setCancelable(false)
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("YES") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//                getLocation()
            }
            .setNegativeButton("NO") { _, _ ->
                //  dialog.cancel()
                getLocation()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(
                this@HomeUserActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@HomeUserActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                requestLocation
            )
        } else {
            mLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            mLocationNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            mLocationPassive =
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            when {
                mLocationGPS != null -> {
                    val lat = mLocationGPS!!.latitude
                    val longi = mLocationGPS!!.longitude
                    val latlng = "${lat},${longi}"

                    PrefrenceUtil(this).curretLatLng = latlng
                }
                mLocationNetwork != null -> {
                    val lat = mLocationNetwork!!.latitude
                    val longi = mLocationNetwork!!.longitude

                    val latlng = "${lat},${longi}"
                    PrefrenceUtil(this).curretLatLng = latlng

                }
                mLocationPassive != null -> {
                    val lat = mLocationPassive!!.latitude
                    val longi = mLocationPassive!!.longitude

                    val latlng = "${lat},${longi}"
                    PrefrenceUtil(this).curretLatLng = latlng
                }
                else ->
                    Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            /**
             * Thats All Run Your App
             */
        }
    }
    //endregion

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED ->
                    Toast.makeText(
                        this@HomeUserActivity,
                        "App need of grant permission",
                        Toast.LENGTH_SHORT
                    ).show() //Tell to user the need of grant permission
            }
        }
    }

    fun toggleBottomNavigation(isShowBottomView: Boolean) {
        if (isShowBottomView) user_bottomNavigationView.visibility = View.VISIBLE
        else
            user_bottomNavigationView.visibility = View.GONE

    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100
    }

}
