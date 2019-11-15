package com.brewfinder.app.ui.user.fragment.home_fragment.hot_deals


import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.brewfinder.app.R
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import com.brewfinder.app.utils.showToast
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.Geofence
import com.google.firebase.database.DatabaseError
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.*
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import kotlinx.android.synthetic.main.fragment_hot_deals.*


/**
 * A simple [Fragment] subclass.
 */
class HotDealsFragment : BaseFragment(),
    OnMapReadyCallback, OnLocationClickListener, PermissionsListener,
    OnCameraTrackingChangedListener, GeoQueryEventListener {


    //    lateinit var  mGeoQuery :GeoQuery
    private var mGeojsonSourceLayerId = "mGeojsonSourceLayerId"
    private var mPermissionsManager: PermissionsManager? = null
    private var mLatitude: Double? = null
    private var mLat: Double? = null
    private var mLong: Double? = null
    private var mLongitude: Double? = null
    private var mGeoQuery: GeoQuery? = null
    private var selectedUserLatlng: LatLng? = null
    private var mMapboxMap: MapboxMap? = null
    private var mLocationComponent: LocationComponent? = null
    private var mIsInTrackingMode: Boolean = false
    private val mGeoRange = arrayOf("5", "10", "20", "30", "40", "50")
    private val symbolIconId = "symbolIconId"
    private val mDealArrayList = arrayListOf<Deal>()
    private var mFirebseRepository = FireBaseRepository()


    override val mViewModel: BaseViewModel?
        get() = null

    //    //mGeoQuery
    override fun onGeoQueryReady() {

        mFirebseRepository.getUserDealDB(object : FirebaseNetworkCallBack {
            override fun onSuccess(response: Any) {
                mDealArrayList.clear()
                mDealArrayList.addAll(response as Collection<Deal>)
//                mDealArrayList=response
                if (mDealArrayList.size != 0) {
                    for (marker in mDealArrayList.indices) {
                        val latlng =
                            mDealArrayList[marker].deal_latlng  // "30.7089023,76.7027965"//
                        val latlong = latlng?.split(',')
                        val latitude = latlong!![0].toDouble()
                        val longitude = latlong[1].toDouble()
                        mMapboxMap?.addMarker(
                            MarkerOptions()
                                .position(LatLng(latitude, longitude))
                                .title(mDealArrayList[marker].deal_companyName)
                        )
                    }
                } else {
                    showToast("array is null")
                }
            }
            override fun onError(errorMessages: String) {
                showToast(errorMessages)
            }
        })

        mMapboxMap?.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }

    }

    //mGeoQuery
    override fun onKeyEntered(key: String?, location: GeoLocation?) {

//        val  geofence: Geofence = Geofence.Builder()
////            .setRequestId(GEOFENCE_REQ_ID) // Geofence ID
//            .setCircularRegion( location!!.latitude, location.longitude, 50F) // defining fence region
//            .setExpirationDuration( 3000 ) // expiring date
//            // Transition types that it should look for
////            .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER, Geofence.GEOFENCE_TRANSITION_EXIT )
//            .build()
        //
        //  showToast(key.toString() + location)
    }

    //mGeoQuery
    override fun onKeyMoved(key: String?, location: GeoLocation?) {
//        showToast(key.toString() + location)
    }

    //mGeoQuery
    override fun onKeyExited(key: String?) {
//        showToast(key.toString())
    }

    //mGeoQuery
    override fun onGeoQueryError(error: DatabaseError?) {
//        showToast(error.toString())
    }

    //mapbox
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mMapboxMap = mapboxMap
        mapboxMap.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }
    }

    //LocationComponent
    override fun onLocationComponentClick() {
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onPermissionResult(granted: Boolean) {
        //if permission false hai to apni location
        // if true hi to fine
        if (granted) {
            mMapboxMap!!.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }
        } else {
            showToast("Permission Is not Granted")
        }
    }

    override fun onCameraTrackingChanged(currentMode: Int) {

    }

    override fun onCameraTrackingDismissed() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hot_deals, container, false)
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val arrayAdpt = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, mGeoRange)
        spinner_range.adapter = arrayAdpt

        val range_item = spinner_range.selectedItem.toString()

        // "30.7089023,76.7027965"//

        //reverse code is  not function

//        val reverseGeocode = MapboxGeocoding.builder()
//            .accessToken(getString(R.string.mapbox_access_token))
//            .query(Point.fromLngLat(mLongitude!!, mLatitude!!))
//            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
//            .build()

        // Demo is User tnd on IVY HOSPITAL

        // creates a new query around

        val latlng = PrefrenceUtil(mActivity).curretLatLng  // "30.7089023,76.7027965"//
        if (latlng == "") {
            mLatitude = 30.7089023
            mLongitude = 76.7027965
        } else {
            val latlong = latlng?.split(',')
            mLatitude = latlong!![0].toDouble()
            mLongitude = latlong[1].toDouble()
        }


        //reverse code is  not function

//        val reverseGeocode = MapboxGeocoding.builder()
//            .accessToken(getString(R.string.mapbox_access_token))
//            .query(Point.fromLngLat(mLongitude!!, mLatitude!!))
//            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
//            .build()

        text_address.text = "Current Location: Sector 72, Mohali, SAS Nagar, Punjab, India"

        // Demo is User tnd on IVY HOSPITAL
        mGeoQuery = mFirebseRepository.mGeoFire.queryAtLocation(GeoLocation(mLatitude!!, mLongitude!!), 0.6)
//
        mGeoQuery?.addGeoQueryEventListener(this)

        val mile = arrayOf("Mile")

        val arrayAdptMile = ArrayAdapter(activity!!, android.R.layout.simple_spinner_dropdown_item, mile)

        spinner_miles.adapter = arrayAdptMile

        Mapbox.getInstance(mActivity, getString(R.string.mapbox_access_token))

        mapViewHotFragment.onCreate(savedInstanceState)

        mapViewHotFragment.getMapAsync(this)

        btn_changelocation.setOnClickListener {
            goToPickerActivity()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun enableLocationComponent(loadedMapStyle: Style) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(mActivity)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(mActivity)
                .elevation(5f)
                .accuracyAlpha(.6f)
                .accuracyColor(Color.RED)
                .build()

//                .foregroundDrawable(com.brewfinder.app.R.drawable.ic_blue_pin)

            // Get an instance of the component
            mLocationComponent = mMapboxMap?.locationComponent

            val locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(mActivity, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .useDefaultLocationEngine(true)
                .build()

            // Activate with options
            mLocationComponent?.activateLocationComponent(locationComponentActivationOptions)

            // Enable to make component visible
            mLocationComponent?.isLocationComponentEnabled = true

            // Set the component's camera mode
            mLocationComponent?.cameraMode = CameraMode.TRACKING
            // Set the component's render mode
            mLocationComponent?.renderMode = RenderMode.COMPASS

            // Add the location icon click listener
            mLocationComponent?.addOnLocationClickListener(this)

            // Add the camera tracking listener. Fires if the map camera is manually moved.
//            mLocationComponent?.addOnCameraTrackingChangedListener(this)

            val latlng = PrefrenceUtil(mActivity).curretLatLng  // "30.7089023,76.7027965"//
            if (latlng == "") {
                mLatitude = 23.2134896
                mLongitude = 78.8204883
            } else {
                val latlong = latlng?.split(',')
                mLatitude = latlong!![0].toDouble()
                mLongitude = latlong[1].toDouble()
            }

//            // creates a new query around
//            val latlonng: String? = PrefrenceUtil(mActivity).curretLatLng  // "30.7089023,76.7027965"//
//            val latlong = latlonng?.split(',')
//            val latitude = latlong!![0].toDouble()
//            val longitude = latlong[1].toDouble()

            val position = CameraPosition.Builder()
                .target(LatLng(mLatitude!!, mLongitude!!))
                .zoom(14.0)
                .tilt(0.0)
                .build()

            for (i in 1..3) {
                mLocationComponent?.cameraMode = CameraMode.TRACKING
                mLocationComponent?.zoomWhileTracking(16.0)
            }

            //location button
            back_to_camera_tracking_mode.setOnClickListener {

                if (!mIsInTrackingMode) {

                    mIsInTrackingMode = true
                    mLocationComponent?.cameraMode = CameraMode.TRACKING
                    mLocationComponent?.zoomWhileTracking(16.0)
//
//                    //set current ltlng in pref.
                    PrefrenceUtil(mActivity).curretLatLng =
                        "${mLocationComponent?.lastKnownLocation?.latitude},${mLocationComponent?.lastKnownLocation?.longitude}"

                    text_address.text =
                        "Current Location: Sector 72, Mohali, SAS Nagar, Punjab, India"

//                    Toast.makeText(mActivity,"${mLocationComponent?.lastKnownLocation?.latitude},${mLocationComponent?.lastKnownLocation?.longitude}" , Toast.LENGTH_SHORT).show()
                } else {
                    mIsInTrackingMode = false
                    //Toast.makeText(mActivity, getString(com.brewfinder.app.R.string.tracking_already_enabled), Toast.LENGTH_SHORT).show()
                }
            }
            mMapboxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5)

//            mFirebseRepository.setLocationGeoFire(mLocationComponent?.lastKnownLocation?.latitude!!, mLocationComponent?.lastKnownLocation?.longitude!!)

        } else {
            mPermissionsManager = PermissionsManager(this)

            mPermissionsManager!!.requestLocationPermissions(mActivity)
        }

        mMapboxMap?.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }

    }

    override fun onStart() {
        super.onStart()
        mapViewHotFragment.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapViewHotFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapViewHotFragment.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapViewHotFragment.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewHotFragment.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewHotFragment.onLowMemory()
    }


    private fun goToPickerActivity() {
        val latlng = PrefrenceUtil(activity!!).curretLatLng  // "30.7089023,76.7027965"//

        if (latlng == "") {
            mLat = 23.2134896
            mLong = 78.8204883
        } else {
            val latlong = latlng?.split(',')
            mLat = latlong!![0].toDouble()
            mLong = latlong[1].toDouble()
        }

        val camerPosition = CameraPosition.Builder()
        camerPosition.bearing(4.0)
        camerPosition.tilt(5.0)
        camerPosition.padding(4.0, 4.0, 4.0, 4.0)
        camerPosition.target(LatLng(mLat!!, mLong!!))
        camerPosition.zoom(16.0)


        startActivityForResult(
            PlacePicker.IntentBuilder()
                .accessToken(getString(R.string.mapbox_access_token))
                .placeOptions(
                    PlacePickerOptions.builder()
                        .statingCameraPosition(
                            camerPosition.build()
                        )
                        .build()
                )
                .build(activity!!), 5678
        )
    }

    @SuppressLint("MissingSuperCall", "SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

//        //location
        if (resultCode == RESULT_OK && requestCode == 5678) {

            // Retrieve selected location's CarmenFeature
            val selectedCarmenFeature: CarmenFeature = PlaceAutocomplete.getPlace(data)

            mLatitude = (selectedCarmenFeature.geometry() as Point).latitude()
            mLongitude = (selectedCarmenFeature.geometry() as Point).longitude()

            //selectedCarmenFeature.placeName()
            text_address.text = "Current Location: " + selectedCarmenFeature.placeName()

            val position = CameraPosition.Builder()
                .target(LatLng(mLatitude!!, mLongitude!!))
                .zoom(14.0)
                .tilt(5.0)
                .build()


            // Add the symbol layer icon to map for future use
            mMapboxMap!!.style?.addImage(
                symbolIconId,
                BitmapFactory.decodeResource(mActivity.resources, R.drawable.ic_pin)
            )

            mMapboxMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), 10)
//              mMapboxMap!!.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }
        }
    }

}

