package com.bgabi.travelit.models

data class User(
    val userName: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val description: String? = null,
    val followers: User? = null,
    val following: User? = null,
    val userPosts: Post? = null
) {
}