package com.bgabi.travelit.models

import java.io.Serializable


data class Post     // creating a constructor class.
    (val postId : String? = null,
     val postUser: String? = null,
     val postLocation: String? = null,
     val postDate: String? = null,
     val postDescription: String? = null,
     val postLikes: ArrayList<String> = ArrayList(),
     val comments: ArrayList<Comment> = ArrayList(),
     val postReports: String? = null): Serializable
