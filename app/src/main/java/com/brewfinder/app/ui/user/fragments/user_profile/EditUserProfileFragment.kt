package com.brewfinder.app.ui.user.fragments.user_profile


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
import kotlinx.android.synthetic.main.fragment_edit_business_profile.user_profile_image
import kotlinx.android.synthetic.main.fragment_edit_user_profile.*
import kotlinx.android.synthetic.main.user_profile_toolbar.*

/**
 * @author Shubham
 * 16/10/19
 */
class EditUserProfileFragment : BaseFragment() {
    override val mViewModel: BaseViewModel?
        get() = null

    var userModel = User()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userModel = PrefrenceUtil(mActivity).userProfile!!

        et_edit_user_name.setText(userModel.user_name)

        et_edit_user_email.setText(userModel.user_email)

        et_edit_user_phone.setText(userModel.user_mobile)

        et_edit_user_location.setText(userModel.open_hours)

        back_arrow_user_profile.setOnClickListener {
            activity!!.supportFragmentManager.popBackStackImmediate()
        }



        Glide.with(mActivity)
                .load(userModel.user_pic)
                .into(user_profile_image)


    }


}
