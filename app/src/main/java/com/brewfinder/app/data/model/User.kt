package com.brewfinder.app.data.model

import com.google.firebase.database.Exclude


class User(

        //user
        var id: String? = "",
        var user_name: String? = "",
        var user_email: String? = "",
        var user_pic: String? = "",
        var user_mobile: String = "",
        var user_type: String? = "",
        //business
        var user_location: String? = "",
        var company_name: String = "",
        var open_hours: String = "",
        var busines_logo: String = "",
        var latlng: String = "" // Business Company latlng

) {
    // toMap is used to Store Multipale data in List
    // when we add or get data we use tomap

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("mid", id!!)
        result.put("user_name", user_name!!)
        result.put("user_email", user_email!!)
        result.put("user_pic", user_pic!!)
        result.put("busines_logo", busines_logo)
//        result.put("email", email!!)
        result.put("latlng", latlng)
        result.put("user_mobile", user_mobile)
        result.put("user_type", user_type!!)
        result.put("company_name", company_name)
        result.put("open_hours", open_hours)

        return result
    }


}