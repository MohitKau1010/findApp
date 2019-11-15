package com.brewfinder.app.ui.business.fragments.business_profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brewfinder.app.R
import com.brewfinder.app.data.model.User
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_business_profile.*


/**
 * @author Shubham
 * 16/10/19
 */
class BusinessProfileFragment : BaseFragment() {
    override val mViewModel: BaseViewModel?
        get() = null

    var userModel = User()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//
//        back_arrow_profile.setOnClickListener {
//            (activity as BaseActivity).doFragmentTransaction(
//                containerViewId = R.mid.frameLayoutBusiness, fragment = BusinessHomeFragment()
//            )
//        }


        ic_edit_icon.setOnClickListener {

            (activity as BaseActivity).doFragmentTransaction(
                    containerViewId = R.id.frameLayoutBusiness, fragment = EditBusinessProfileFragment()
            )
        }

        userModel = PrefrenceUtil(mActivity).userProfile!!

        tv_brewery_name.text = userModel.company_name


        Glide.with(mActivity)
                .load(userModel.busines_logo)
                .into(user_profile_image)

    }


}
