package com.bgabi.travelit.models

import java.io.Serializable


data class Post     // creating a constructor class.
    (val postId : String? = null,
     val postUser: String? = null,
     val postLocation: String? = null,
     val postDate: String? = null,
     val postDescription: String? = null,
     val postLikes: String? = null,
     val postComments: String? = null,
     val postReports: String? = null): Serializable
