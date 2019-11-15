package com.brewfinder.app.ui.user.fragments.user_profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.brewfinder.app.R

/**
 * @author Shubham
 * 16/10/19
 */
class BusinessProfileAsUserFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_profile_as_user, container, false)
    }


}
