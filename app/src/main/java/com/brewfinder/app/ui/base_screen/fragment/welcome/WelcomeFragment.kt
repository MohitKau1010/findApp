package com.brewfinder.app.ui.base_screen.fragment.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brewfinder.app.R
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.base_screen.fragment.business_signup.BusinessUserSignupFragment
import com.brewfinder.app.ui.base_screen.fragment.login.LoginFragment
import com.brewfinder.app.ui.base_screen.fragment.register.RegisterFragment
import com.brewfinder.app.ui.base_screen.fragment.reset.ResetFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.fragment_welcome.*


/**
 * @author Shubham
 * 28/9/19
 */
class WelcomeFragment : BaseFragment(), View.OnClickListener {

    override val mViewModel: BaseViewModel?
        get() = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // set click listener
        btn_UserSignUp.setOnClickListener(this)
        btnResetPassword.setOnClickListener(this)
        btn_UserSignIn.setOnClickListener(this)
        btn_BusiSetUp.setOnClickListener(this)


    }

    override fun onClick(view: View?) {
        (activity as BaseActivity).hideKeyboard(activity!!)
        when (view?.id) {
            R.id.btn_UserSignUp -> {
                (activity as BaseActivity).doFragmentTransaction(
                        containerViewId = R.id.flWelcomeContainer, fragment = RegisterFragment())
            }
            R.id.btnResetPassword -> {
                (activity as BaseActivity).doFragmentTransaction(
                        activity?.supportFragmentManager!!,
                        R.id.flWelcomeContainer,
                        ResetFragment(),
                        isAddFragment = true,
                        isAddToBackStack = true,
                        allowStateLoss = false
                )
            }
            R.id.btn_UserSignIn -> {
                (activity as BaseActivity).doFragmentTransaction(
                        containerViewId = R.id.flWelcomeContainer, fragment = LoginFragment())
            }
            R.id.btn_BusiSetUp -> {
                (activity as BaseActivity).doFragmentTransaction(
                        containerViewId = R.id.flWelcomeContainer, fragment = BusinessUserSignupFragment())
            }
        }
    }
}
