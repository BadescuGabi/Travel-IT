package com.bgabi.travelit.models

data class User(
    val uid: String? = null,
    val email: String? = null,
    val userName: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val followers: User? = null,
    val following: User? = null,
    val travelHistory: String? = null,
    val favorites: String? = null,
    val futureTravel: String? = null,
    val userPosts: Post? = null,
    val isAdmin: String? = null
) {
}