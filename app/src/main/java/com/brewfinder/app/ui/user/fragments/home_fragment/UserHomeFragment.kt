package com.brewfinder.app.ui.user.fragments.home_fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.brewfinder.app.R
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.activity.welcome.WelcomeActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.user.fragment.home_fragment.hot_deals.HotDealsFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.deals.DealsFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.fav_deals.FavDealsFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.search_fragment.SearchFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.startMyActivityWithNewTask
import com.google.android.material.tabs.TabLayout
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import kotlinx.android.synthetic.main.fragment_user_home.*

/**
 * A simple [Fragment] subclass.
 */


class UserHomeFragment : BaseFragment() {
    override val mViewModel: BaseViewModel?
        get() = null

    private val mDealsFragment by lazy { DealsFragment() }
    private val mHotDealsFragment by lazy { HotDealsFragment() }
    private val mFirebaseRepository = FireBaseRepository()
    private var mMapboxMap: MapboxMap? = null
    private var mLat: Double? = null
    private var mLong: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // setUp ViewPager
        setupViewPager(viewpager)

        // set Tool bar UI
        setToolbarUi()

        // set TabSelected Listener
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val vg = tabs.getChildAt(0) as ViewGroup
                val vgTab = vg.getChildAt(tab!!.position) as ViewGroup
                val tabChildsCount = vgTab.childCount
                for (i in 0 until tabChildsCount) {
                    val tabViewChild = vgTab.getChildAt(i)
                    if (tabViewChild is TextView) {
                        tabViewChild.textSize = 14f
                        tabViewChild.setTextColor(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.txt_dark
                            )
                        )
                        tabViewChild.setTypeface(null, Typeface.NORMAL)

                    }
                }
                vgTab.setBackgroundResource(R.color.white)
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val vg = tabs.getChildAt(0) as ViewGroup
                val vgTab = vg.getChildAt(tab!!.position) as ViewGroup
                val tabChildsCount = vgTab.childCount
                for (i in 0 until tabChildsCount) {
                    val tabViewChild = vgTab.getChildAt(i)
                    if (tabViewChild is TextView) {
                        tabViewChild.textSize = 18f
                        tabViewChild.setTextColor(
                            ContextCompat.getColor(
                                activity!!,
                                R.color.white
                            )
                        )
                        tabViewChild.setTypeface(null, Typeface.BOLD)
                    }
                }

                val tv =
                    ((tabs.getChildAt(0) as LinearLayout).getChildAt(tab.position) as LinearLayout).getChildAt(
                        1
                    ) as TextView
                tv.textSize = 18f

                vgTab.setBackgroundResource(R.color.btn_skyblue)

            }
        })
    }

    private fun setToolbarUi() {
        // set click listener

        ivAddFav.setBackgroundResource(R.drawable.tool_heart)
        title_center_text_home_tool.text = "HOME"

        ivAddFav.setOnClickListener {
            mActivity.doFragmentTransaction(
                containerViewId = R.id.frame_layout,
                fragment = FavDealsFragment(),
                isAddToBackStack = true,
                isAddFragment = true
            )
        }

        right_image_home_tool.setOnClickListener {
            goToPickerActivity()
        }

        title_center_text_home_tool.setOnClickListener {

            mFirebaseRepository.makeSignOut()
            activity!!.finish()
            activity!!.startMyActivityWithNewTask(WelcomeActivity())
        }
        right_end_image_userhome_tool.setOnClickListener {

            mActivity.doFragmentTransaction(
                enterAnimation = R.animator.slide_up,
                popExitAnimation = R.animator.slide_down,
                containerViewId = R.id.frame_layout,
                fragment = SearchFragment()
            )
        }

        /**
         * not use here this method

         */
//        right_end_image_home_tool.setOnClickListener {
//
//            /**
//             * bundle
//             */
//
//            val bundle = Bundle()
//            bundle.putStringArrayList("dealList", dealId)
//            val frg = SearchFragment()
//            frg.arguments = bundle
//
//            mActivity.doFragmentTransaction(
//                enterAnimation = R.animator.slide_up,
//                popExitAnimation = R.animator.slide_down,
//                containerViewId = R.id.frame_layout,
//                fragment = frg
//            )
//        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = UserHomeTabAdapter(activity!!.supportFragmentManager)
//        adapter.addFragment(DealsFragment(), "Deals")
//        adapter.addFragment(HotDealsFragment(), "Hot Deals")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewpager)
//        viewPager.offscreenPageLimit = 2


        val vg = tabs.getChildAt(0) as ViewGroup
        val vgTab = vg.getChildAt(tabs.getTabAt(0)!!.position) as ViewGroup
        val tabChildsCount = vgTab.childCount
        for (i in 0 until tabChildsCount) {
            val tabViewChild = vgTab.getChildAt(i)
            if (tabViewChild is TextView) {
                tabViewChild.textSize = 18f
                tabViewChild.setTextColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.white
                    )
                )
                tabViewChild.setTypeface(null, Typeface.BOLD)
            }
        }
        val tv =
            ((tabs.getChildAt(0) as LinearLayout).getChildAt(tabs.getTabAt(0)!!.position) as LinearLayout).getChildAt(
                1
            ) as TextView
        tv.textSize = 18f

        vgTab.setBackgroundResource(R.color.btn_skyblue)


    }

    /**
     * can use later
     */
    fun refreshDealFragment() {
        mDealsFragment.refreshListData()
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
//        if (resultCode == Activity.RESULT_OK && requestCode == 5678) {
//
////            // Retrieve selected location's CarmenFeature
////            val selectedCarmenFeature: CarmenFeature = PlaceAutocomplete.getPlace(data)
////
////            val mLatitude = (selectedCarmenFeature.geometry() as Point).latitude()
////            val mLongitude = (selectedCarmenFeature.geometry() as Point).longitude()
//
//
//        }
    }
}
