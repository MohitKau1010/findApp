package com.brewfinder.app.data.model

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Deal(
    var deal_id: String? = "",
    var deal_user_name: String? = "",
    var deal_image: String? = "",
    var is_fav: String? = "",
    var deal_date: String? = "",
    var is_follow: String? = "",
    var message: String = "",
    var comments: String? = "",
    var createdAt: String? = "",
    var deal_time: String? = "",
    var description: String? = "",
    var discount_offered: String? = "",
    var deal_companyName: String? = "",
    var deal_company_logo: String? = "",
    var deal_latlng: String? = "",
    var redeem_code: String? = "",
    var deal_title: String? = ""

) : Serializable {


    // toMap is used to Store Multipale data in List
    // when we add or get data we use tomap

    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("comment_deal_id", deal_id!!)
        result.put("comment_deal_user_name", deal_user_name!!)
        result.put("commwnt_deal_image", deal_image!!)
        result.put("is_fav", is_fav!!)
        result.put("is_follow", is_follow!!)
        result.put("comments", comments!!)
        result.put("deal_time", deal_time!!)
        result.put("description", description!!)
        result.put("discount_offered", discount_offered!!)
        result.put("created_at", createdAt!!)
        result.put("latlng", deal_latlng!!)
        result.put("redeem_code", redeem_code!!)
        result.put("deal_title", deal_title!!)  // title is also called as deal name


        return result
    }


}