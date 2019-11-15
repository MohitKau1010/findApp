package com.brewfinder.app.ui.base_screen.activity.splash

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.os.Handler
import com.brewfinder.app.R
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.ui.base_screen.activity.BaseActivity
import com.brewfinder.app.ui.base_screen.activity.welcome.WelcomeActivity
import com.brewfinder.app.ui.business.activity.BusinessHomeActivity
import com.brewfinder.app.ui.user.activity.HomeUserActivity
import com.brewfinder.app.utils.Constants
import com.brewfinder.app.utils.startMyActivityWithNewTask

/**
 * @author Shubham
 * 28/9/19
 */

private const val PERMISSION_REQUEST = 10

@TargetApi(Build.VERSION_CODES.M)
class SplashActivity : BaseActivity() {


    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 2000 //2 seconds


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        //Initializing the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }

    private val mRunnable: Runnable = Runnable {

        if (PrefrenceUtil(this).userId.isNullOrEmpty()) {
            startMyActivityWithNewTask(WelcomeActivity())
            finish()
        } else {
            if (PrefrenceUtil(this).userProfile?.user_type == Constants.USER_TYPE) {
                startMyActivityWithNewTask(HomeUserActivity())
                finish()
            } else {
                startMyActivityWithNewTask(BusinessHomeActivity())
                finish()
            }
        }

    }


}
