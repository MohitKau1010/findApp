package com.brewfinder.app.ui.user.fragments.home_fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.brewfinder.app.ui.user.fragment.home_fragment.hot_deals.HotDealsFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.deals.DealsFragment


/**
 * @author Mohit
 * 11/10/19
 */
class UserHomeTabAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            DealsFragment()
        } else {
            HotDealsFragment()
        }
    }


    //This method sets our tabs title
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Deals"
            1 -> "Hot Deals"
            else -> null
        }
    }
}