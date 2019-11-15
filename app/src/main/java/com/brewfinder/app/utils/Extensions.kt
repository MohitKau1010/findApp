package com.brewfinder.app.utils

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.brewfinder.app.BrewFinder

/**
 *@author Rashmi
 * 11/15/2019
 */


fun Activity.showToast(message: String = "", resId: Int = 0) {
    Toast.makeText(BrewFinder.applicationContext(), if (message.isEmpty()) getString(resId) else message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(message: String = "", resId: Int = 0) {
    Toast.makeText(BrewFinder.applicationContext(), if (message.isEmpty()) getString(resId) else message, Toast.LENGTH_LONG).show()
}
