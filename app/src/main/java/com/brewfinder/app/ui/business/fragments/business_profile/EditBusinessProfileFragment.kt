package com.brewfinder.app.ui.business.fragments.business_profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brewfinder.app.R
import com.brewfinder.app.data.model.User
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.business_profile_toolbar.*
import kotlinx.android.synthetic.main.fragment_business_profile.user_profile_image
import kotlinx.android.synthetic.main.fragment_edit_business_profile.*

/**
 * @author Shubham
 * 16/10/19
 */
class EditBusinessProfileFragment : BaseFragment() {
    override val mViewModel: BaseViewModel?
        get() = null

    var userModel = User()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_business_profile, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userModel = PrefrenceUtil(mActivity).userProfile!!

        tv_edit_brewery_name.text = userModel.company_name

        tv_edit_business_profile_address.text = userModel.user_location

        et_business_email.setText(userModel.user_email)

        et_business_phone.setText(userModel.user_mobile)

        et_business_operation_hours.setText(userModel.open_hours)

        back_arrow_profile.setOnClickListener {
            activity!!.supportFragmentManager.popBackStackImmediate()
        }



        Glide.with(mActivity)
                .load(userModel.busines_logo)
                .into(user_profile_image)


    }


}
