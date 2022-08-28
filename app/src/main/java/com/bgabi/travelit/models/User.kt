package com.bgabi.travelit.models

import java.io.Serializable

data class User(
    val uid: String? = null,
    val email: String? = null,
    var userName: String? = null,
    var description: String? = null,
    val followers: ArrayList<User> = ArrayList(),
    val following:  ArrayList<User> = ArrayList(),
    val travelHistory: String? = null,
    val favorites: String? = null,
    val futureTravel: String? = null,
    val userPosts:  ArrayList<Post> = ArrayList(),
    val notifications: ArrayList<String> = ArrayList(),
    val admin: String? = null
) : Serializable