package com.bgabi.travelit.models

import java.io.Serializable

data class User(
    val uid: String? = null,
    val email: String? = null,
    val userName: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val followers: ArrayList<User> = ArrayList(),
    val following:  ArrayList<User> = ArrayList(),
    val travelHistory: String? = null,
    val favorites: String? = null,
    val futureTravel: String? = null,
    val userPosts:  ArrayList<Post> = ArrayList(),
    val isAdmin: String? = null
) : Serializable