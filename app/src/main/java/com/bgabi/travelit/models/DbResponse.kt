package com.bgabi.travelit.models

import android.location.Location


data class DbResponse(
    var users: List<User>? = null,
    var posts: List<Post>? = null,
    var comments: List<Comment>? = null,
    var exception: Exception? = null
)