package com.brewfinder.app.ui.base_screen.fragment.register

import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.brewfinder.app.R
import com.brewfinder.app.data.model.User
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.agreement.UserAgreementActivity
import com.brewfinder.app.ui.base_screen.activity.doFragmentTransaction
import com.brewfinder.app.ui.base_screen.fragment.BaseFragment
import com.brewfinder.app.ui.base_screen.fragment.login.LoginFragment
import com.brewfinder.app.ui.viewModel.BaseViewModel
import com.brewfinder.app.utils.Constants
import com.brewfinder.app.utils.showToast
import com.brewfinder.app.utils.startMyActivity
import com.example.mohitmvvmfirebase.utils.Utility
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_register.*


/**
 *@author Rashmi
 * 10/1/2019
 */

class RegisterFragment : BaseFragment(), View.OnClickListener {
    override val mViewModel: BaseViewModel?
        get() = mRegisterModel

    private lateinit var mRegisterModel: RegisterViewModel
    private var mCurrentUser: FirebaseUser? = null      //current User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRegisterModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java) //initializing model

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachObserver()

        //setClickListener
        btnSignUp.setOnClickListener(this)
        tv_signIn_register.setOnClickListener(this)


        //for IME options to work
//        et_name_register.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                doLogin()
//            }
//            false
//        }
        //to check checkbox is checked or not
        checkBox.makeLinks(
                Pair("User Agreement", View.OnClickListener {
                    activity!!.startMyActivity(UserAgreementActivity())
                })
        )


    }


    private fun CheckBox.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this.text)
        for (link in links) {
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    Selection.setSelection((view as CheckBox).text as Spannable, 0)
                    view.invalidate()
                    link.second.onClick(view)
                }
            }
            val startIndexOfLink = this.text.toString().indexOf(link.first)
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
        this.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun doSignUp() {
        if (checkBox.isChecked) {
            val email = et_email_register.text.toString().trim()
            val password = et_pass_register.text.toString().trim()
            val phone = et_phone_register.text.toString().trim()
            val name = et_name_register.text.toString().trim()

            when {
                mCurrentUser != null -> showToast("${mCurrentUser?.email}is already logged in")
                name.isEmpty() -> {
                    et_name_register.requestFocus()
                    et_name_register.error = "Name field cannot be empty"
                }
                email.isEmpty() -> {
                    et_email_register.requestFocus()
                    et_email_register.error = "Email field cannot be empty"

                }
                !Utility.isValidEmail(email) -> {
                    et_email_register.requestFocus()
                    et_email_register.error = "Please enter a valid email"
                }


                phone.isEmpty() -> {
                    et_phone_register.requestFocus()
                    et_phone_register.error = "Phone number field cannot be empty"

                }
                phone.length > 15 || phone.length < 10 -> {
                    et_phone_register.requestFocus()
                    et_phone_register.error = " Phone number must contain 10 to 15 digits "

                }
                password.isEmpty() -> {
                    et_pass_register.requestFocus()
                    et_pass_register.error = "Password field cannot be empty"

                }
                password.length < 6 -> {
                    et_pass_register.requestFocus()
                    et_pass_register.error = "Password must contain at least of 6 characters"
                }

                else -> {
                    mRegisterModel.signUpUser(email, password)
                }
            }
        } else {
            showToast("Please check terms and conditions")
        }
    }

    override fun onClick(item: View?) {
        (activity as BaseActivity).hideKeyboard(activity!!)
        when (item?.id) {

            R.id.btnSignUp -> {
                doSignUp()
            }
            R.id.tv_signIn_register -> {
                //adding LoginFragment and removing this Fragment
                mActivity.doFragmentTransaction(containerViewId = R.id.flWelcomeContainer, fragment = LoginFragment())
                mActivity.supportFragmentManager.let {
                    it.beginTransaction().remove(it.fragments[1]).commit()
                }
            }
        }

    }

    private fun attachObserver() {


        mRegisterModel.userMutable.observe(this, Observer {
            mCurrentUser = it
            val user = User()        //User Model
            user.id = it.uid
            user.user_name = et_name_register.text.toString().trim()
            user.user_email = it.email
            user.user_mobile = et_phone_register.text.toString().trim()
            user.user_pic = " "
            user.user_type = Constants.USER_TYPE
            mRegisterModel.dataBaseUser(it.uid, user)

            mCurrentUser = it
            showToast("You have successfully created your Account")

            mActivity.doFragmentTransaction(containerViewId = R.id.flWelcomeContainer, fragment = LoginFragment())
            mActivity.supportFragmentManager.let { manager ->
                manager.beginTransaction().remove(manager.fragments[1]).commit()
            }
        })


        mRegisterModel.error.observe(this, Observer {

            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()

        })
        mRegisterModel.dataBase().observe(this, Observer {
            when (it) {
                true -> {
                    showToast("User Created Successfully")
                }
                false -> {
                    showToast("Something went wrong")
                }
            }
        })

    }

}