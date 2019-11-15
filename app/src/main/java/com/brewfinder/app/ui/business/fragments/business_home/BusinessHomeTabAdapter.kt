package com.brewfinder.app.ui.business.fragments.business_home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 *@author Rashmi
 * 11/8/2019
 */
class BusinessHomeTabAdapter(private val fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList.get(position)
    }
}