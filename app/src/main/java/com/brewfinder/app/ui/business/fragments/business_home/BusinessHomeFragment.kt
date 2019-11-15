package com.brewfinder.app.ui.business.fragments.business_home

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
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.brewfinder.app.R
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.activity.welcome.WelcomeActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.business.fragments.business_home.create_deals.CreateDealFragment
import com.brewfinder.app.ui.business.fragments.business_home.deal_near_me.DealNearMeViewModel
import com.brewfinder.app.ui.business.fragments.business_home.deal_near_me.DealsNearMeFragment
import com.brewfinder.app.ui.business.fragments.business_home.my_business_deals.MyBusinessDealsFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.fav_deals.FavDealsFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.search_fragment.SearchFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.showToast
import com.brewfinder.app.utils.startMyActivityWithNewTask
import com.google.android.material.tabs.TabLayout
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions
import kotlinx.android.synthetic.main.fragment_business_home.*
import kotlinx.android.synthetic.main.home_toolbar.*

/**
 *@author Rashmi
 * 10/15/2019
 */
class BusinessHomeFragment : BaseFragment() {

    override val mViewModel: BaseViewModel?
        get() = null
    private val mFireBaseRepository = FireBaseRepository()
    private val mDealsNearMeFragment by lazy { DealsNearMeFragment() }
    private val mMyBusinessDealsFragment by lazy { MyBusinessDealsFragment() }
    private var mDealsNearMeViewModel: DealNearMeViewModel? = null

    private var mLat: Double? = null
    private var mLong: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_business_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mDealsNearMeViewModel =
            ViewModelProviders.of(mActivity).get(DealNearMeViewModel::class.java)

        mDealsNearMeViewModel = ViewModelProviders.of(mActivity).get(DealNearMeViewModel::class.java)


        homebusiness_toolbar_id.visibility = View.VISIBLE

        setupViewPager(viewpager_business)
        setToolbarUi()


        // set click Listener
        title_center_text_home_tool.setOnClickListener {
            mFireBaseRepository.makeSignOut()
            mActivity.startMyActivityWithNewTask(WelcomeActivity())
            mActivity.finish()

        }

        // set TabSelected Listener
        tab_business.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            //setting white color when user changes from one tab to another
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val vg = tab_business.getChildAt(0) as ViewGroup
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
                        tabViewChild.setTypeface(null, Typeface.BOLD)

                    }
                }
                vgTab.setBackgroundResource(R.color.white)
            }


            //changing toolbar icons,text on change of tabLayouts
            override fun onTabSelected(tab: TabLayout.Tab?) {
                changeTabColor(tab)
                when (tab?.position) {
                    0 -> {
                        setDealListener()
                        updateToolbarIcon(R.drawable.ic_pin)
                        updateToolbarText("HOME")
                    }
                    1 -> {
                        setMyDealListener()
                        updateToolbarIcon(R.drawable.ic_add)
                        updateToolbarText("MY DEALS")
                    }
                }

            }
        })
    }

    //changing tabColor when user changes the tabs
    private fun changeTabColor(tab: TabLayout.Tab?) {
        val vg = tab_business.getChildAt(0) as ViewGroup
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
                ((tab_business.getChildAt(0) as LinearLayout).getChildAt(tab.position) as LinearLayout).getChildAt(
                        1
                ) as TextView
        tv.textSize = 18f

        vgTab.setBackgroundResource(R.color.btn_skyblue)
    }


    //adding 2 different fragments in tabLayout
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter =
                BusinessHomeTabAdapter(
                        activity!!.supportFragmentManager
                )
        adapter.addFragment(mDealsNearMeFragment, "Deals Near Me")
        adapter.addFragment(mMyBusinessDealsFragment, "My Deals")
        viewPager.adapter = adapter
        tab_business.setupWithViewPager(viewPager)

        changeTabColor(tab_business.getTabAt(0))

    }

    //setting logout on Text
    private fun setToolbarUi() {
        // set click listener

        //Search Icon
        img_location_home_tool.setOnClickListener {
            goToPickerActivity()

        }
        img_search_home_tool.setOnClickListener {
            mActivity.doFragmentTransaction(
                enterAnimation = R.animator.slide_up,
                popExitAnimation = R.animator.slide_down,
                containerViewId = R.id.frameLayoutBusiness,
                fragment = SearchFragment()
            )
        }
        // set click Listener
        title_center_text_home_tool.setOnClickListener {
            mFireBaseRepository.makeSignOut()
            mActivity.startMyActivityWithNewTask(WelcomeActivity())
            mActivity.finish()

        }
        img_add_fav_list.setOnClickListener {
            mActivity.doFragmentTransaction(
                containerViewId = R.id.frameLayoutBusiness,
                fragment = FavDealsFragment(),
                isAddToBackStack = true,
                isAddFragment = true
            )
        }
    }

    //changing icon with another icon
    fun updateToolbarIcon(icPin: Int) {
        img_location_home_tool.setImageDrawable(ContextCompat.getDrawable(mActivity, icPin))
    }

    //changing text with another text
    fun updateToolbarText(text: String) {
        title_center_text_home_tool.text = text
    }

    //setting listener on search icon
    fun setDealListener() {
        img_location_home_tool.setOnClickListener {
            showToast("pin")
        }
    }


    //opening new fragment on click search
    fun setMyDealListener() {
        img_location_home_tool.setOnClickListener {
            mActivity.doFragmentTransaction(
                    enterAnimation = R.animator.slide_up,
                    popExitAnimation = R.animator.slide_down,
                    containerViewId = R.id.frameLayoutBusiness,
                    fragment = CreateDealFragment()
            )
        }
    }


    //refreshing dealsNearMe list
    fun refreshDealsNearMe() {
        mDealsNearMeFragment.refreshListData()
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
        if (resultCode == Activity.RESULT_OK && requestCode == 5678) {

            // Retrieve selected location's CarmenFeature
//            val selectedCarmenFeature: CarmenFeature = PlaceAutocomplete.getPlace(data)

//            val mLatitude = (selectedCarmenFeature.geometry() as Point).latitude()
//            val mLongitude = (selectedCarmenFeature.geometry() as Point).longitude()

//            //selectedCarmenFeature.placeName()
//            text_address.text = "Current Location: " + selectedCarmenFeature.placeName()
//
//            val position = CameraPosition.Builder()
//                .target(LatLng(mLatitude, mLongitude))
//                .zoom(14.0)
//                .tilt(5.0)
//                .build()
//            mMapboxMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(position), 10)
        }
    }
}
