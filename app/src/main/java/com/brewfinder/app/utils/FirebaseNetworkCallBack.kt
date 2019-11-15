package com.brewfinder.app.utils

/**
 *@author Rashmi
 * 10/2/2019
 */
interface FirebaseNetworkCallBack {
    fun onSuccess(response: Any)
    fun onError(errorMessages: String)
}