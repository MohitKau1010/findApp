package com.brewfinder.app.ui.base_screen.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {


//    var baseprogress: ProgressDialog? = null
//
//
//    fun showProcessDialog(context: Context?, message: String?) {
//        baseprogress = ProgressDialog(context).apply {
//            setTitle("Loading..")
//            setCancelable(false)
//            setCanceledOnTouchOutside(false)
//            show()
//        }
//    }
//
//    fun hideProcessDialog() {
//        baseprogress?.dismiss()
//    }


    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideSoftKeyboard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}

fun AppCompatActivity.doFragmentTransaction(
        fragManager: FragmentManager = supportFragmentManager,
        @IdRes containerViewId: Int,
        fragment: Fragment,
        isAddFragment: Boolean = true,
        isAddToBackStack: Boolean = true,
        allowStateLoss: Boolean = false,
        @AnimatorRes enterAnimation: Int = 0,
        @AnimatorRes exitAnimation: Int = 0,
        @AnimatorRes popEnterAnimation: Int = 0,
        @AnimatorRes popExitAnimation: Int = 0
) {

    val fragmentTransaction = fragManager.beginTransaction()
            .setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)

    if (isAddFragment) {
        fragmentTransaction.add(containerViewId, fragment, fragment.javaClass.simpleName)
    } else {
        fragmentTransaction.replace(containerViewId, fragment, fragment.javaClass.simpleName)
    }
    if (isAddToBackStack) {
        fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
    }
    if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    } else {
        fragmentTransaction.commit()
    }
}



