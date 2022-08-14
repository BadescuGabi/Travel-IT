package com.bgabi.travelit.models

import java.io.Serializable


data class Post     // creating a constructor class.
    (val authorImage: String? = null,
    val authorName: String? = null,
    val postDate: String? = null,
    val postDescription: String? = null,
    val postIV: String? = null,
    val postLikes: String? = null,
    val postComments: String? = null): Serializable
