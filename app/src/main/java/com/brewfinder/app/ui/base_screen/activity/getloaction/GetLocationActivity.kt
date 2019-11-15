package com.brewfinder.app.ui.base_screen.activity.getloaction

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import com.brewfinder.app.R
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.OnLocationClickListener
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_get_location.*


class GetLocationActivity : BaseActivity(), OnMapReadyCallback, PermissionsListener,
    OnLocationClickListener {

    //OnLocationClickListener
    override fun onLocationComponentClick() {
    }

    //PermissionsListener
    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
    }

    //PermissionsListener
    override fun onPermissionResult(granted: Boolean) {
    }


    private val requestcodeautocomplete = 1
    private var REQUEST_LOCATION = 2
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var home: CarmenFeature? = null
    private var work: CarmenFeature? = null
    private val geojsonSourceLayerId = "geojsonSourceLayerId"
    private var permissionsManager: PermissionsManager? = null
    private var locationComponent: LocationComponent? = null
    private var isInTrackingMode: Boolean = false
    private val symbolIconId = "symbolIconId"
    private var lat: Double? = null
    private var lng: Double? = null


    var source: GeoJsonSource? = null
    lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get User LatLng
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


//        //Check gps is enable or not
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            //Write Function To enable gps
//            gps()
//        } else {
//            //GPS is already On then
//            getLocation()
//        }


        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this@GetLocationActivity, getString(R.string.mapbox_access_token))

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_get_location)

        mapView = findViewById(R.id.mapView)
        mapView!!.onCreate(savedInstanceState)
        mapView!!.getMapAsync(this)


    }

    override fun onMapReady(mapboxMap1: MapboxMap) {
        this.mapboxMap = mapboxMap1

        mapboxMap!!.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }

//        mapboxMap!!.setStyle(Style.LIGHT) { style ->
//            initSearchFab()
//             enableLocationComponent(style)
//
//            // Add the symbol layer icon to map for future use
//            style.addImage(
//                symbolIconId,
//                BitmapFactory.decodeResource(this@GetLocationActivity.resources, R.drawable.ic_pin)
//            )
//
//            // Create an empty GeoJSON source using the empty feature collection
//            setUpSource(style)
//
//            // Set up a new symbol layer for displaying the searched location's feature coordinates
//            setupLayer(style)
//
//        }

//        mapboxMap?.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }

    }

    private fun initSearchFab() {
        fab_location_search.setOnClickListener {

            mapboxMap!!.setStyle(Style.LIGHT) { style ->
                // Add the symbol layer icon to map for future use
                style.addImage(
                    symbolIconId,
                    BitmapFactory.decodeResource(
                        this@GetLocationActivity.resources,
                        R.drawable.ic_pin
                    )
                )

                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style)

                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style)
            }

            val intent = PlaceAutocomplete.IntentBuilder()
                .accessToken(getString(R.string.mapbox_access_token))
                .placeOptions(
                    PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .build(PlaceOptions.MODE_CARDS)
                )
                .build(this@GetLocationActivity)
            startActivityForResult(intent, requestcodeautocomplete)

        }

        fab_location_done.setOnClickListener {
            //1. upload ltlng to userModel
            //2.
            finish()

        }

    }


    private fun setUpSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(geojsonSourceLayerId))
    }

    private fun setupLayer(loadedMapStyle: Style) {
        loadedMapStyle.addLayer(
            SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(arrayOf(0f, -8f))
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)




        if (resultCode == Activity.RESULT_OK && requestCode == requestcodeautocomplete) {

            // Retrieve selected location's CarmenFeature
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data!!)

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon

            if (mapboxMap != null) {
                val style = mapboxMap!!.style

                source = style!!.getSourceAs(geojsonSourceLayerId)

                if (source != null) {

                    source!!.setGeoJson(
                        FeatureCollection.fromFeatures(
                            arrayOf<Feature>(Feature.fromJson(selectedCarmenFeature.toJson()))
                        )
                    )
                }


//                  // Move map camera to the selected location
                val intent = Intent()

                with((selectedCarmenFeature.geometry() as Point)) {
                    mapboxMap!!.animateCamera(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder().apply {
                                target(
                                    LatLng(
                                        latitude(),
                                        longitude()
                                    )
                                )
                                zoom(16.0)

                            }.build()
                        ), 2000
                    )



                    intent.putExtra("latLong", "${latitude()} , ${longitude()}")
                    intent.putExtra("address", selectedCarmenFeature.placeName())

                }

                setResult(Activity.RESULT_OK, intent)

            }
        }
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()

        mapView!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    private fun gps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton(
            "YES"
        ) { _, _ ->
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            //getLocation()
        }
            .setNegativeButton(
                "NO"
            ) { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    ///////////////////
//    @SuppressLint("MissingPermission")
//    private fun getLocation() {
////        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        //Check Permissions again
//        if (ActivityCompat.checkSelfPermission(
//                this@GetLocationActivity,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this@GetLocationActivity,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION
//            )
//        } else {
//            val LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            val LocationNetwork =
//                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//            val LocationPassive =
//                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
//            if (LocationGps != null) {
//                lat = LocationGps.latitude
//                lng = LocationGps.longitude
//                val latlng = "${lat},${lng}"
//
//                PrefrenceUtil(this@GetLocationActivity).curretLatLng = latlng
////                latitude = lat.toString()
////                longitude = longi.toString()
////                showLocationTxt.setText("Your Location:\nLatitude= $latitude\nLongitude= $longitude")
//            } else if (LocationNetwork != null) {
//                lat = LocationNetwork.latitude
//                lng = LocationNetwork.longitude
//
//                val latlng = "${lat},${lng}"
//                PrefrenceUtil(this@GetLocationActivity).curretLatLng = latlng
////                latitude = lat.toString()
////                longitude = longi.toString()
////                showLocationTxt.setText("Your Location:\nLatitude= $latitude\nLongitude= $longitude")
//            } else if (LocationPassive != null) {
//                lat = LocationPassive.latitude
//                lng = LocationPassive.longitude
//
//                val latlng = "${lat},${lng}"
//                PrefrenceUtil(this@GetLocationActivity).curretLatLng = latlng
////                latitude = lat.toString()
////                longitude = longi.toString()
////                showLocationTxt.setText("Your Location:\nLatitude= $latitude\nLongitude= $longitude")
//            } else {
//                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show()
////                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
//            }
//            //Thats All Run Your App
//        }
//
////
//    }


    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {

        initSearchFab()

        // Add the symbol layer icon to map for future use
        loadedMapStyle.addImage(
            symbolIconId,
            BitmapFactory.decodeResource(this@GetLocationActivity.resources, R.drawable.ic_pin)
        )

        // Create an empty GeoJSON source using the empty feature collection
        setUpSource(loadedMapStyle)

        // Set up a new symbol layer for displaying the searched location's feature coordinates
        setupLayer(loadedMapStyle)

        ///////////////////

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .elevation(5f)
                .accuracyAlpha(.6f)
                .accuracyColor(Color.RED)
                .build()

//                .foregroundDrawable(com.brewfinder.app.R.drawable.ic_blue_pin)

            // Get an instance of the component
            locationComponent = mapboxMap?.locationComponent

            val locationComponentActivationOptions = LocationComponentActivationOptions
                .builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .useDefaultLocationEngine(true)
                .build()

            // Activate with options
            locationComponent?.activateLocationComponent(locationComponentActivationOptions)

            // Enable to make component visible
            locationComponent?.isLocationComponentEnabled = true

            // Set the component's camera mode
            locationComponent?.cameraMode = CameraMode.TRACKING
            // Set the component's render mode
            locationComponent?.renderMode = RenderMode.COMPASS

            // Add the location icon click listener
            locationComponent?.addOnLocationClickListener(this)


            fab_current_location.setOnClickListener {
                if (!isInTrackingMode) {
                    isInTrackingMode = true
                    locationComponent?.cameraMode = CameraMode.TRACKING
                    locationComponent?.zoomWhileTracking(16.0)

                    //Toast.makeText(mActivity, getString(com.brewfinder.app.R.string.tracking_already_enabled), Toast.LENGTH_SHORT).show()
                } else {
                    isInTrackingMode = false
                    //Toast.makeText(mActivity, getString(com.brewfinder.app.R.string.tracking_already_enabled), Toast.LENGTH_SHORT).show()
                }
            }
            // when press current location button
            locationComponent?.cameraMode = CameraMode.TRACKING
            locationComponent?.zoomWhileTracking(5.0)

//            val position = CameraPosition.Builder()
//                .target(LatLng(30.7089023, 76.7027965))
//                .zoom(14.0)
//                .tilt(0.0)
//                .build()
//
//
//            mapboxMap?.animateCamera(CameraUpdateFactory.newCameraPosition(position), 5)

        } else {
            permissionsManager = PermissionsManager(this)

            permissionsManager!!.requestLocationPermissions(this)
        }

        mapboxMap?.setStyle(Style.LIGHT) { style -> enableLocationComponent(style) }
    }
}
