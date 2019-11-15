package com.brewfinder.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.brewfinder.app.ui.base_screen.activity.welcome.WelcomeActivity


fun Fragment.hideKeyboard() {
    view?.let { activity!!.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

// toast comment
fun Context.toast(message: String?) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()


//Welcome is for Signup , Signin , Operations
fun Context.startWelcomeActivity() =
        Intent(this, WelcomeActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }


fun Context.startMyActivityWithNewTask(activity: Activity) =
        Intent(this, activity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

fun Context.startMyActivity(activity: Activity) = startActivity(Intent(this, activity::class.java))

//
//fun Context.startSignupActivity() =
//    Intent(this, SignupActivity::class.java).also {
//        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(it)
//    }
//
//fun Context.startAddDBActivity() =
//    Intent(this, PostActivity::class.java).also {
//        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivities(arrayOf(it))
//    }
//
//fun Context.startStorageActivity() =
//    Intent(this, StorageActivity::class.java).also {
//        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivities(arrayOf(it))
//    }
//
//fun Context.startUserListActivity() =
//    Intent(this, UserListActivity::class.java).also {
//        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivities(arrayOf(it))
//    }


