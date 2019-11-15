package com.brewfinder.app.ui.base_screen.fragment.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.brewfinder.app.R
import com.brewfinder.app.data.repository.FireBaseRepository
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.base_screen.fragment.register.RegisterFragment
import com.brewfinder.app.ui.base_screen.fragment.reset.ResetFragment
import com.brewfinder.app.ui.business.activity.BusinessHomeActivity
import com.brewfinder.app.ui.user.activity.HomeUserActivity
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.Constants
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import com.brewfinder.app.utils.showToast
import com.brewfinder.app.utils.startMyActivityWithNewTask
import com.example.mohitmvvmfirebase.utils.Utility
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_login.*


/**
 *@author Rashmi
 * 9/28/2019
 */

class LoginFragment : BaseFragment(), View.OnClickListener {
    override val mViewModel: BaseViewModel?
        get() = mLoginModel            // loader

    private lateinit var mLoginModel: LoginViewModel
    private var mCurrentUser: FirebaseUser? = null
    private var mFireBaseRepository = FireBaseRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLoginModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachObserver()

        //setClickListener
        btnSignIn.setOnClickListener(this)
        tv_forgot_pass.setOnClickListener(this)
        tv_signUp.setOnClickListener(this)

//        et_pass_login.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                doLogin()
//            }
//            false
//        }

    }

    override fun onClick(item: View?) {
        (activity as BaseActivity).hideKeyboard(activity!!)
        when (item?.id) {
            R.id.btnSignIn -> {
                if (Utility.isNetworkAvailable(mActivity)) doLogin()
                else showToast(resId = R.string.no_internet_toast)
            }
            R.id.tv_forgot_pass -> {
                (activity as BaseActivity).doFragmentTransaction(
                        activity?.supportFragmentManager!!, R.id.flWelcomeContainer,
                        enterAnimation = R.animator.slide_up,
                        popExitAnimation = R.animator.slide_down,
                        fragment = ResetFragment(), isAddFragment = false, isAddToBackStack = true
                )
            }
            R.id.tv_signUp -> {
                //adding RegisterFragment and removing this Fragment
                mActivity.doFragmentTransaction(
                        containerViewId = R.id.flWelcomeContainer,
                        fragment = RegisterFragment()
                )
                mActivity.supportFragmentManager.let {
                    it.beginTransaction().remove(it.fragments[1]).commit()
                }
            }
        }
    }


    private fun doLogin() {
        val email = et_email_login.text.toString().trim()
        val password = et_pass_login.text.toString().trim()


        //if email is valid or not
        when {
//            mCurrentUser != null ->
            // fetch user data into database
            ///activity?.startMyActivityWithNewTask(BusinessHomeActivity())

            email.isEmpty() -> {
                et_email_login.requestFocus()
                et_email_login.error = "Please fill your email"
            }
            !Utility.isValidEmail(email) -> {
                et_email_login.requestFocus()
                et_email_login.error = "Please enter valid email"

            }
            password.isEmpty() -> {
                et_pass_login.requestFocus()
                et_pass_login.error = "Please fill your password"

            }
            password.length < 6 -> {
                et_pass_login.requestFocus()
                et_pass_login.error = "Password must contain at least 6 characters"
            }
            else -> {
                FirebaseApp.initializeApp(activity!!)
                mLoginModel.signInUser(email, password)
            }
        }
    }


    private fun attachObserver() {
        mLoginModel.userMutable.observe(this, Observer {
            mCurrentUser = it


            // fetch user data into database
            mFireBaseRepository.fetchUserData(it.uid, object : FirebaseNetworkCallBack {

                override fun onSuccess(response: Any) {

                    ///////////////////////////////////////
                    when (response) {
                        "null" -> // delete this user
                            showToast("User Type Null Found")
                        Constants.USER_TYPE -> {
                            activity?.startMyActivityWithNewTask(HomeUserActivity())
                            activity?.finish()
                        }
                        Constants.BUSINESS_TYPE -> {
                            activity?.startMyActivityWithNewTask(BusinessHomeActivity())
                            activity?.finish()
                        }
                    }
                }

                override fun onError(errorMessages: String) {
                    showToast(errorMessages)
                }
            })

            //check here Fetch database User Check client type =
            //activity?.startMyActivityWithNewTask(BusinessHomeActivity())

        })

        mLoginModel.onLoggedIn().observe(this, Observer {
            mCurrentUser = it
            showToast("You have successfully Logged In")

        })

        mLoginModel.error.observe(this, Observer {
            Toast.makeText(activity, "Invalid credentials", Toast.LENGTH_SHORT).show()

        })

    }
}
