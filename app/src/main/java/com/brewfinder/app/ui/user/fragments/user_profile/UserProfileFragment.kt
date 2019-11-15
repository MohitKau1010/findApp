package com.brewfinder.app.ui.user.fragments.user_profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brewfinder.app.R
import com.brewfinder.app.data.model.User
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.activity.welcome.WelcomeActivity
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.user.fragments.home_fragment.search_fragment.SearchFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.showToast
import com.brewfinder.app.utils.startMyActivityWithNewTask
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_user_home.*
import kotlinx.android.synthetic.main.fragment_user_home.title_center_text_home_tool
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.user_profile_toolbar.*

/**
 * @author Shubham
 * 16/10/19
 */
class UserProfileFragment : BaseFragment() {
    override val mViewModel: BaseViewModel?
        get() = null

    var userModel = User()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        userModel = PrefrenceUtil(mActivity).userProfile!!

        tv_user_name.text = userModel.user_name
        tv_user_address.text = userModel.user_location

        Glide.with(mActivity)
                .load(userModel.user_pic)
                .into(user_profile_image)




        setToolbar()
        onClick()


    }

    private fun setToolbar() {
        ic_chatIcon.setOnClickListener {
            //            goToPickerActivity()
            showToast("Coming Soon..")
        }


        img_search_home_tool.setOnClickListener {

            mActivity.doFragmentTransaction(
                enterAnimation = R.animator.slide_up,
                popExitAnimation = R.animator.slide_down,
                containerViewId = R.id.frame_layout,
                fragment = SearchFragment()
            )
        }
    }

    private fun onClick() {

        im_edit_user_profile.setOnClickListener {
            (activity as BaseActivity).doFragmentTransaction(
                    containerViewId = R.id.frame_layout, fragment = EditUserProfileFragment()
            )
        }
    }

}



