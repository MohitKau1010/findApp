package com.brewfinder.app.ui.base_screen.fragment.business_signup


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.brewfinder.app.R
import com.brewfinder.app.databinding.FragmentBusinessUserSignupBinding
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.base_screen.fragment.login.LoginFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.example.mohitmvvmfirebase.utils.Utility.isValidEmail
import kotlinx.android.synthetic.main.fragment_business_user_signup.*


/**
 * @author Mohit
 * 28/9/19
 */
class BusinessUserSignupFragment : BaseFragment(), View.OnClickListener {
    override val mViewModel: BaseViewModel?
        get() = viewModel

    private lateinit var viewModel: BusinessSignupViewModel


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentBusinessUserSignupBinding =
                DataBindingUtil.inflate(
                        inflater,
                        R.layout.fragment_business_user_signup,
                        container,
                        false
                )

        viewModel = ViewModelProviders.of(activity!!).get(BusinessSignupViewModel::class.java)
        binding.viewmodel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_Uname_business.setText(" ")
        et_Bemail_business.setText(" ")
        et_phone_business.setText(" ")
        et_Bpassword_business.setText(" ")

        btn_busiSignup_next.setOnClickListener(this)
        go_to_signIn1.setOnClickListener(this)

        viewModel.clearInitailValues()
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_busiSignup_next -> {
                viewModel.emptyerror.value = false
                if (et_Uname_business.text.isNullOrEmpty()) {
                    et_Uname_business.requestFocus()
                    et_Uname_business.error = "Please fill name"
                } else if (et_Bemail_business.text.isNullOrEmpty()) {
                    et_Bemail_business.requestFocus()
                    et_Bemail_business.error = "Please fill email"
                } else if (et_phone_business.text.isNullOrEmpty() || et_phone_business.text.length > 15 || et_phone_business.text.length < 10) {
                    et_phone_business.requestFocus()
                    et_phone_business.error =
                            "Mobile Number must be minimum 10 & maximum 15 digit mobile number"
                } else if (et_Bpassword_business.text.isNullOrEmpty() || et_Bpassword_business.text.length < 6) {
                    et_Bpassword_business.requestFocus()
                    et_Bpassword_business.error = "Password must be at least of 6 characters"
                } else {
                    if (!isValidEmail(et_Bemail_business.text.trim())) {
                        et_Bemail_business.requestFocus()
                        et_Bemail_business.error = "Please enter valid email.."
                    } else {

                        (activity as BaseActivity).hideKeyboard(activity!!)

                        viewModel.name = et_Uname_business.text.trim().toString()
                        viewModel.email = et_Bemail_business.text.trim().toString()
                        viewModel.mobile = et_phone_business.text.trim().toString()

                        viewModel.emptyerror.value = false


                        (activity as BaseActivity).doFragmentTransaction(
                                activity?.supportFragmentManager!!,
                                R.id.flWelcomeContainer,
                                fragment = BusinessSetupSignupFragment()
                        )
                    }
                }

            }
            R.id.go_to_signIn1 -> {
                viewModel.clearInitailValues()


                //adding RegisterFragment and removing this Fragment
                mActivity.doFragmentTransaction(
                        containerViewId = R.id.flWelcomeContainer,
                        fragment = LoginFragment()
                )
                mActivity.supportFragmentManager.let {
                    it.beginTransaction().remove(it.fragments[1]).commit()
                }
            }
        }
    }


}
