package com.brewfinder.app.data.model

import java.io.Serializable

/**
 *@author Rashmi
 * 11/8/2019
 */
class CommentSection(

        var createdAt: String = "",
        var comment: String = "",
        var commentUserProfile: String? = "",
        var commentUserName: String? = "",
        var commentUserId: String = "",
        var commentID: String = ""

) : Serializable