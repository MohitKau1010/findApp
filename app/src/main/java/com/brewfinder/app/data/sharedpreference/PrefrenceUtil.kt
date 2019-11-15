package com.brewfinder.app.data.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.brewfinder.app.data.model.User
import com.google.gson.Gson

/**
 * @author Mohit
 * 30/9/19
 */
class PrefrenceUtil(private val mContext: Context) {


    private val PREFERENCE_USER_MODEL = "USER_MODEL"
    private val PREFERENCE_USERID = "userId"
    private val PREFERENCE_CURRENT_LTLNG = "Current_latlng"


    private fun getSharedPrefrence(): SharedPreferences {
        return mContext.getSharedPreferences("BrewFinderApp", Context.MODE_PRIVATE)
    }

    fun clearPrefsData() {
        getSharedPrefrence().edit().clear().apply()
    }

    var userProfile: User?
        get() = Gson().fromJson(
                getSharedPrefrence().getString(PREFERENCE_USER_MODEL, "")!!,
                User::class.java
        )
        set(userProfile) {
            getSharedPrefrence().edit().putString(PREFERENCE_USER_MODEL, Gson().toJson(userProfile))
                    .apply()
        }

    var userId: String?
        get() = getSharedPrefrence().getString(PREFERENCE_USERID, "")
        set(userId) {
            getSharedPrefrence().edit().putString(PREFERENCE_USERID, userId).apply()
        }

    //gpsNetwork
    var curretLatLng: String?
        get() = getSharedPrefrence().getString(PREFERENCE_CURRENT_LTLNG, "")
        set(curretLatLng) {
            getSharedPrefrence().edit().putString(PREFERENCE_CURRENT_LTLNG, curretLatLng).apply()
        }


}