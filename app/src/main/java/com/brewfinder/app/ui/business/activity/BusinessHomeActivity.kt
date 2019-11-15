package com.brewfinder.app.ui.business.activity

import android.os.Bundle
import android.view.View
import com.brewfinder.app.R
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.business.fragments.business_home.BusinessHomeFragment
import com.brewfinder.app.ui.business.fragments.business_profile.BusinessProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_business_home.*

/**
 *@author Rashmi
 * 10/8/2019
 */
class BusinessHomeActivity : BaseActivity() {


    private var active: BaseFragment? = null
    private val mBusinessHomeFrag = BusinessHomeFragment()
    private val mBusinessProfileFrag = BusinessProfileFragment()
//    private val mBusinessSettingFrag = BusinessSettingFragment()

    override fun onBackPressed() {
        //super.onBackPressed()
        supportFragmentManager.popBackStackImmediate()
    }

    fun toggleBottomNavigation(isShowBottomView: Boolean) {
        if (isShowBottomView) business_bottomNavigationView.visibility = View.VISIBLE
        else
            business_bottomNavigationView.visibility = View.GONE

    }
    

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.navigation_home -> {
                        supportFragmentManager.beginTransaction().hide(active!!).show(mBusinessHomeFrag)
                                .commit()
                        active = mBusinessHomeFrag
                    }
                    R.id.navigation_profile -> {

                        supportFragmentManager.beginTransaction().hide(active!!)
                                .show(mBusinessProfileFrag)
                                .commit()
                        active = mBusinessProfileFrag
                    }
                    R.id.navigation_setting -> {
//                    supportFragmentManager.beginTransaction().hide(active!!)
//                        .show(mBusinessSettingFrag)
//                        .commit()
//                    active = mBusinessSettingFrag
                    }
                }
                true
            }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_home)

        //bottom navigation view
        business_bottomNavigationView.setOnNavigationItemSelectedListener(
                mOnNavigationItemSelectedListener
        )

        // add fragments
        active = mBusinessHomeFrag

//        supportFragmentManager.beginTransaction()
//            .add(R.mid.frameLayoutBusiness, mBusinessSettingFrag, "Setting")
//            .hide(mBusinessSettingFrag).commit()

        supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutBusiness, mBusinessProfileFrag, "Profile")
                .hide(mBusinessProfileFrag).commit()

        supportFragmentManager.beginTransaction()
                .add(R.id.frameLayoutBusiness, mBusinessHomeFrag, "Home").commit()


    }


}