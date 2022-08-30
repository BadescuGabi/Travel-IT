package com.bgabi.travelit.models

import java.io.Serializable

data class User(
    val uid: String? = null,
    val email: String? = null,
    var userName: String? = null,
    var description: String? = null,
    val followers: ArrayList<String> = ArrayList(),
    val following:  ArrayList<String> = ArrayList(),
    val travelHistory: ArrayList<String> = ArrayList(),
    val userPosts:  ArrayList<Post> = ArrayList(),
    val notifications: ArrayList<String> = ArrayList(),
    val admin: String? = null
) : Serializable